package ru.practicum.ewm.paramRequest;

import ru.practicum.ewm.events.model.Sort;
import ru.practicum.ewm.events.model.State;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Param {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static ParamAdminRequest toParamAdminRequest(List<Long> users, List<State> states,List<Long> categories,
                                                        String rangeStart, String rangeEnd) {
        return new ParamAdminRequest(users,
                                    states,
                                    categories,
                                    LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER),
                                    LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER));
    }

    public static ParamUserRequest toParamUserRequest(String text, List<Long> categories, Boolean paid, String rangeStart,
                                                      String rangeEnd, Boolean onlyAvailable, String sort) {
        ParamUserRequest param = new ParamUserRequest();
        param.setText(text.toLowerCase());
        param.setCategories(categories);
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
