package com.curtisnewbie.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * Create a string of Date in this format: ddMMyyyy
     *
     * @return a string of Date in this format: ddMMyyyy
     */
    public static String getDateTimeStr() {
        return new SimpleDateFormat("ddMMyyyy").format(new Date());
    }
}
