package vn.edu.ute.repository;

import vn.edu.ute.model.AcademicClass;
import java.util.List;

public interface AcademicClassRepository {
    List<AcademicClass> findAll();

    AcademicClass findById(Long id);

    AcademicClass save(AcademicClass academicClass);

    boolean delete(Long id);
}
