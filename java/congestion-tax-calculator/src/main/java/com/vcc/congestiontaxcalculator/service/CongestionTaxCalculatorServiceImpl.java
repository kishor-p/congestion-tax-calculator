package com.vcc.congestiontaxcalculator.service;

import com.vcc.congestiontaxcalculator.model.Vehicle;
import com.vcc.congestiontaxcalculator.model.config.CongestionCharge;
import com.vcc.congestiontaxcalculator.model.dto.CalculateTaxPayloadDto;
import com.vcc.congestiontaxcalculator.model.error.CongestionCalculatorException;
import com.vcc.congestiontaxcalculator.model.error.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

/**
 * <li>Implementation of {@link CongestionTaxCalculatorService}</li>
 */
@Service
public class CongestionTaxCalculatorServiceImpl implements CongestionTaxCalculatorService{

    // Default CITY if the payload is missing.
    private static final String DEFAULT_CITY = "GOTHENBURG";

    // Config service to provide the details about CongestionConfig
    CongestionConfigService configService;
    @Autowired
    public CongestionTaxCalculatorServiceImpl(CongestionConfigService configService){
        this.configService = configService;
    }

    /**
     * <li>Calculates the CONGESTION TAX of a Vehicle on provided Dates in provided City</li>
     * <li>Throws exception if Vehicle or Dates are missing</li>
     * @param calculatePayload Vehicle Details Payload
     * @return Tax Calculated
     * @throws CongestionCalculatorException exception if Vehicle or Dates are missing
     */
    @Override
    public double getTax(CalculateTaxPayloadDto calculatePayload) throws CongestionCalculatorException {

        if(calculatePayload.getVehicle() == null || !StringUtils.hasText(calculatePayload.getVehicle().getVehicleType())){
            throw new CongestionCalculatorException(ErrorCodes.ERR_01_400_01);
        }

        if(calculatePayload.getDates() == null || calculatePayload.getDates().size() == 0){
            throw new CongestionCalculatorException(ErrorCodes.ERR_01_400_02);
        }

        // Sort Dates in Ascending order
        Collections.sort(calculatePayload.getDates());

        // USe Default City if payload is missing one
        if(!StringUtils.hasText(calculatePayload.getCity())){calculatePayload.setCity(DEFAULT_CITY);}

        // Final Total fee
        double totalFee = 0;

        // Initial Date to check for SingleCharge rule
        LocalDateTime intervalStart = calculatePayload.getDates().get(0);
        // Highest Charge in current hour
        double highSingleChargeFee = 0;

        for (int i = 0; i < calculatePayload.getDates().size() ; i++) {
            LocalDateTime date = calculatePayload.getDates().get(i);
            double  nextFee = getTollFee(date, calculatePayload.getVehicle(), calculatePayload.getCity());
            long minutes = intervalStart.until(date, ChronoUnit.MINUTES);
            if (minutes <= 60) {
                if (nextFee > highSingleChargeFee) {
                    totalFee -= highSingleChargeFee;
                    highSingleChargeFee = nextFee;
                    totalFee += highSingleChargeFee;
                }
            } else {
                totalFee += nextFee;
                intervalStart = date;
                highSingleChargeFee = nextFee;
            }
        }
        if (totalFee > 60) {
            totalFee = 60;
        }
        return totalFee;
    }


    /**
     * <li>Finds a Toll FEE for given vehicle on a specified Date</li>
     * <li>Check if the date is Toll Free or if the vehicle is TollFree Vehicle based on Configuration</li>
     *
     * @param date Date on which the vehicle has passed through the toll
     * @param vehicle Vehicle details
     * @param city City name
     * @return Congestion Tax
     */
    private double getTollFee(LocalDateTime date, Vehicle vehicle, String city) {
        // Check if Vehicle or Date is TOLL FREE
        if (isTollFreeDate(date) || configService.isTollFreeVehicle(vehicle)) return 0;

        int hour = date.getHour();
        int minute = date.getMinute();
        int hourMinutes = Integer.parseInt(hour+""+minute);
        if(minute == 0){hourMinutes*=10;}

        // Fetch all Congestion amounts for hte specific city
        List<CongestionCharge> charges =  configService.fetchCongestionChargesForCity(city);

        // For the returned list of Charges check if the time falls in any of them and return the amount
        for (CongestionCharge congCharge: charges) {
            if(hourMinutes >= congCharge.getStartHour() && hourMinutes <= congCharge.getEndHour()){
                return congCharge.getAmount();
            }
        }
        return 0;
    }


    /**
     * <li>checks if the given date is a TOLL FREE date</li>
     * <li>SATURDAY and SUNDAYS are always counted as TOLL FREE days</li>
     * <li>Check Configuration for RED days on given year.</li>
     *
     * @param date Date that needs to be checked
     * @return a boolean indicating if the given date is TOLL FREE or not
     */
    private Boolean isTollFreeDate(LocalDateTime date) {
        int year = date.getYear();
        int month = date.getMonth().getValue();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfMonth = date.getDayOfMonth();

        // If the Day is SUNDAY or SATURDAY then it is TOLL FREE
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) return true;


        // Before checking Date check If whole month is TOLL FREE
        if(configService.isTollFreeMonth(year, month)) { return true; }


        // Finally check for the Day if it's red day
        return configService.isRedDay(year, month, dayOfMonth);
    }

}
