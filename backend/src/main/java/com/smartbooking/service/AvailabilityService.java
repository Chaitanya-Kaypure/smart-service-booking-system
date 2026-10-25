package com.smartbooking.service;

import com.smartbooking.dto.service.AvailabilitySlotResponse;
import com.smartbooking.dto.service.DailyAvailabilityResponse;
import com.smartbooking.entity.ServiceAvailability;
import com.smartbooking.entity.ServiceEntity;
import com.smartbooking.entity.WeekDay;
import com.smartbooking.exception.BadRequestException;
import com.smartbooking.repository.AppointmentRepository;
import com.smartbooking.repository.ServiceAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final ServiceCatalogService serviceCatalogService;
    private final ServiceAvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public DailyAvailabilityResponse getDailyAvailability(Long serviceId, LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot query availability for past dates");
        }

        ServiceEntity service = serviceCatalogService.getServiceEntity(serviceId);
        if (!service.isActive()) {
            throw new BadRequestException("Service is not active");
        }

        WeekDay weekDay = WeekDay.fromJavaDay(date.getDayOfWeek());
        ServiceAvailability availability = availabilityRepository
                .findByServiceIdAndDayOfWeekAndActiveTrue(serviceId, weekDay)
                .orElseThrow(() -> new BadRequestException("Service is not available on the selected day"));

        LocalTime slotStart = availability.getOpeningTime();
        LocalTime closingTime = availability.getClosingTime();
        int interval = availability.getSlotIntervalMinutes();
        int duration = service.getDurationMinutes();

        List<AvailabilitySlotResponse> slots = new ArrayList<>();
        while (!slotStart.plusMinutes(duration).isAfter(closingTime)) {
            LocalTime slotEnd = slotStart.plusMinutes(duration);
            boolean booked = appointmentRepository.existsOverlappingBooking(
                    serviceId,
                    date,
                    slotStart,
                    slotEnd,
                    AppointmentConstants.NON_BLOCKING_STATUSES);
            boolean available = !booked;
            if (date.equals(LocalDate.now()) && slotStart.isBefore(LocalTime.now())) {
                available = false;
            }
            slots.add(AvailabilitySlotResponse.builder()
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .available(available)
                    .build());
            slotStart = slotStart.plusMinutes(interval);
        }

        return DailyAvailabilityResponse.builder()
                .serviceId(serviceId)
                .date(date)
                .slotIntervalMinutes(interval)
                .slots(slots)
                .build();
    }
}
