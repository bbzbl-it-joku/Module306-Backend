package ch.bbzbl_it.module_306_backend.repository;

import ch.bbzbl_it.module_306_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByUsername(String username);
}
