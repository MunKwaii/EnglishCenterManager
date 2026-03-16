package vn.edu.ute.repository;

import vn.edu.ute.model.PlacementTest;
import java.util.List;

public interface PlacementTestRepository {
    List<PlacementTest> findAll();
    PlacementTest findById(Long id);
    PlacementTest save(PlacementTest test);
    boolean delete(Long id);
}
