package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.entities.Email;

import java.util.List;
import java.util.Optional;

public interface EmailService {
    public void sendEmail(String email, String subject, String message);
    public Email getEmailByEmail(String email);
    public List<Email> getAllEmails();
    public Boolean addEmail(Email email);
    public Boolean updateEmail(Email email,Long id);
    public Boolean deleteEmail(Long id);
}
