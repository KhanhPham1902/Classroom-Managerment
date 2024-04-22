package com.khanhpham.managerclassroom.methods;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GetTimes {

    // get current time
    public static String getTimeUpdate(Context context){
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        // Format time
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String formattedHour = decimalFormat.format(hour);
        String formattedMinute = decimalFormat.format(minute);
        String formattedDay = decimalFormat.format(day);
        String formattedMonth = decimalFormat.format(month);
        String timeOfDay = formattedHour + ":" + formattedMinute;
        String timeOfYear = formattedDay + "-" + formattedMonth + "-" + year;

        return timeOfDay + " " + timeOfYear;
    }

    // check classroom time valid
    public static int isClassTimeValid(String classDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date currentDate = new Date();
        String formattedCurrentDate = sdf.format(currentDate);

        return formattedCurrentDate.compareTo(classDate);
    }
}
