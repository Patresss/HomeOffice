package com.patres.homeoffice.settings;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record WorkSettings(
        Set<DayOfWeek> days,
        @JsonFormat(pattern = "HH:mm")
        LocalTime start,
        @JsonFormat(pattern = "HH:mm")
        LocalTime end
) {

}
