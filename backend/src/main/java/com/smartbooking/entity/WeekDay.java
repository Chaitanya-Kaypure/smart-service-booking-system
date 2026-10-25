package com.smartbooking.entity;

import java.time.DayOfWeek;

public enum WeekDay {
    MONDAY(DayOfWeek.MONDAY),
    TUESDAY(DayOfWeek.TUESDAY),
    WEDNESDAY(DayOfWeek.WEDNESDAY),
    THURSDAY(DayOfWeek.THURSDAY),
    FRIDAY(DayOfWeek.FRIDAY),
    SATURDAY(DayOfWeek.SATURDAY),
    SUNDAY(DayOfWeek.SUNDAY);

    private final DayOfWeek javaDay;

    WeekDay(DayOfWeek javaDay) {
        this.javaDay = javaDay;
    }

    public DayOfWeek toJavaDay() {
        return javaDay;
    }

    public static WeekDay fromJavaDay(DayOfWeek dayOfWeek) {
        for (WeekDay weekDay : values()) {
            if (weekDay.javaDay == dayOfWeek) {
                return weekDay;
            }
        }
        throw new IllegalArgumentException("Unsupported day: " + dayOfWeek);
    }
}
