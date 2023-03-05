package com.vcc.congestiontaxcalculator.model.config;

import lombok.Data;

/**
 * <li>A Object/Class representation of the ConfigurationCharge that is used for Congestion tax Calculation</li>
 */
@Data
public class CongestionCharge {

    private Integer startHour;
    private Integer endHour;
    private Double amount;

}
