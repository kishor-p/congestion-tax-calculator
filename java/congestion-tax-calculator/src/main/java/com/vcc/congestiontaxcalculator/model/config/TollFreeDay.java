package com.vcc.congestiontaxcalculator.model.config;

import lombok.Data;

import java.util.List;

/**
 * Class/Object grouping all the RED days of a Month
 */
@Data
public class TollFreeDay {
    private Integer month;
    private List<Integer> days;
}
