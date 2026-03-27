package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.JobResponse;
import lk.helphub.api.application.dto.PauseJobRequest;
import lk.helphub.api.application.dto.TimeSummaryResponse;
import lk.helphub.api.application.dto.VerifyJobOtpRequest;

import java.util.UUID;

public interface JobTimeSessionService {
    void requestStartOtp(UUID jobId, String userEmail);
    JobResponse verifyStartOtp(UUID jobId, String userEmail, VerifyJobOtpRequest request);
    JobResponse pauseJob(UUID jobId, String userEmail, PauseJobRequest request);
    void requestResumeOtp(UUID jobId, String userEmail);
    JobResponse verifyResumeOtp(UUID jobId, String userEmail, VerifyJobOtpRequest request);
    TimeSummaryResponse getTimeSummary(UUID jobId);
}
