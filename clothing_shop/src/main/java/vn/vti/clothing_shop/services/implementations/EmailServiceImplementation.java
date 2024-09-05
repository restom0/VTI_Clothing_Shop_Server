package vn.vti.clothing_shop.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.entities.Email;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.repositories.EmailRepository;
import vn.vti.clothing_shop.services.interfaces.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class EmailServiceImplementation implements EmailService {
    @Autowired
    private EmailRepository emailRepository;

    public EmailServiceImplementation(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }
    public void sendEmail(String email, String subject, String message){

    };
    public Email getEmailByEmail(String email){
        try{
            Email result = emailRepository.findByEmail(email);
            if(result == null){
                throw new NotFoundException("Email not found");
            }
            return result;
        }
        catch (Exception e){
            throw new InternalServerErrorException("Server error");
        }
    };
    public List<Email> getAllEmails(){
        try{
            return emailRepository.findAll();
        }
        catch (Exception e){
            throw new InternalServerErrorException("Server error");
        }
    };
    public Boolean addEmail(Email email){
        try{
            emailRepository.save(email);
            return true;
        }
        catch (Exception e){
            throw new InternalServerErrorException("Server error");
        }
    };
    public Boolean updateEmail(Email email,Long id){
        try {
            Optional<Email> emailOptional = emailRepository.findById(id);
            if (emailOptional.isEmpty()) {
                throw new NotFoundException("Email not found");
            }
            Email email1 = emailOptional.get();
            email1.setEmail(email.getEmail());
            emailRepository.save(email1);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Server error");
        }
    };
    public Boolean deleteEmail(Long id){
        try {
            Optional<Email> emailOptional = emailRepository.findById(id);
            if (emailOptional.isEmpty()) {
                throw new NotFoundException("Email not found");
            }
            Email email1 = emailOptional.get();
            email1.setDeleted_at(LocalDateTime.now());
            emailRepository.save(email1);
            return true;
        } catch (Exception e) {
            throw new InternalServerErrorException("Server error");
        }
    };
}
