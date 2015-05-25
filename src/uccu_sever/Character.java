/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    boolean dirty; //标记是否被修改过
    HashMap<Integer, UCCUTimer> timers; //所有的计时器
    Lock lock;
    /*
        0 聊天计时器，限制刷屏
    
    
    
        */
    
    private Character(int _id, String _name, int _level, byte _gender, int _posX, int _posY)
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
        lock = new ReentrantLock();
    }
    public static Character unpack(ByteBuffer bf)
    {
        int id = bf.getInt();
        String name = Datagram.extractString(bf);
        int level = bf.get();
        byte gender = bf.get();
        int posX = bf.getInt();
        int posY = bf.getInt();
        Character newchar = new Character(id, name, level, gender, posX, posY);
        return newchar;
    }
    public void update(ByteBuffer bf)
    {
        synchronized(this)
        {
            name = Datagram.extractString(bf);
            level = bf.get();
            gender = bf.get();
            posX = bf.getInt();
            posY = bf.getInt();
        }
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
    
    public void move(int x, int y)
    {
        synchronized(this)
        {
            posX = x;
            posY = y;
            dirty = true;
        }
    }
    
    public ByteBuffer pack()
    {
        ByteBuffer msg = ByteBuffer.allocate(128);
        synchronized(this)
        {
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
}
