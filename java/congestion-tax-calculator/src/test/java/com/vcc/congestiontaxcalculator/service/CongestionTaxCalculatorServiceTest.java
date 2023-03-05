package com.vcc.congestiontaxcalculator.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcc.congestiontaxcalculator.model.Car;
import com.vcc.congestiontaxcalculator.model.Vehicle;
import com.vcc.congestiontaxcalculator.model.config.CongestionConfig;
import com.vcc.congestiontaxcalculator.model.config.TollFreeCalendar;
import com.vcc.congestiontaxcalculator.model.dto.CalculateTaxPayloadDto;
import com.vcc.congestiontaxcalculator.model.error.CongestionCalculatorException;
import com.vcc.congestiontaxcalculator.service.model.CongestionTaxCalculatorFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CongestionTaxCalculatorServiceTest {

    @Mock
    CongestionConfigService configService;

    @InjectMocks
    CongestionTaxCalculatorServiceImpl congestionTaxCalculatorService;

    private static CongestionTaxCalculatorFixture taxFixtures;

    @BeforeAll
    public static void init() throws IOException {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File(CongestionTaxCalculatorServiceTest.class.getClassLoader().getResource("get-tax-fixture-1.json").getFile());
        taxFixtures = oMapper.readValue(file, CongestionTaxCalculatorFixture.class);
    }

    @BeforeEach
    public void setUp()  {
        CongestionConfig cc = taxFixtures.getCongestionConfig();

        Map<Integer, TollFreeCalendar> tollFreeCalenderByYear = new HashMap<>();
        Map<Integer, Map<Integer, List<Integer>>> readDaysByYearMonth = new HashMap<>();

        cc.getTollFreeCalenders().forEach(tc -> {
            tollFreeCalenderByYear.put(tc.getYear(), tc);

            Map<Integer, List<Integer>> redDaysByMonth = new HashMap<>();
            tc.getTollFreeDays().forEach(tfd -> {
                redDaysByMonth.put(tfd.getMonth(), tfd.getDays());
            });

            readDaysByYearMonth.put(tc.getYear(), redDaysByMonth);
        });

        ReflectionTestUtils.setField(configService,  "congestionConfig", cc);
        ReflectionTestUtils.setField(configService,  "tollFreeCalenderByYear", tollFreeCalenderByYear);
        ReflectionTestUtils.setField(configService,  "readDaysByYearMonth", readDaysByYearMonth);

        Mockito.when(configService.isTollFreeVehicle(ArgumentMatchers.any(Vehicle.class))).thenCallRealMethod();
        Mockito.when(configService.fetchCongestionChargesForCity(ArgumentMatchers.anyString())).thenReturn(cc.getTaxRules().get(0).getCongestionCharges());
        Mockito.when(configService.isRedDay(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenCallRealMethod();
        Mockito.when(configService.isTollFreeMonth(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenCallRealMethod();
    }

    @Test
    public void testGetTax() {

        taxFixtures.getTestCases().forEach(tc -> {
            double actualTax = 0;
            try {
                actualTax = congestionTaxCalculatorService.getTax(
                        new CalculateTaxPayloadDto(
                                new Vehicle(tc.getVehicleType()),
                                tc.getDateList(),
                                "GOTHENBURG"));
            } catch (CongestionCalculatorException e) {

            }
            Assertions.assertEquals(tc.getExpectedTax(), actualTax, tc.getDescription());
            log.info("Tested:"+tc.getDescription());
        });
    }
}
