package lk.helphub.api.application;

public interface MailService {
    void sendMail(String to, String subject, String body);
}
