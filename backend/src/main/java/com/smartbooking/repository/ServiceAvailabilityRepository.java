package com.smartbooking.repository;

import com.smartbooking.entity.ServiceAvailability;
import com.smartbooking.entity.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceAvailabilityRepository extends JpaRepository<ServiceAvailability, Long> {

    List<ServiceAvailability> findByServiceIdAndActiveTrue(Long serviceId);

    Optional<ServiceAvailability> findByServiceIdAndDayOfWeekAndActiveTrue(Long serviceId, WeekDay dayOfWeek);
}
