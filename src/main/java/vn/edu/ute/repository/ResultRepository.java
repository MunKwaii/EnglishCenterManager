package vn.edu.ute.repository;

import vn.edu.ute.model.Result;
import java.util.List;

public interface ResultRepository {
    List<Result> findByClassId(Long classId);
    List<Result> findByStudentId(Long studentId);
    Result save(Result result);
    boolean saveAll(List<Result> results);
}
