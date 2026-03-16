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
import vn.edu.ute.service.PromotionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepositoryImpl();
    private final FinanceRepository financeRepo = new FinanceRepositoryImpl();
    // Bổ sung Service xử lý Khuyến mãi
    private final PromotionService promoService = new PromotionServiceImpl();

    @Override
    public void enrollStudent(Student student, AcademicClass academicClass, Promotion promotion) throws Exception {
        Long currentStudents = enrollmentRepo.countActiveStudentsInClass(academicClass.getClassId());
        if (currentStudents >= academicClass.getMaxStudent()) {
            throw new Exception("Lớp học đã đạt sĩ số tối đa!");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .academicClass(academicClass)
                .enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.Enrolled)
                .result(EnrollmentResult.NA) // Đảm bảo fix lỗi transient
                .build();
        enrollmentRepo.save(enrollment);

        // LOGIC TÍNH LẠI HỌC PHÍ SAU KHUYẾN MÃI
        BigDecimal originalFee = academicClass.getCourse().getFee();
        BigDecimal finalFee = promoService.calculateDiscountedAmount(originalFee, promotion);

        String note = "Học phí lớp: " + academicClass.getClassName();
        if (promotion != null) {
            note += " (Đã áp dụng mã: " + promotion.getPromoName() + ")";
        }

        Invoice invoice = Invoice.builder()
                .student(student)
                .promotion(promotion) // Lưu Khuyến mãi vào hóa đơn
                .totalAmount(finalFee) // Lưu giá đã giảm
                .issueDate(LocalDate.now())
                .status(InvoiceStatus.Issued)
                .note(note)
                .build();
        financeRepo.saveInvoice(invoice);
    }

    @Override
    public List<Enrollment> getEnrollmentsByClassId(Long classId) throws Exception {
        return enrollmentRepo.getEnrollmentsByClassId(classId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) throws Exception {
        return enrollmentRepo.getEnrollmentsByStudentId(studentId);
    }
}