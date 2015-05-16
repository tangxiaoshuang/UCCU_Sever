/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author xiaoshuang
 */

public class GameServer implements Decoder, Register, Reaper{
    private Set<AioSession> gates; 
    private AioSession database;
    private AioModule aio;
    
    private boolean regEnable;
    private boolean createEnable;
    private int maxChar;
    
    private HashMap<Integer, Character> chars; 
    
    public GameServer(boolean reg, boolean crt, int max)
    {
        regEnable = reg;
        createEnable = crt;
        maxChar = max;
        gates = new HashSet<>();
        chars = new HashMap<>();
    }
    public void init(AioModule a, String DBHost, int DBPort)
    {
        aio = a;
        database = aio.connect(DBHost, DBPort, new DatabaseDecoder(), new DatabaseReaper());
        ByteBuffer msg = ByteBuffer.allocate(8);
        msg.putInt(12345);
        msg.flip();
        database.write(Datagram.wrap(msg, Target.DB, 0x00));
    }
    
    public Character loadCharacter(int sessionID, int id)
    {
        if(chars.containsKey(id))
        {
            return chars.get(id);
        }
        
        ByteBuffer msg = ByteBuffer.allocate(32);
        
        //按照数据包定义向DB申请得到角色id的角色信息
        //在DatabaseDecoder中发送消息给Gate
        return null;
    }
    
    class DatabaseDecoder implements Decoder
    {
        @Override
        public void decode(ByteBuffer buffer, AioSession session)
        {
            ByteBuffer datagram = Datagram.getDatagram(buffer);
            if(datagram == null)
                return;
            char sn = Datagram.trim(datagram);
            switch(sn)
            {
                case 0x0301://与DB连接无误，开始监听Gate
                    aio.asyncAccept();
                    break;
            }
        }
    }
    
    class DatabaseReaper implements Reaper
    {
        @Override
        public void reap(AioSession session)
        {
            System.out.println("Session " + session.getRemoteSocketAddress() + " has disconnected!");
        }
    }
    
    @Override
    public void decode(ByteBuffer buffer, AioSession session)//处理与Gate之间的数据交流
    {
        ByteBuffer datagram = Datagram.getDatagram(buffer);
        if(datagram == null)
            return;
        char sn = Datagram.trim(datagram);
        ByteBuffer msg = ByteBuffer.allocate(256);
        switch(sn)
        {
            case 0x0100:
            {
                int hello = datagram.getInt();
                byte reg = (byte)(regEnable?1:0);
                byte crt = (byte)(createEnable?1:0);
                msg.put(reg);
                msg.put(crt);
                msg.putInt(maxChar);
                msg.flip();
                session.write(Datagram.wrap(msg, Target.Gate, 0x01));
                break;
            }
            case 0x0109:
            {
                int sessionID = datagram.getInt();
                int id = datagram.getInt();
                Character c = loadCharacter(sessionID, id);
                if(c != null)
                {
                    msg.putInt(sessionID);
                    msg.put(c.pack());//写入打包后的人物信息
                    //外观等
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x0A));
                }
                break;
            }   
            case 0x010B:
            {
                int id = datagram.getInt(4);
                int posX = datagram.getInt(8);
                int posY = datagram.getInt(12);
                
                synchronized(chars.get(id))
                {
                    Character c = chars.get(id);
                    if((Math.abs(c.posX-posX) < 10) && (Math.abs(c.posY-posY) < 10))
                    {
                        c.posX = posX;
                        c.posY = posY;
                        session.write(Datagram.wrap(datagram, Target.Gate, 0x0C));
                    }
                }
                break;
            }
            case 0x010D:
            {
                int id = datagram.getInt(4);
                synchronized(chars.get(id))
                {
                    Character c = chars.get(id);
                    if(c.canChat())
                    {
                        c.resetTimer(0, 0L);
                        session.write(Datagram.wrap(datagram, Target.Gate, 0x0F));
                    }
                    else
                    {
                        msg.putInt(datagram.getInt());//添加sessionID
                        msg.putInt(0);//拒绝由于说话间隔太短
                        session.write(Datagram.wrap(msg, Target.Gate, 0x0E));
                    }
                }
                break;
            }
            case 0x0110:
            {
                int id = datagram.getInt(4);
                synchronized(chars.get(id))
                {
                    Character c = chars.get(id);
                    if(c.canChat())
                    {
                        c.resetTimer(0, 0L);
                        session.write(Datagram.wrap(datagram, Target.Gate, 0x12));
                    }
                    else
                    {
                        msg.putInt(datagram.getInt(0));
                        msg.putInt(0);
                        msg.putInt(datagram.getInt(8));
                        session.write(Datagram.wrap(msg, Target.Gate, 0x11));
                    }
                }
                break;
            }
        }
    }
    @Override
    public boolean register(AioSession session, AioModule aio)
    {
        gates.add(session);
        return true;
    }
    @Override
    public void reap(AioSession session)
    {
        System.out.println("Session " + session.getRemoteSocketAddress() + " has disconnected!");
    }
}
