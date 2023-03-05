package com.vcc.congestiontaxcalculator.model.error;

/**
 * <li> List of all Error Codes and their messages </li>
 * <li> Error Code Format:
 *     <code><ul>
 *         <li> ERR_ </li>
 *         <li>[SERVICE IDENTIFIER]_ </li>
 *         <li>[HTTP STATUS CODE]_ </li>
 *         <li>[ERROR CODE SEQUENCE]  </li>
 *      </ul></code>
 * </li>
 * <li> Message Format: <code>[ERROR_CODE]#[ERROR TITLE]#[ERROR DETAIL with PLACEHOLDERS for PARAMS]</code> </li>
 *
 * <li> <b>USAGE</b>: Used when a custom {@link CongestionCalculatorException} is thrown </li>
 */
public interface ErrorCodes {

    String ERR_01_400_01 = "ERR_01_400_01#Error calculating Tax#Please provide Vehicle Type.";
    String ERR_01_400_02 = "ERR_01_400_02#Error calculating Tax#Please provide dates to calculate Tax.";
}
