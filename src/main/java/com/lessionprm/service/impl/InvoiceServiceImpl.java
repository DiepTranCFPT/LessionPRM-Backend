package com.lessionprm.service.impl;

import com.lessionprm.entity.Course;
import com.lessionprm.entity.Invoice;
import com.lessionprm.entity.User;
import com.lessionprm.exception.BadRequestException;
import com.lessionprm.exception.ResourceNotFoundException;
import com.lessionprm.repository.CourseRepository;
import com.lessionprm.repository.InvoiceRepository;
import com.lessionprm.repository.UserRepository;
import com.lessionprm.service.interfaces.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public Invoice createInvoice(Long userId, Long courseId, Invoice.PaymentMethod paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (!course.getIsActive()) {
            throw new BadRequestException("Course is not active");
        }
        
        // Check if user already purchased this course
        if (hasUserPurchasedCourse(userId, courseId)) {
            throw new BadRequestException("User has already purchased this course");
        }
        
        Invoice invoice = new Invoice(user, course, course.getPrice(), paymentMethod);
        invoice.setOrderId("ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        invoice.setStatus(Invoice.Status.PENDING);
        
        return invoiceRepository.save(invoice);
    }
    
    @Override
    public Invoice updateInvoice(Long id, Invoice invoice) {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        existingInvoice.setStatus(invoice.getStatus());
        existingInvoice.setTransactionId(invoice.getTransactionId());
        existingInvoice.setPaymentUrl(invoice.getPaymentUrl());
        existingInvoice.setNotes(invoice.getNotes());
        
        return invoiceRepository.save(existingInvoice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByUser(User user) {
        return invoiceRepository.findByUser(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByUser(User user, Pageable pageable) {
        return invoiceRepository.findByUser(user, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByUserId(Long userId) {
        return invoiceRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByUserId(Long userId, Pageable pageable) {
        return invoiceRepository.findByUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByStatus(Invoice.Status status) {
        return invoiceRepository.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByTransactionId(String transactionId) {
        return invoiceRepository.findByTransactionId(transactionId);
    }
    
    @Override
    public Invoice markAsPaid(Long id, String transactionId) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        invoice.setStatus(Invoice.Status.PAID);
        invoice.setTransactionId(transactionId);
        invoice.setPaidAt(LocalDateTime.now());
        
        return invoiceRepository.save(invoice);
    }
    
    @Override
    public Invoice markAsFailed(Long id, String reason) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        invoice.setStatus(Invoice.Status.FAILED);
        invoice.setNotes(reason);
        
        return invoiceRepository.save(invoice);
    }
    
    @Override
    public Invoice markAsCancelled(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        
        invoice.setStatus(Invoice.Status.CANCELLED);
        
        return invoiceRepository.save(invoice);
    }
    
    @Override
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = invoiceRepository.getTotalRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPaidInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return invoiceRepository.countPaidInvoicesByDateRange(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserPurchasedCourse(Long userId, Long courseId) {
        return invoiceRepository.countPaidInvoicesByUserAndCourse(userId, courseId) > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Long> getPaidCourseIdsByUser(Long userId) {
        return invoiceRepository.findPaidCourseIdsByUser(userId);
    }
    
    @Override
    public void processExpiredInvoices() {
        // Process invoices that are pending for more than 24 hours
        LocalDateTime expiryDate = LocalDateTime.now().minusHours(24);
        List<Invoice> expiredInvoices = invoiceRepository.findExpiredInvoices(expiryDate);
        
        for (Invoice invoice : expiredInvoices) {
            invoice.setStatus(Invoice.Status.CANCELLED);
            invoice.setNotes("Automatically cancelled due to expiry");
            invoiceRepository.save(invoice);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByStatus(Invoice.Status status) {
        return invoiceRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentMethodStatistics() {
        return invoiceRepository.getPaymentMethodStatistics();
    }
}