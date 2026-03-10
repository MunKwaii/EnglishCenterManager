package vn.edu.ute.service;

import vn.edu.ute.model.Staff;
import java.util.List;

public interface StaffService {
    void saveStaff(Staff staff) throws Exception;
    List<Staff> getActiveStaffs() throws Exception;
}