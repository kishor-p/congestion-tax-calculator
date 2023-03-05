package com.vcc.congestiontaxcalculator.model.config;

import lombok.Data;
import java.util.List;

/**
 * A Object/Class that groups all hte Congestion charges for a specific CITY
 */
@Data
public class TaxRule {
    private String city;
    private List<CongestionCharge> congestionCharges;

}
