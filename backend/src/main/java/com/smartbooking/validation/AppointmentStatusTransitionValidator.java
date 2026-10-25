package com.smartbooking.validation;

import com.smartbooking.entity.AppointmentStatus;
import com.smartbooking.exception.BadRequestException;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class AppointmentStatusTransitionValidator {

    private static final Map<AppointmentStatus, Set<AppointmentStatus>> ALLOWED = Map.of(
            AppointmentStatus.PENDING, EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED),
            AppointmentStatus.CONFIRMED, EnumSet.of(AppointmentStatus.CHECKED_IN, AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW),
            AppointmentStatus.CHECKED_IN, EnumSet.of(AppointmentStatus.IN_PROGRESS, AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW),
            AppointmentStatus.IN_PROGRESS, EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW),
            AppointmentStatus.COMPLETED, EnumSet.noneOf(AppointmentStatus.class),
            AppointmentStatus.CANCELLED, EnumSet.noneOf(AppointmentStatus.class),
            AppointmentStatus.NO_SHOW, EnumSet.noneOf(AppointmentStatus.class)
    );

    private AppointmentStatusTransitionValidator() {
    }

    public static void validateTransition(AppointmentStatus current, AppointmentStatus next) {
        if (current == next) {
            return;
        }
        Set<AppointmentStatus> allowed = ALLOWED.getOrDefault(current, EnumSet.noneOf(AppointmentStatus.class));
        if (!allowed.contains(next)) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + next);
        }
    }

    public static boolean isTerminal(AppointmentStatus status) {
        return status == AppointmentStatus.COMPLETED
                || status == AppointmentStatus.CANCELLED
                || status == AppointmentStatus.NO_SHOW;
    }
}
