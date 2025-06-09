package com.microservice.systemservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYY_MM_DD_T_HH_MM_SS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static boolean isDateInFormat(String dateStr, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String changeFormat(String dateStr, String fromFormat, String toFormat) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(fromFormat);
            SimpleDateFormat outputFormat = new SimpleDateFormat(toFormat);

            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }

}
