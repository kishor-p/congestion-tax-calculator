package com.vcc.congestiontaxcalculator.controller;

import com.vcc.congestiontaxcalculator.model.dto.CalculateTaxPayloadDto;
import com.vcc.congestiontaxcalculator.model.config.CongestionConfig;
import com.vcc.congestiontaxcalculator.model.error.CongestionCalculatorException;
import com.vcc.congestiontaxcalculator.service.CongestionConfigService;
import com.vcc.congestiontaxcalculator.service.CongestionTaxCalculatorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * API to calculate the Congestion Tax for a vehicle.
 */
@RestController
@Slf4j
@Tag(name="Congestion Tax Calculator API", description = "API to calculate the Congestion Tax for a vehicle.")
public class CongestionTaxController {

    CongestionTaxCalculatorService calculatorService;
    CongestionConfigService configService;

    @Autowired
    public CongestionTaxController(
            CongestionTaxCalculatorService calculatorService,
            CongestionConfigService configService){
        this.calculatorService = calculatorService;
        this.configService = configService;
    }

    @PostMapping("/calculatetax")
    @Operation(
            summary = "Calculates congestion tax amount for a vehicle on a provided Dates formatted[yyyy-MM-ddTHH:mm:ss.SSSZ]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Calculated Tax Amount"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "500", description = "Internal Error")
            }
    )
    public ResponseEntity<Double> calculateCongestionTax(@RequestBody CalculateTaxPayloadDto payload) throws CongestionCalculatorException {
        double amount = calculatorService.getTax(payload);
        return new ResponseEntity<>(amount, HttpStatus.OK);
    }

    @GetMapping("/cogestionconfig")
    @Operation(
            summary = "Returns the current Congestion Configuration application is configured and loaded with.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Calculated Tax Amount"),
                    @ApiResponse(responseCode = "500", description = "Internal Error")
            }
    )
    public ResponseEntity<CongestionConfig> fetchCurrentConfiguration(){
        return new ResponseEntity<>(configService.getCongestionConfig(), HttpStatus.OK);
    }

    @GetMapping("/")
    @Hidden
    public RedirectView index() {
        return new RedirectView("swagger-ui.html");
    }

}
