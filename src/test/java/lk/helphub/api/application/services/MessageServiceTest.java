package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.MessageRequest;
import lk.helphub.api.application.dto.MessageResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.impl.MessageServiceImpl;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.Message;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.repository.BidRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.MessageRepository;
import lk.helphub.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void testSendMessage_Success() {
        UUID jobId = UUID.randomUUID();
        String email = "poster@example.com";
        Job job = Job.builder().id(jobId).postedBy(User.builder().email(email).build()).build();
        User sender = User.builder().firstName("Admin").lastName("User").email(email).build();
        MessageRequest request = new MessageRequest("Hello", null, null, null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        MessageResponse result = messageService.sendMessage(jobId, email, request);

        assertNotNull(result);
        assertEquals("Hello", result.getContent());
    }

    @Test
    void testAcceptSuggestion_Success() {
        UUID jobId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        String posterEmail = "poster@example.com";
        User poster = User.builder().email(posterEmail).build();
        Job job = Job.builder().id(jobId).postedBy(poster).build();
        Message message = Message.builder()
                .id(messageId)
                .job(job)
                .suggestedPrice(new BigDecimal("4000.00"))
                .suggestionStatus("PENDING")
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        JobResponse result = messageService.acceptSuggestion(jobId, messageId, posterEmail);

        assertEquals(new BigDecimal("4000.00"), job.getPrice());
        assertEquals("ACCEPTED", message.getSuggestionStatus());
    }
}
