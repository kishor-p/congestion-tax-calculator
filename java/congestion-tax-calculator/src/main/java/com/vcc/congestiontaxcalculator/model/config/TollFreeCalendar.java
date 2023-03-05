package com.vcc.congestiontaxcalculator.model.config;

import lombok.Data;

import java.util.List;

/**
 * A Object/Class representation of Toll Free Month and RED Days for a given year
 */
@Data
public class TollFreeCalendar {

    private Integer year;
    private List<Integer> tollFreeMonths;

    private List<TollFreeDay> tollFreeDays;
}
