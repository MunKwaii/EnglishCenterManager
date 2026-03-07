package vn.edu.ute.service.impl;

import vn.edu.ute.model.*;
import vn.edu.ute.model.enums.EnrollmentStatus;
import vn.edu.ute.model.enums.InvoiceStatus;
import vn.edu.ute.repository.EnrollmentRepository;
import vn.edu.ute.repository.FinanceRepository;
import vn.edu.ute.repository.impl.EnrollmentRepositoryImpl;
import vn.edu.ute.repository.impl.FinanceRepositoryImpl;
import vn.edu.ute.service.EnrollmentService;
import java.time.LocalDate;

public class EnrollmentServiceImpl implements EnrollmentService {
    // Sửa lỗi khởi tạo bằng cách gọi class Impl
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
}