//package vn.vti.clothing_shop.services.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import vn.vti.clothing_shop.entities.Email;
//import vn.vti.clothing_shop.exceptions.NotFoundException;
//import vn.vti.clothing_shop.repositories.EmailRepository;
//import vn.vti.clothing_shop.services.interfaces.EmailService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class EmailServiceImpl implements EmailService {
//    @Autowired
//    private EmailRepository emailRepository;
//
//    public EmailServiceImpl(EmailRepository emailRepository) {
//        emailRepository = emailRepository;
//    }
//    public void sendEmail(String email, String subject, String message){
//
//    };
//    public Email getEmailByEmail(String email){
//        try{
//            Email result = emailRepository.findByEmail(email);
//            if(result == null){
//                throw new NotFoundException("Email not found");
//            }
//            return result;
//        }
//        catch (WrapperException e){
//            throw new InternalServerErrorException("Server error");
//        }
//    };
//    public List<Email> getAllEmails(){
//        try{
//            return emailRepository.findAll();
//        }
//        catch (WrapperException e){
//            throw new InternalServerErrorException("Server error");
//        }
//    };
//    public Boolean addEmail(Email email){
//        try{
//            emailRepository.save(email);
//            return true;
//        }
//        catch (WrapperException e){
//            throw new InternalServerErrorException("Server error");
//        }
//    };
//    public Boolean updateEmail(Email email,Long id){
//        try {
//            Optional<Email> emailOptional = emailRepository.findById(id);
//            if (emailOptional.isEmpty()) {
//                throw new NotFoundException("Email not found");
//            }
//            Email email1 = emailOptional.get();
//            email1.setEmail(email.getEmail());
//            emailRepository.save(email1);
//            return true;
//        } catch (WrapperException e) {
//            throw new InternalServerErrorException("Server error");
//        }
//    };
//    public Boolean deleteEmail(Long id){
//        try {
//            Optional<Email> emailOptional = emailRepository.findById(id);
//            if (emailOptional.isEmpty()) {
//                throw new NotFoundException("Email not found");
//            }
//            Email email1 = emailOptional.get();
//            email1.setdeletedAt(Instant.now().toEpochMilli());
//            emailRepository.save(email1);
//            return true;
//        } catch (WrapperException e) {
//            throw new InternalServerErrorException("Server error");
//        }
//    };
//}
