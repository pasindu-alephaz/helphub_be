package lk.helphub.api.application.services;

public interface MailService {
    void sendMail(String to, String subject, String body);
}
