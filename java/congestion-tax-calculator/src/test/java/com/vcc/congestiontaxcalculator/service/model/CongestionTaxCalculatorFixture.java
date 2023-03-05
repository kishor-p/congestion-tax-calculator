package com.vcc.congestiontaxcalculator.service.model;

import com.vcc.congestiontaxcalculator.model.config.CongestionConfig;
import lombok.Data;

import java.util.List;

@Data
public class CongestionTaxCalculatorFixture {
    List<TestCase> testCases;
    CongestionConfig congestionConfig;
}


