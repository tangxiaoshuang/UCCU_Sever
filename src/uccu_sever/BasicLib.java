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

class Const
{
    static String[] gateAddress;
    static int[] gatePort;
    static String gameServerAddress;
    static int gameServerPort;
    static String DBAddress;
    static int DBPort;
    
    static long MIN_CHAT_INTERVAL = 1000;//最小聊天间隔
    
}


class UCCUTimer
{
    private long startTime;
    private long initTime;
    public UCCUTimer()
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
    
    
    
}

public class BasicLib {
    
}
