package springboot.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.jpa.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
}
