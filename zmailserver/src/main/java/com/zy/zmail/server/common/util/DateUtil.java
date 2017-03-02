package com.zy.zmail.server.common.util;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by wenliz on 2017/2/14.
 */
public class DateUtil {
    private static Calendar getStartOfDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
    public static Date getDayBegin() {
        Calendar cal = getStartOfDate();
        return cal.getTime();
    }

    public static Date getBeginDayOfWeek() {
        Calendar cal = getStartOfDate();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    public static Date getStartOfMonth(){
        Calendar cal = getStartOfDate();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }


    public static Date getBeforeOfNow(int month){
        Calendar cal = getStartOfDate();
        int currentMonth = cal.get(Calendar.MONTH);
        int newMonth = currentMonth - month;
        if(newMonth<0){
            newMonth += 12;
            int currentYear = cal.get(Calendar.YEAR);
            currentYear--;
            cal.set(Calendar.YEAR, currentYear);
        }
        cal.set(Calendar.MONTH, newMonth);
        return cal.getTime();
    }
}

