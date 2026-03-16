package vn.edu.ute.service;

import vn.edu.ute.model.Staff;

import java.util.List;

public interface StaffService {
    // Queries/helpers
    boolean isEmailExists(String email) throws Exception;

    Staff findByPhone(String phone) throws Exception;

    List<Staff> getActiveStaffs() throws Exception;

    List<Staff> getAllStaffs() throws Exception;

    // CRUD
    void addStaff(Staff staff) throws Exception;

    void updateStaff(Staff staff) throws Exception;

    void deleteStaff(Long id) throws Exception;
}