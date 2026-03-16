package vn.edu.ute.service.impl;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repository.UserAccountRepository;
import vn.edu.ute.repository.impl.UserAccountRepositoryImpl;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.util.PasswordUtil;
import vn.edu.ute.util.UserSession;
import java.util.List;
import java.util.stream.Collectors;

public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository repo = new UserAccountRepositoryImpl();

    @Override
    public boolean login(String username, String password) {
        return repo.findByUsername(username)
                .filter(u -> PasswordUtil.check(password, u.getPasswordHash())) // Gọi BCrypt checkpw ở đây
                .map(u -> {
                    UserSession.login(u);
                    return true;
                }).orElse(false);
    }

    // --- HÀM THÊM MỚI: Có check trùng và mã hóa ---
    @Override
    public void addAccount(UserAccount account) throws Exception {
        if (isUsernameExists(account.getUsername())) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }
        // Bắt buộc mã hóa mật khẩu khi tạo mới
        account.setPasswordHash(PasswordUtil.hash(account.getPasswordHash()));
        repo.save(account);
    }

    // --- HÀM CẬP NHẬT: Xử lý logic an toàn ---
    @Override
    public void updateAccount(UserAccount account) throws Exception {
        UserAccount existing = repo.findById(account.getUserId())
                .orElseThrow(() -> new Exception("Tài khoản không tồn tại!"));

        // Logic: Nếu mật khẩu gửi lên khác với mật khẩu cũ trong DB -> người dùng vừa đổi pass -> băm tiếp
        if (!account.getPasswordHash().equals(existing.getPasswordHash())) {
            account.setPasswordHash(PasswordUtil.hash(account.getPasswordHash()));
        }

        repo.save(account);
    }

    @Override
    public void deleteAccount(Long id) throws Exception {
        repo.deleteById(id);
    }

    // --- CÁC HÀM LAMBDA  ---
    @Override
    public List<UserAccount> filterByRole(vn.edu.ute.model.enums.UserRole role) {
        return repo.findAll().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUsernameExists(String username) {
        return repo.findAll().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public List<UserAccount> getAllAccounts() {
        return repo.findAll();
    }

    @Override
    public List<UserAccount> searchByUsername(String keyword) {
        return repo.findAll().stream()
                .filter(u -> u.getUsername().contains(keyword))
                .toList();
    }

    @Override
    public long countActiveAccounts() {
        return repo.findAll().stream().filter(UserAccount::getIsActive).count();
    }
}