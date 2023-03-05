package com.vcc.congestiontaxcalculator.service.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Data
public class TestCase {

    private String description;

    private String vehicleType;

    private List<String> dates;

    private int expectedTax;


    private List<LocalDateTime> dateList = new ArrayList<>();

    public List<String> getDates() {
        return dates;
    }
    public void setDates(List<String> dates) {
        this.dates = dates;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.getDates().forEach(strDate ->{
            try {
                this.getDateList().add(LocalDateTime.parse(strDate, formatter));
            } catch (Exception e) {
            }
        });
    }
}