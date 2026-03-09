package vn.edu.ute.repository;

import vn.edu.ute.model.UserAccount;
import java.util.List;
import java.util.Optional;

public interface UserAccountRepository {
    void save(UserAccount account);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findById(Long id);
    List<UserAccount> findAll();
    void deleteById(Long id);
}