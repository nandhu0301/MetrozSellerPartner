package com.smiligenceUAT1.metrozsellerpartner.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.DATE_FORMAT;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.DATE_FORMAT_YYYYMD;
import static com.smiligenceUAT1.metrozsellerpartner.common.Constant.DATE_TIME_FORMAT;

public class DateUtils {
    public static String fetchCurrentDate (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }


    public static String fetchFormatedCurrentDate (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }
    public static String fetchCurrentDateAndTime (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchYesterdayDateTimeInterval(){
        LocalDate now= LocalDate.now();
        LocalDate yesterday =now.minusDays(1);
        String yesterdayDate = yesterday.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));

        return yesterdayDate;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchYesterdayDate(){
        LocalDate now= LocalDate.now();
        LocalDate yesterday =now.minusDays(1);
        String yesterdayDate = yesterday.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        return yesterdayDate;
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchLast7days(){
        LocalDate now= LocalDate.now();
        LocalDate lastSeven =now.minusDays(7);
        String lastSevenDays = lastSeven.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
        return lastSevenDays;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchLast30Days(){
        LocalDate now= LocalDate.now();
        LocalDate thirty=now.minusDays(30);
        String thirthDate = thirty.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));

        return thirthDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchLast60Days(){
        LocalDate now= LocalDate.now();
        LocalDate sixty=now.minusDays(60);
        String sixtyDate = sixty.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
        return sixtyDate;
    }

    public static String fetchTime(String dateTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm"  );
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = timeFormat.format(date);
        return time;
    }

    public static ArrayList<String> fetchTimeInterval() {

        int begin = 0; // Starts at mid nigth 12 AM
        int end = 1439; //23*60 + 59; // till mid nigth 11:59 PM
        int interval = 120; // every 2 hours

        ArrayList<String> timeArrayList = new ArrayList<>();

        for (int time = begin; time <= end; time += interval) {
            timeArrayList.add(String.format("%02d:%02d", time / 60, time % 60));
        }
        return timeArrayList;
    }
    public static String fetchTimewithSeconds(String dateTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm:ss"  );
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = timeFormat.format(date);
        return time;
    }

    /**
     * @param  target  hour to check
     * @param  start   interval start
     * @param  end     interval end
     * @return true    true if the given hour is between
     */
    public static boolean isHourInInterval(String target, String start, String end) {
        return ((target.compareTo(start) >= 0)
                && (target.compareTo(end) <= 0));
    }

}
