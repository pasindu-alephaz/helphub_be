package lk.helphub.api.application.services;

import lk.helphub.api.application.dto.MessageRequest;
import lk.helphub.api.application.dto.MessageResponse;
import lk.helphub.api.application.dto.JobResponse;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse sendMessage(UUID jobId, String userEmail, MessageRequest request);
    List<MessageResponse> getMessages(UUID jobId, String userEmail);
    JobResponse acceptSuggestion(UUID jobId, UUID messageId, String userEmail);
}
