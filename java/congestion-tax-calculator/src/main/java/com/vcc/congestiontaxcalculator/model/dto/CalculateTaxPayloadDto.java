package com.vcc.congestiontaxcalculator.model.dto;

import com.vcc.congestiontaxcalculator.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <li> A DTO to carry the user provided data</li>
 * <li> This DTO has all the necessary details that required for Tax Calculation</li>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateTaxPayloadDto {
    private Vehicle vehicle;

    private List<LocalDateTime> dates;
    private String city;
}
