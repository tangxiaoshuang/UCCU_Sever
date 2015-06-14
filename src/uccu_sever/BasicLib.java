/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.security.MessageDigest;

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
    static String DBAddress = "192.168.1.102";
    static int DBPort = 55847;
    static String LoginAddress = "115.27.34.171";
    static int LoginPort = 50277;
    
    
    
    static String blank = "           ";
    static long MIN_CHAT_INTERVAL = 1000;//最小聊天间隔
    
}


public class BasicLib {
    public static String md5(String str) {
        StringBuffer sb = new StringBuffer(32);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] tmp = md.digest(str.getBytes("GBK"));
            for(int i = 0; i < tmp.length; ++i)
            {
                sb.append(Integer.toHexString((tmp[i]&0xFF)|0x100).toUpperCase().substring(1, 3));
            }
        } catch (Exception e) {
            UccuLogger.warn("BasicLib/MD5", "Can't calculate the MD5! "+e);
            return null;
        }
        return sb.toString();
    }
    public static double getDistance(int sx, int sy, int tx, int ty)
    {
        double res = (double)(sx-tx)*(double)(sx-tx) + (double)(sy-ty)*(double)(sy-ty);
        return Math.sqrt(res);
    }
    public static void movePoint(int sx, int sy, int tx, int ty, int d)
    {
        
    }
}
