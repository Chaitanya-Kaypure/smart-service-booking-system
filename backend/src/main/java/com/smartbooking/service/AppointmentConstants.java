package com.smartbooking.service;

import com.smartbooking.entity.AppointmentStatus;

import java.util.EnumSet;
import java.util.Set;

public final class AppointmentConstants {

    public static final Set<AppointmentStatus> NON_BLOCKING_STATUSES = EnumSet.of(
            AppointmentStatus.CANCELLED,
            AppointmentStatus.NO_SHOW,
            AppointmentStatus.COMPLETED
    );

    public static final Set<AppointmentStatus> QUEUE_ACTIVE_STATUSES = EnumSet.of(
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.IN_PROGRESS
    );

    private AppointmentConstants() {
    }
}
