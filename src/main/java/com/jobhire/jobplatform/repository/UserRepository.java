package com.jobhire.jobplatform.repository;
import org.springframework.data.jpa.repository.JpaRepository;
   
import com.jobhire.jobplatform.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}