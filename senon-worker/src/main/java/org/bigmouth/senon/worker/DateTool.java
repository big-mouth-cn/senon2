package org.bigmouth.senon.worker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTool {
    private Calendar calendar = Calendar.getInstance();

    public DateTool addDay(int amount) {
        calendar.add(Calendar.DAY_OF_YEAR, amount);
        return this;
    }

    public DateTool add(int field, int amount) {
        calendar.add(field, amount);
        return this;
    }

    public String format(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(calendar.getTime());
    }

}
