package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.entities.Email;

import java.util.List;

public interface EmailService {
    void sendEmail(String email, String subject, String message);

    Email getEmailByEmail(String email);

    List<Email> getAllEmails();

    Boolean addEmail(Email email);

    Boolean updateEmail(Email email, Long id);

    Boolean deleteEmail(Long id);
}
