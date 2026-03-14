package vn.edu.ute.repository;

import vn.edu.ute.model.Staff;
import java.util.List;

public interface StaffRepository {
    void save(Staff staff) throws Exception;
    List<Staff> findAllActive() throws Exception;
    List<Staff> findAll() throws Exception;
    Staff findById(Long id) throws Exception;
}