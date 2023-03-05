package com.vcc.congestiontaxcalculator.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcc.congestiontaxcalculator.model.vehicles.Vehicle;
import com.vcc.congestiontaxcalculator.model.config.CongestionConfig;
import com.vcc.congestiontaxcalculator.model.config.TollFreeCalendar;
import com.vcc.congestiontaxcalculator.model.dto.CalculateTaxPayloadDto;
import com.vcc.congestiontaxcalculator.model.error.CongestionCalculatorException;
import com.vcc.congestiontaxcalculator.service.model.CongestionTaxCalculatorFixture;
import com.vcc.congestiontaxcalculator.service.model.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
public class CongestionTaxCalculatorServiceTest {

    @Mock
    CongestionConfigService configService;

    @InjectMocks
    CongestionTaxCalculatorServiceImpl congestionTaxCalculatorService;

    private static CongestionTaxCalculatorFixture taxFixtures;
    private static Map<Integer, TollFreeCalendar> tollFreeCalenderByYear = new HashMap<>();
    private static Map<Integer, Map<Integer, List<Integer>>> readDaysByYearMonth = new HashMap<>();

    @BeforeAll
    public static void init() throws IOException {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File(CongestionTaxCalculatorServiceTest.class.getClassLoader().getResource("get-tax-fixture-1.json").getFile());
        taxFixtures = oMapper.readValue(file, CongestionTaxCalculatorFixture.class);

        CongestionConfig cc = taxFixtures.getCongestionConfig();

        cc.getTollFreeCalenders().forEach(tc -> {
            tollFreeCalenderByYear.put(tc.getYear(), tc);

            Map<Integer, List<Integer>> redDaysByMonth = new HashMap<>();
            tc.getTollFreeDays().forEach(tfd -> {
                redDaysByMonth.put(tfd.getMonth(), tfd.getDays());
            });

            readDaysByYearMonth.put(tc.getYear(), redDaysByMonth);
        });
    }

    @BeforeEach
    public void setUp()  {
        ReflectionTestUtils.setField(configService,  "congestionConfig", taxFixtures.getCongestionConfig());
        ReflectionTestUtils.setField(configService,  "tollFreeCalenderByYear", tollFreeCalenderByYear);
        ReflectionTestUtils.setField(configService,  "readDaysByYearMonth", readDaysByYearMonth);

        Mockito.when(configService.isTollFreeVehicle(ArgumentMatchers.any(Vehicle.class))).thenCallRealMethod();
        Mockito.when(configService.fetchCongestionChargesForCity(ArgumentMatchers.anyString())).thenReturn(taxFixtures.getCongestionConfig().getTaxRules().get(0).getCongestionCharges());
        Mockito.when(configService.isRedDay(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenCallRealMethod();
        Mockito.when(configService.isTollFreeMonth(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenCallRealMethod();

    }


    @ParameterizedTest
    @MethodSource("fetchAllTestCase")
    public void testGetTax(TestCase tc){
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
    }

    private static List<TestCase> fetchAllTestCase(){
        return taxFixtures.getTestCases();
    }
}
