package com.smartbooking.repository;

import com.smartbooking.entity.QueueSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface QueueSessionRepository extends JpaRepository<QueueSession, Long> {

    Optional<QueueSession> findByServiceIdAndQueueDate(Long serviceId, LocalDate queueDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QueueSession q WHERE q.service.id = :serviceId AND q.queueDate = :queueDate")
    Optional<QueueSession> findForUpdate(@Param("serviceId") Long serviceId, @Param("queueDate") LocalDate queueDate);
}
