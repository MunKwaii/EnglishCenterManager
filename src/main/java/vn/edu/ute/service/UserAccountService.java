package vn.edu.ute.service;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.model.enums.UserRole;
import java.util.List;

public interface UserAccountService {
    // --- Authentication ---
    boolean login(String username, String password);

    // --- RUD Validate ---
    void addAccount(UserAccount account) throws Exception;
    void updateAccount(UserAccount account) throws Exception;
    void deleteAccount(Long id) throws Exception;

    // --- Các hàm truy vấn Lambda (Giữ nguyên) ---
    List<UserAccount> filterByRole(UserRole role);
    List<UserAccount> searchByUsername(String keyword);
    long countActiveAccounts();
    boolean isUsernameExists(String username);
    List<UserAccount> getAllAccounts();
}