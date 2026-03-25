package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.dto.MessageRequest;
import lk.helphub.api.application.dto.MessageResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.MessageService;
import lk.helphub.api.domain.entity.Bid;
import lk.helphub.api.domain.entity.Image;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.Message;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.BidRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.MessageRepository;
import lk.helphub.api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    @Override
    public MessageResponse sendMessage(UUID jobId, String userEmail, MessageRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        User sender = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // Authorization: Poster, Accepted Provider, or a Bidder can send messages
        boolean isPoster = job.getPostedBy().getEmail().equals(userEmail);
        boolean isAcceptedProvider = job.getAcceptedBy() != null && job.getAcceptedBy().getEmail().equals(userEmail);
        boolean isBidder = bidRepository.findByJobIdAndProviderEmail(jobId, userEmail).isPresent();

        if (!isPoster && !isAcceptedProvider && !isBidder) {
            throw new RuntimeException("You are not authorized to send messages for this job");
        }

        Message message = Message.builder()
                .job(job)
                .sender(sender)
                .content(request.getContent())
                .suggestedPrice(request.getSuggestedPrice())
                .suggestedScheduledAt(request.getSuggestedScheduledAt())
                .suggestedAvailabilityDuration(request.getSuggestedAvailabilityDuration())
                .suggestionStatus(request.getSuggestedPrice() != null || request.getSuggestedScheduledAt() != null || request.getSuggestedAvailabilityDuration() != null ? "PENDING" : null)
                .build();

        Message savedMessage = messageRepository.save(message);
        return mapToMessageResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID jobId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Authorization check similar to sendMessage
        boolean isPoster = job.getPostedBy().getEmail().equals(userEmail);
        boolean isAcceptedProvider = job.getAcceptedBy() != null && job.getAcceptedBy().getEmail().equals(userEmail);
        boolean isBidder = bidRepository.findByJobIdAndProviderEmail(jobId, userEmail).isPresent();

        if (!isPoster && !isAcceptedProvider && !isBidder) {
             throw new RuntimeException("You are not authorized to view messages for this job");
        }

        return messageRepository.findByJobIdOrderByCreatedAtAsc(jobId)
                .stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JobResponse acceptSuggestion(UUID jobId, UUID messageId, String userEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(userEmail)) {
            throw new RuntimeException("Only the job poster can accept suggestions");
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!message.getJob().getId().equals(jobId)) {
            throw new RuntimeException("Message does not belong to this job");
        }

        if (!"PENDING".equals(message.getSuggestionStatus())) {
            throw new RuntimeException("No pending suggestion found in this message");
        }

        // Apply suggestions
        if (message.getSuggestedPrice() != null) {
            job.setPrice(message.getSuggestedPrice());
        }
        if (message.getSuggestedScheduledAt() != null) {
            job.setScheduledAt(message.getSuggestedScheduledAt());
        }
        if (message.getSuggestedAvailabilityDuration() != null) {
            job.setJobAvailabilityDuration(message.getSuggestedAvailabilityDuration());
        }

        message.setSuggestionStatus("ACCEPTED");
        messageRepository.save(message);
        Job savedJob = jobRepository.save(job);

        return mapToJobResponse(savedJob);
    }

    private MessageResponse mapToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .jobId(message.getJob().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .content(message.getContent())
                .suggestedPrice(message.getSuggestedPrice())
                .suggestedScheduledAt(message.getSuggestedScheduledAt())
                .suggestedAvailabilityDuration(message.getSuggestedAvailabilityDuration())
                .suggestionStatus(message.getSuggestionStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private JobResponse mapToJobResponse(Job job) {
        List<String> imageUrls = new ArrayList<>();
        if (job.getImages() != null) {
            imageUrls = job.getImages().stream().map(Image::getUrl).collect(Collectors.toList());
        }

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .subcategoryId(job.getSubcategory() != null ? job.getSubcategory().getId() : null)
                .locationAddress(job.getLocationAddress())
                .locationCoordinates(job.getLocationCoordinates() != null ? job.getLocationCoordinates().toText() : null)
                .price(job.getPrice())
                .scheduledAt(job.getScheduledAt())
                .jobType(job.getJobType())
                .preferredPrice(job.getPreferredPrice())
                .urgencyFlag(job.getUrgencyFlag())
                .status(job.getStatus())
                .postedBy(job.getPostedBy() != null ? job.getPostedBy().getId() : null)
                .acceptedBy(job.getAcceptedBy() != null ? job.getAcceptedBy().getId() : null)
                .imageUrls(imageUrls)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
