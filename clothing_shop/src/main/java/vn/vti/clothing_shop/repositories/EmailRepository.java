//package vn.vti.clothing_shop.repositories;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import vn.vti.clothing_shop.entities.Email;
//
//import java.util.List;
//
//@Repository
//public interface EmailRepository extends JpaRepository<Email, Long> {
//    List<Email> findAllByDeletedAtIsNullOrderByIdDesc();
//
//    Email findByEmail(String email);
//}
