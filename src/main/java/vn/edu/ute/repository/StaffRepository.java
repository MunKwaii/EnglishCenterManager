package vn.edu.ute.repository;

import vn.edu.ute.model.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffRepository {
    void save(Staff staff) throws Exception;

    Optional<Staff> findById(Long id) throws Exception;

    List<Staff> findAllActive() throws Exception;

    List<Staff> findAll() throws Exception;

    void deleteById(Long id) throws Exception;

    Optional<Staff> findByEmail(String email) throws Exception;

    Optional<Staff> findByPhone(String phone) throws Exception;
}