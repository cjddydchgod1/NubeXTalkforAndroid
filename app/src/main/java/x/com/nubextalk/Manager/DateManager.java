/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

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
    /**
     * 지정된 Format으로 날짜를 문자열로 변환
     *
     * @param date
     * @return
     */
    public static String convertDate(Date date){
        return new SimpleDateFormat("yyyy년 MM월 dd일 (E)").format(date);
    }

    /**
     * 지정한 Format으로 날짜를 문자열로 변환
     * DateFormat은 아래 사이트 참조
     * https://promobile.tistory.com/197
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String convertDate (Date date, String pattern){
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String convertDate (long time, String pattern){
        return new SimpleDateFormat(pattern).format(time);
    }

    public static String convertDate(Date date, String inPattern, TimeZone t){
        SimpleDateFormat s = new SimpleDateFormat(inPattern);
        s.setTimeZone(t);
        return s.format(date);
    }

    /**
     * 특정 Format의(inPattern) 날짜 문자열(date)을 지정한 Format(outPattern) 으로 변환
     * @param date
     * @param inPattern
     * @param outPattern
     * @return
     */
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

    /**
     * 특정 Format(inPattern) 문자열을(date) Date객체로 변환
     * @param date
     * @param inPattern
     * @return
     */
    public static Date convertDatebyString (String date, String inPattern){
        try {
            return new SimpleDateFormat(inPattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * 두개의 Date의 날짜 차이 계산
     * @param fDate
     * @param eDate
     * @return
     */
    public static int getBetweenDay( Date fDate, Date eDate ) {
        if(fDate==null || eDate == null) {return 0;}
        Calendar cal=Calendar.getInstance();
        long c_time=eDate.getTime();
        long v_time=fDate.getTime();
        long i_time=c_time-v_time;
        return (int) (Math.abs(i_time)/(1000 * 60 * 60 * 24));
    }

    /**
     * 두개 Date의 시간 차이 계산
     * @param fDate
     * @param eDate
     * @return
     */
    public static int getBetweenHour( Date fDate, Date eDate ) {
        if(fDate==null || eDate == null) {return 0;}
        Calendar cal=Calendar.getInstance();
        long c_time=eDate.getTime();
        long v_time=fDate.getTime();
        long i_time=c_time-v_time;
        return (int) (Math.abs(i_time)/(1000 * 60 * 60));
    }

    /**
     * 두개 Date의 분 차이 계산
     * @param fDate
     * @param eDate
     * @return
     */
    public static int getBetweenMin( Date fDate, Date eDate ) {
        if(fDate==null || eDate == null) {return 0;}
        Calendar cal=Calendar.getInstance();
        long c_time=eDate.getTime();
        long v_time=fDate.getTime();
        long i_time=c_time-v_time;
        return (int) (Math.abs(i_time)/(1000 * 60));
    }

    /**
     * 주어진 두개의 Date가 같은 날짜(Day)에 있는지 체크
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean isSameDay(Date startDate, Date endDate){
        ArrayList<Date> minMaxDates = getMinMaxDate(startDate, endDate);
        startDate = minMaxDates.get(0);
        endDate = minMaxDates.get(1);
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        return startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 현재시간과 주어진 date의 시간 차이 계산
     * @param date
     * @param inPattern
     * @return
     */
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
            if(sec<=0)                  { return "방금 전"; }
            else if(sec<60)             { return sec +"초 전"; }
            else if(60<sec&&min<60)     { return min +"분 전"; }
            else if(60<min&&hour<24)    { return hour +"시간 전"; }
            else                        { return ""; }
        }
    }

    /**
     * 비교 함수를 위하여 두개의 날짜를 초기화
     *  startDate : 00시 00분 00초
     *  endDate : 23시 59분 59초
     *
     * @param startDate
     * @param endDate
     * @return
     */
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
}
