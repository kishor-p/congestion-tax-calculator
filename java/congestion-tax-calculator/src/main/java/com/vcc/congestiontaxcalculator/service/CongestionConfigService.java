package com.vcc.congestiontaxcalculator.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcc.congestiontaxcalculator.model.Vehicle;
import com.vcc.congestiontaxcalculator.model.config.CongestionCharge;
import com.vcc.congestiontaxcalculator.model.config.CongestionConfig;
import com.vcc.congestiontaxcalculator.model.config.TollFreeCalendar;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <li>Service to load and store Congestion Configuration </li>
 * <li>This is a SINGLETON service that initializes only once per application run cycle</li>
 * <li>If Config is changed or replaced, the application needs to be restated to take effect</li>
 * <li>For now the configuration is loaded from a JSON file from resources.</li>
 */
@Service
@Scope("singleton")
@Slf4j
public class CongestionConfigService {

    //The whole CongestionConfig loaded from json
    @Getter
    private CongestionConfig congestionConfig;

    // Map to hold the congestion charges by City
    private Map<String, List<CongestionCharge>> congestionChargesByCity;

    // Map to hold the TollFreeCalender by Year
    private Map<Integer, TollFreeCalendar> tollFreeCalenderByYear;

    // Map to hold the RED days by Month and Year
    private Map<Integer, Map<Integer, List<Integer>>> readDaysByYearMonth;


    /**
     * <li>A POST-CONSTRUCT method to load CongestionConfig from JSON and initialize rest of the data</li>
     * <li>Once CongestionConfig is loaded this method initializes few of MAP datastructures to make the filtering easy</li>
     */
    @PostConstruct
    public void initialize(){

        log.info("Initializing CongestionConfigService");
      try{
          ObjectMapper oMapper = new ObjectMapper();
          oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          File file = new File(this.getClass().getClassLoader().getResource("congestion-config.json").getFile());
          congestionConfig = oMapper.readValue(file, CongestionConfig.class);

          congestionChargesByCity = new HashMap<>();
          congestionConfig.getTaxRules().forEach(tr -> {
              congestionChargesByCity.put(tr.getCity().toUpperCase(), tr.getCongestionCharges());
          });

          tollFreeCalenderByYear   = new HashMap<>();
          readDaysByYearMonth = new HashMap<>();
          congestionConfig.getTollFreeCalenders().forEach(tc -> {
              tollFreeCalenderByYear.put(tc.getYear(), tc);

              Map<Integer, List<Integer>> redDaysByMonth = new HashMap<>();
              tc.getTollFreeDays().forEach(tfd -> {
                  redDaysByMonth.put(tfd.getMonth(), tfd.getDays());
              });

              readDaysByYearMonth.put(tc.getYear(), redDaysByMonth);
          });
      } catch (Exception e) {
      }
        log.info("Initializing CongestionConfigService Complete");
    }

    /**
     * <li>Returns list of Congestion Charges based on the City name</li>
     * @param city City for which the congestion charges are required
     * @return list of Congestion Charges for given city
     */
    public List<CongestionCharge> fetchCongestionChargesForCity(String city){
        if(StringUtils.hasText(city)){
            return congestionChargesByCity.get(city.toUpperCase());
        }
        return null;
    }

    /**
     * Checks if the provided month in provided year is TollFree month
     * @param year Year to which the month belongs
     * @param month Month to which the check is conducted
     * @return boolean indicating if the provided month is toll free or not
     */
    public boolean isTollFreeMonth(int year, int month){
        if(tollFreeCalenderByYear.containsKey(year)){
            return tollFreeCalenderByYear.get(year).getTollFreeMonths().contains(month);
        }
        return false;
    }


    /**
     * Checks if provided day is RED Day (Public Holiday)
     * @param year Year to which the Day belongs
     * @param month Month to which the Day belongs
     * @param day Day to which the check is ocnducted
     * @return Result indicating if the given Day is holiday or not
     */
    public boolean isRedDay(int year, int month, int day){
        if(readDaysByYearMonth.containsKey(year)){
            Map<Integer, List<Integer>> redDaysByMonth = readDaysByYearMonth.get(year);
            if(redDaysByMonth.containsKey(month)){
                return redDaysByMonth.get(month).contains(day);
            }
        }
        return false;
    }


    /**
     * Chedk is the provided vehicle is one of hte Toll Free vehicles
     * @param vehicle Vehicle that needs to be checked
     * @return Result if the Vehicle is TollFree or Not
     */
    public boolean isTollFreeVehicle(Vehicle vehicle){
        if(congestionConfig != null)
            return congestionConfig.getTollFreeVehicleTypes().contains(vehicle.getVehicleType().toUpperCase());
        else return false;
    }
}
