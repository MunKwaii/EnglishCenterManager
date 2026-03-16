package vn.edu.ute.service.impl;

import vn.edu.ute.model.Result;
import vn.edu.ute.repository.ResultRepository;
import vn.edu.ute.repository.impl.ResultRepositoryImpl;
import vn.edu.ute.service.ResultService;

import java.util.List;

public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository = new ResultRepositoryImpl();

    @Override
    public List<Result> getResultsByClassId(Long classId) {
        return resultRepository.findByClassId(classId);
    }

    @Override
    public List<Result> getResultsByStudentId(Long studentId) {
        return resultRepository.findByStudentId(studentId);
    }

    @Override
    public Result saveResult(Result result) {
        return resultRepository.save(result);
    }

    @Override
    public boolean saveAllResults(List<Result> results) {
        return resultRepository.saveAll(results);
    }
}
