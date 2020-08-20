package x.com.nubextalk.Manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateManager {
    public static String convertDate(Date date){
        return new SimpleDateFormat("yyyy년 MM월 dd일 (E)").format(date);
    }


    public static String convertDate (Date date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String convertDate (long date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String convertDate(Date date, String inPattern, TimeZone t){
        SimpleDateFormat s = new SimpleDateFormat(inPattern);
        s.setTimeZone(t);
        return s.format(date);
    }

    public static String convertDate (String date, String inPattern, String outPattern){
        try {
            return new SimpleDateFormat(outPattern).format(new SimpleDateFormat(inPattern).parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return String.valueOf(date);
        }
    }

    public static String convertDate (String date, String regex, String inPattern, String outPattern) {
        try {
            Matcher m = Pattern.compile(regex).matcher(date);
            return m.find() ? new SimpleDateFormat(outPattern).format(new SimpleDateFormat(inPattern).parse(m.group(0))) : date;
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }

    public static Date convertDatebyString (String date, String inPattern){
        try {
            return new SimpleDateFormat(inPattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static int getBetweenDay( Date fDate, Date eDate ) {
        if(fDate==null || eDate == null) {return 0;}
        Calendar cal=Calendar.getInstance();
        long c_time=eDate.getTime();
        long v_time=fDate.getTime();
        long i_time=c_time-v_time;
        return (int) (Math.abs(i_time)/(1000 * 60 * 60 * 24));
    }

    public static int getBetweenHour( Date fDate, Date eDate ) {
        if(fDate==null || eDate == null) {return 0;}
        Calendar cal=Calendar.getInstance();
        long c_time=eDate.getTime();
        long v_time=fDate.getTime();
        long i_time=c_time-v_time;
        return (int) (Math.abs(i_time)/(1000 * 60 * 60));
    }

    public static int getBetweenMin( Date fDate, Date eDate ) {
        if(fDate==null || eDate == null) {return 0;}
        Calendar cal=Calendar.getInstance();
        long c_time=eDate.getTime();
        long v_time=fDate.getTime();
        long i_time=c_time-v_time;
        return (int) (Math.abs(i_time)/(1000 * 60));
    }

    public static int getBetweenDayCount(Date startDate, Date endDate){
        ArrayList<Date> minMaxDates = getMinMaxDate(startDate, endDate);
        startDate = minMaxDates.get(0);
        endDate = minMaxDates.get(1);
        return getBetweenDay(startDate, endDate);
    }

    public static int getBetweenMinCount(Date startDate, Date endDate){
        ArrayList<Date> minMaxDates = getMinMaxMinute(startDate, endDate);
        startDate = minMaxDates.get(0);
        endDate = minMaxDates.get(1);
        return getBetweenMin(startDate, endDate);
    }

    public static boolean isSameDay(Date startDate, Date endDate){
        ArrayList<Date> minMaxDates = getMinMaxMinute(startDate, endDate);
        startDate = minMaxDates.get(0);
        endDate = minMaxDates.get(1);
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        return startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR);
    }

    public static String getTimeInterval( String date, String inPattern ) {
        Date time = convertDatebyString(date, inPattern);
        if(time==null) {return "0";}
        Calendar cal=Calendar.getInstance();
        Date endDate=cal.getTime();
        long c_time=endDate.getTime();
        long v_time=time.getTime();
        long i_time=c_time-v_time;
        int sec  = (int) (i_time/1000);
        int min  = (int) (i_time/(1000*60));
        int hour = (int) (i_time/(1000 * 60 * 60) % 24);
        int day = (int) (i_time/(1000 * 60 * 60) / 24 );
        if(day > 0){
            return day +"일전";
        }
        else{
            if(sec==0)                  { return "방금 전"; }
            else if(sec<60)             { return sec +"초 전"; }
            else if(60<sec&&min<60)     { return min +"분 전"; }
            else if(60<min&&hour<24)    { return hour +"시간 전"; }
            else                        { return ""; }
        }
    }

    public static ArrayList<Date> getMinMaxDate(Date startDate, Date endDate){
        ArrayList<Date> dateList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        dateList.add(c.getTime());

        c.setTime(endDate);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 23,59,59);
        c.set(Calendar.MILLISECOND, 999);
        dateList.add(c.getTime());

        return dateList;
    }

    public static ArrayList<Date> getMinMaxMinute(Date startDate, Date endDate){
        ArrayList<Date> dateList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR), c.get(Calendar.MINUTE), 0);
        c.set(Calendar.MILLISECOND, 0);
        dateList.add(c.getTime());

        c.setTime(endDate);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR),c.get(Calendar.MINUTE),59);
        c.set(Calendar.MILLISECOND, 999);
        dateList.add(c.getTime());

        return dateList;
    }
}
