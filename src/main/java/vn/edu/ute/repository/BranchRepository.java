package vn.edu.ute.repository;

import vn.edu.ute.model.Branch;
import java.util.List;

public interface BranchRepository {
    List<Branch> findAll();

    Branch findById(Long id);

    Branch save(Branch branch); // Sử dụng cho cả thêm và sửa (em.persist)

    boolean delete(Long id);
}
