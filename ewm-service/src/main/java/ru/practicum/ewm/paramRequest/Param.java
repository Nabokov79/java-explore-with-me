package ru.practicum.ewm.paramRequest;

import ru.practicum.ewm.events.model.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Param {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParamAdminRequest toParamAdminRequest(String users, String states, String categories,
                                                        String rangeStart, String rangeEnd) {
        return new ParamAdminRequest(
                Arrays.stream(users.split(",")).map(Long::valueOf).collect(Collectors.toList()),
                Arrays.asList(states.split(",")),
                Arrays.stream(categories.split(",")).map(Long::valueOf).collect(Collectors.toList()),
                LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER),
                LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER));
    }

    public static ParamUserRequest toParamUserRequest(String text, String categories, Boolean paid, String rangeStart,
                                                      String rangeEnd, Boolean onlyAvailable, String sort) {
        ParamUserRequest param = new ParamUserRequest();
        param.setText(text.toLowerCase());
        param.setCategories(Arrays.stream(categories.split(",")).map(Long::valueOf).collect(Collectors.toList()));
        param.setPaid(paid);
        if (!rangeStart.isEmpty()) {
            param.setRangeStart(LocalDateTime.parse(rangeStart,DATE_TIME_FORMATTER));
        }
        if (!rangeEnd.isEmpty()) {
            param.setRangeEnd(LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER));
        }
        param.setOnlyAvailable(onlyAvailable);
        param.setSort(Sort.valueOf(sort));
        return param;
    }
}
