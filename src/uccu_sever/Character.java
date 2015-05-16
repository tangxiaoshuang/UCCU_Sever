/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 *
 * @author xiaoshuang
 */
public class Character {
    int id;
    String name;
    int level;
    byte gender;
    int posX;
    int posY;
    HashMap<Integer, UCCUTimer> timers; //所有的计时器
    /*
        0 聊天计时器，限制刷屏
    
    
    
        */
    
    public Character()
    {
       //默认构造
    }
    public Character(int _id, String _name, int _level, byte _gender, int _posX, int _posY)
    {
        id = _id;
        name = _name;
        level = _level;
        gender = _gender;
        posX = _posX;
        posY = _posY;
        timers = new HashMap<>();
        timers.put(0, new UCCUTimer());
        timers.get(0).reset(0);
    }
    
    public long getMS(int id)
    {
        synchronized(timers)
        {
            if(timers.containsKey(id))
                return timers.get(id).getMS();
        }
        return -1;
    }
    
    public long getS(int id)
    {
        long res = getMS(id);
        if(res == -1)
            return -1;
        return res/1000L;
    }
    
    public void resetTimer(int id, long startTime)
    {
        synchronized(timers)
        {
            if(timers.containsKey(id))
                timers.get(id).reset(startTime);
            else
                timers.put(id, new UCCUTimer());
            timers.get(id).reset(startTime);
        }
    }
    
    public boolean canChat()//Ensure this operation is synchronized.
    {
        return getMS(0) >= Const.MIN_CHAT_INTERVAL;
    }
    
    
    public ByteBuffer pack()
    {
        ByteBuffer msg = ByteBuffer.allocate(128);
        msg.putInt(id);
        msg.put(name.getBytes());
        msg.put((byte)level);
        msg.put(gender);
        msg.putInt(posX);
        msg.putInt(posY);
        msg.flip();
        return msg;
    }
}
