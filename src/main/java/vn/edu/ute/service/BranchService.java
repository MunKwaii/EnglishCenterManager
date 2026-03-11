package vn.edu.ute.service;

import vn.edu.ute.model.Branch;
import java.util.List;

public interface BranchService {
    List<Branch> getAllBranches();
    List<Branch> getActiveBranches();
    Branch addBranch(Branch branch);
    Branch updateBranch(Branch branch);
    boolean deleteBranch(Long id);
    List<Branch> searchBranchByName(String keyword);
}
