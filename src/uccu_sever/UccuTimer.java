/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

/**
 *
 * @author xiaoshuang
 */
public class UccuTimer {
    private long startTime;
    private long initTime;
    public UccuTimer()
    {
        startTime = 0;
        initTime = 0;
    }
    public void reset(long start)//单位NanoSec
    {
        startTime = start;
        initTime = System.nanoTime();
    }
    public long getMS()
    {
        return (System.nanoTime() - initTime + startTime)/1000_000L;
    }
    public long getS()
    {
        return getMS()/1000L;
    }
    public long getMin()
    {
        return getMS()/60_000L;
    }
    public long getHour()
    {
        return getMS()/3_600_000L;
    }
    public String getString()
    {
        long total = getMS();
        total /= 1000L;
        long sec = total % 60;
        long min = (total/60) % 60;
        long hour = (total/3600) % 24;
        long day = (total/86400) % 365;
        long year = (total/31_536_000L);
        
        StringBuilder sb = new StringBuilder();
        sb.append(year).append(" years, ").append(day).append(" days, ");
        sb.append(hour).append(" hours, ").append(min).append(" minutes, ");
        sb.append(sec).append(" seconds.");
        return sb.toString();
    }
    public String getTimestamp()
    {
        long total = getMS();
        total /= 1000L;
        long sec = total % 60;
        long min = (total/60) % 60;
        long hour = (total/3600) % 24;
        long day = (total/86400) % 365;
        long year = (total/31_536_000L);
        
        return String.format("[%02d:%02d:%02d:%02d:%02d]", year, day, hour, min, sec);
    }    
}
