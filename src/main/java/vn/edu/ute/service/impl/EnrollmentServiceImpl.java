package vn.edu.ute.service.impl;

import vn.edu.ute.model.*;
import vn.edu.ute.model.enums.EnrollmentStatus;
import vn.edu.ute.model.enums.InvoiceStatus;
import vn.edu.ute.model.enums.EnrollmentResult;
import vn.edu.ute.repository.EnrollmentRepository;
import vn.edu.ute.repository.FinanceRepository;
import vn.edu.ute.repository.impl.EnrollmentRepositoryImpl;
import vn.edu.ute.repository.impl.FinanceRepositoryImpl;
import vn.edu.ute.service.EnrollmentService;
import java.time.LocalDate;
import java.util.List; // MỚI THÊM

public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepositoryImpl();
    private final FinanceRepository financeRepo = new FinanceRepositoryImpl();

    @Override
    public void enrollStudent(Student student, AcademicClass academicClass) throws Exception {
        Long currentStudents = enrollmentRepo.countActiveStudentsInClass(academicClass.getClassId());
        if (currentStudents >= academicClass.getMaxStudent()) {
            throw new Exception("Lớp học đã đạt sĩ số tối đa!");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .academicClass(academicClass)
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.Enrolled)
                .result(EnrollmentResult.NA)
                .build();
        enrollmentRepo.save(enrollment);

        Invoice invoice = Invoice.builder()
                .student(student)
                .totalAmount(academicClass.getCourse().getFee())
                .issueDate(LocalDate.now())
                .status(InvoiceStatus.Issued)
                .note("Học phí lớp: " + academicClass.getClassName())
                .build();
        financeRepo.saveInvoice(invoice);
    }

    // MỚI THÊM: Triển khai hàm gọi xuống Repository để lấy dữ liệu
    @Override
    public List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception {
        return enrollmentRepo.getEnrollmentsByClassId(classId);
    }
}