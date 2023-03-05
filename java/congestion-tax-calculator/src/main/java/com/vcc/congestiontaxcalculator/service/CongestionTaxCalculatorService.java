package com.vcc.congestiontaxcalculator.service;

import com.vcc.congestiontaxcalculator.model.dto.CalculateTaxPayloadDto;
import com.vcc.congestiontaxcalculator.model.error.CongestionCalculatorException;

/**
 * <li>Services related to calculate the Congestion Tax</li>
 */
public interface CongestionTaxCalculatorService {

    /**
     * <li>Calculates the CONGESTION TAX of a Vehicle on provided Dates in provided City</li>
     * <li>Throws exception if Vehicle or Dates are missing</li>
     * @param calculatePayload Vehicle Details Payload
     * @return Tax Calculated
     * @throws CongestionCalculatorException exception if Vehicle or Dates are missing
     */
    public double getTax(CalculateTaxPayloadDto calculatePayload) throws CongestionCalculatorException;
}
