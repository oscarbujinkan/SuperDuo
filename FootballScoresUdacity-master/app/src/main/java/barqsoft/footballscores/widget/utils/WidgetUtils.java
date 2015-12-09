package barqsoft.footballscores.widget.utils;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oscarfuentes on 04-11-15.
 */
public class WidgetUtils {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private static SimpleDateFormat hhmmformat = new SimpleDateFormat("HH:mm", Locale.US);

    public static boolean isToday(String dateString){
        try {
            Date d = format.parse(dateString);
            return DateUtils.isToday(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }
    public static String matchTime(String dateString){
        try {
            Date d = format.parse(dateString);
            return hhmmformat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
