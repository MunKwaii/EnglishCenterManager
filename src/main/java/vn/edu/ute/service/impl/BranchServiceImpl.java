package vn.edu.ute.service.impl;

import vn.edu.ute.model.Branch;
import vn.edu.ute.repository.BranchRepository;
import vn.edu.ute.repository.impl.BranchRepositoryImpl;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.model.enums.Status;

import java.util.List;
import java.util.stream.Collectors;

public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepo = new BranchRepositoryImpl();

    @Override
    public List<Branch> getAllBranches() {
        return branchRepo.findAll();
    }

    @Override
    public List<Branch> getActiveBranches() {
        List<Branch> allBranches = branchRepo.findAll();
        if (allBranches == null) {
            return List.of();
        }
        return allBranches.stream()
                .filter(b -> b.getStatus() == Status.Active)
                .collect(Collectors.toList());
    }

    @Override
    public Branch addBranch(Branch branch) {
        return branchRepo.save(branch);
    }

    @Override
    public Branch updateBranch(Branch branch) {
        return branchRepo.save(branch);
    }

    @Override
    public boolean deleteBranch(Long id) {
        return branchRepo.delete(id);
    }

    @Override
    public List<Branch> searchBranchByName(String keyword) {
        List<Branch> allBranches = branchRepo.findAll();
        if (allBranches == null) {
            return List.of();
        }
        return allBranches.stream()
                .filter(b -> b.getBranchName() != null && 
                             b.getBranchName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
