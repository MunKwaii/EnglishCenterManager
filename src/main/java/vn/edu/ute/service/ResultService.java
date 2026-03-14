package vn.edu.ute.service;

import vn.edu.ute.model.Result;
import java.util.List;

public interface ResultService {
    List<Result> getResultsByClassId(Long classId);

    List<Result> getResultsByStudentId(Long studentId);

    Result saveResult(Result result);

    boolean saveAllResults(List<Result> results);
}
