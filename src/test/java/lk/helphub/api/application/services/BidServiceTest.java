package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.BidRequest;
import lk.helphub.api.application.dto.BidResponse;
import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.services.impl.BidServiceImpl;
import lk.helphub.api.domain.entity.Bid;
import lk.helphub.api.domain.entity.Job;
import lk.helphub.api.domain.entity.User;
import lk.helphub.api.domain.exception.ResourceNotFoundException;
import lk.helphub.api.domain.repository.BidRepository;
import lk.helphub.api.domain.repository.JobRepository;
import lk.helphub.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest {

    @Mock
    private BidRepository bidRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BidServiceImpl bidService;

    @Test
    void testSubmitBid_Success() {
        UUID jobId = UUID.randomUUID();
        String providerEmail = "provider@example.com";
        Job job = Job.builder().id(jobId).status("OPEN").postedBy(User.builder().email("poster@example.com").build()).build();
        User provider = User.builder().id(UUID.randomUUID()).email(providerEmail).firstName("John").lastName("Doe").build();
        BidRequest request = new BidRequest(new BigDecimal("5000.00"), "Proposal", null);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByEmail(providerEmail)).thenReturn(Optional.of(provider));
        when(bidRepository.findByJobIdAndProviderId(any(), any())).thenReturn(Optional.empty());
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));

        BidResponse result = bidService.submitBid(jobId, providerEmail, request);

        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getAmount());
        verify(bidRepository, times(1)).save(any());
    }

    @Test
    void testAcceptBid_Success() {
        UUID jobId = UUID.randomUUID();
        UUID bidId = UUID.randomUUID();
        String posterEmail = "poster@example.com";
        User poster = User.builder().email(posterEmail).build();
        User provider = User.builder().id(UUID.randomUUID()).build();
        Job job = Job.builder().id(jobId).status("OPEN").postedBy(poster).build();
        Bid bid = Bid.builder().id(bidId).job(job).provider(provider).amount(new BigDecimal("4500.00")).status("PENDING").build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(bidRepository.findById(bidId)).thenReturn(Optional.of(bid));
        when(bidRepository.findByJobIdOrderByAmountAsc(jobId)).thenReturn(List.of(bid));
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArgument(0));

        JobResponse result = bidService.acceptBid(jobId, bidId, posterEmail);

        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("ACCEPTED", bid.getStatus());
        assertEquals(new BigDecimal("4500.00"), job.getPrice());
    }
}
