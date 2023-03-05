package com.vcc.congestiontaxcalculator.model.config;

import lombok.Data;
import java.util.List;

/**
 * <li>A Object/Class representation of the Configuration that is used for Congestion tax Calculation</li>
 */
@Data
public class CongestionConfig {

    private List<TaxRule> taxRules;
    private List<String> tollFreeVehicleTypes;

    private List<TollFreeCalendar> tollFreeCalenders;
}
