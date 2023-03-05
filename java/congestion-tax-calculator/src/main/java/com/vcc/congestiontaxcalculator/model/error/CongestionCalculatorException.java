package com.vcc.congestiontaxcalculator.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * <li> Custom Exception designed to throws Checked exceptions </li>
 * <li> Constructors accept the ErrorMessages with format defined in {@link ErrorCodes}  </li>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongestionCalculatorException extends Exception{

    private String errorTitle;
    private String errorCode;
    private String errorDetails;
    private Integer httpStatus;

    /**
     * Overloaded Constructor without message params. Calls other Constructor  with null for message params.
     *
     * @param errorMsg ErrorMessage with format defined in {@link ErrorCodes}
     */
    public CongestionCalculatorException(String errorMsg){
        this(errorMsg, null);
    }

    /**
     * <li> Construction to create the Exception based on Message and its params </li>
     * <li> Splits the Error message based on <i>#</i> assigns the values to respective properties </li>
     * <li> If <b>args</b> are provided, replaces the placeholders ({0}, {1}, {2}...) with args in 'errorDetails' </li>
     *
     * @param errorMsg ErrorMessage with format defined in {@link ErrorCodes}
     * @param args Arguments that needs to be replaced in message place holders ... if any.
     */
    public CongestionCalculatorException(String errorMsg, String[] args){
        if(StringUtils.hasText(errorMsg)){
            String[] errMsgs = errorMsg.split("#");
            String statusStr = errMsgs[0].split("_")[2];
            if(!statusStr.isBlank()){
                try{
                    this.httpStatus = Integer.parseInt(statusStr);
                } catch(NumberFormatException nfe){
                    this.httpStatus = 500;
                }
            }
            this.errorCode = StringUtils.hasText(errMsgs[0])? errMsgs[0] : "N/A";
            this.errorTitle = StringUtils.hasText(errMsgs[1])? errMsgs[1] : "N/A";
            this.errorDetails = StringUtils.hasText(errMsgs[2])? errMsgs[2] : "N/A";


            if(args!=null && args.length > 0){
                for(int i=0; i< args.length;i++){
                    String placeHolder = "\\{"+i+"}";
                    errorDetails = errorDetails.replaceAll(placeHolder, args[i]);
                }
            }
        }
    }
}
