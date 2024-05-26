package com.khanhpham.managerclassroom.methods;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    // check class time valid
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int isTimeValid(String classDate) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return -1;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.now();

        try {
            LocalDate classDateParsed = LocalDate.parse(classDate, formatter);
            return currentDate.compareTo(classDateParsed);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
