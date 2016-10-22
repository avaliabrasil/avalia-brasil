package org.avaliabrasil.avaliabrasil2.avb.util;

import android.content.Context;
import android.util.Log;

import com.jjoe64.graphview.DefaultLabelFormatter;

import java.text.DateFormat;
import java.util.Calendar;

import static com.facebook.GraphRequest.TAG;

/**
 * @author <a href="https://github.com/Klauswk">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */

public class DateAsXAxisLabel extends DefaultLabelFormatter {
    /**
     * the date format that will convert
     * the unix timestamp to string
     */
    protected final DateFormat mDateFormat;

    /**
     * calendar to avoid creating new date objects
     */
    protected final Calendar mCalendar;

    /**
     * create the formatter with the Android default date format to convert
     * the x-values.
     *
     * @param context the application context
     */
    public DateAsXAxisLabel(Context context) {
        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mCalendar = Calendar.getInstance();
    }

    /**
     * create the formatter with your own custom
     * date format to convert the x-values.
     *
     * @param context the application context
     * @param dateFormat custom date format
     */
    public DateAsXAxisLabel(Context context, DateFormat dateFormat) {
        mDateFormat = dateFormat;
        mCalendar = Calendar.getInstance();
    }

    /**
     * formats the x-values as date string.
     *
     * @param value raw value
     * @param isValueX true if it's a x value, otherwise false
     * @return value converted to string
     */
    @Override
    public String formatLabel(double value, boolean isValueX) {
        if (isValueX) {
            mCalendar.setTimeInMillis((long) value);
            int month = mCalendar.get(Calendar.MONTH) + 1;

            Log.d("DateAsXAxis", "Month: " + month);

            String monthString;
            switch (month) {
                case 1:  monthString = "Jan";
                    break;
                case 2:  monthString = "Fev";
                    break;
                case 3:  monthString = "Mar";
                    break;
                case 4:  monthString = "Abr";
                    break;
                case 5:  monthString = "Mai";
                    break;
                case 6:  monthString = "Jun";
                    break;
                case 7:  monthString = "Jul";
                    break;
                case 8:  monthString = "Ago";
                    break;
                case 9:  monthString = "Set";
                    break;
                case 10: monthString = "Out";
                    break;
                case 11: monthString = "Nov";
                    break;
                case 12: monthString = "Dez";
                    break;
                default: monthString = "xxx";
                    break;
            }
            return monthString;
        } else {
            return super.formatLabel(value, isValueX);
        }
    }
}
