package com.smartbooking.repository;

import com.smartbooking.entity.Appointment;
import com.smartbooking.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByBookingReference(String bookingReference);

    Page<Appointment> findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(Long customerId, Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
            FROM Appointment a
            WHERE a.service.id = :serviceId
              AND a.appointmentDate = :date
              AND a.status NOT IN :excluded
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlappingBooking(
            @Param("serviceId") Long serviceId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excluded") Collection<AppointmentStatus> excluded);

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
            FROM Appointment a
            WHERE a.customer.id = :customerId
              AND a.service.id = :serviceId
              AND a.appointmentDate = :date
              AND a.startTime = :startTime
              AND a.status NOT IN :excluded
            """)
    boolean existsCustomerSlotBooking(
            @Param("customerId") Long customerId,
            @Param("serviceId") Long serviceId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("excluded") Collection<AppointmentStatus> excluded);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.service.id = :serviceId
              AND a.appointmentDate = :date
              AND a.status IN :statuses
            ORDER BY a.queueSequence ASC
            """)
    List<Appointment> findQueueAppointments(
            @Param("serviceId") Long serviceId,
            @Param("date") LocalDate date,
            @Param("statuses") Collection<AppointmentStatus> statuses);

    @Query("""
            SELECT a FROM Appointment a
            WHERE (:serviceId IS NULL OR a.service.id = :serviceId)
              AND (:status IS NULL OR a.status = :status)
              AND (:date IS NULL OR a.appointmentDate = :date)
              AND (:customerSearch IS NULL OR :customerSearch = ''
                   OR LOWER(a.customer.fullName) LIKE LOWER(CONCAT('%', :customerSearch, '%'))
                   OR LOWER(a.customer.email) LIKE LOWER(CONCAT('%', :customerSearch, '%')))
            """)
    Page<Appointment> adminSearch(
            @Param("serviceId") Long serviceId,
            @Param("status") AppointmentStatus status,
            @Param("date") LocalDate date,
            @Param("customerSearch") String customerSearch,
            Pageable pageable);

    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

    long countByStatus(AppointmentStatus status);

    @Query("""
            SELECT a.service.name, COUNT(a)
            FROM Appointment a
            WHERE a.appointmentDate >= :fromDate
            GROUP BY a.service.name
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> countBookingsByServiceSince(@Param("fromDate") LocalDate fromDate);

    @Query("""
            SELECT a.appointmentDate, COUNT(a)
            FROM Appointment a
            WHERE a.appointmentDate >= :fromDate
            GROUP BY a.appointmentDate
            ORDER BY a.appointmentDate
            """)
    List<Object[]> countBookingsByDateSince(@Param("fromDate") LocalDate fromDate);

    List<Appointment> findTop10ByOrderByCreatedAtDesc();

    Optional<Appointment> findFirstByCustomerIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscStartTimeAsc(
            Long customerId,
            LocalDate date,
            Collection<AppointmentStatus> statuses);
}
