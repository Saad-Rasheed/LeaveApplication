package com.agp.leaveapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateUtil {

    public static final Long GetDifference(String date1, String date2) {

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDaysBWDates(d1, d2) + 1;
    }

    private static long getDaysBWDates(Date newDate, Date endDate) {
        long diff = endDate.getTime() - newDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

}
