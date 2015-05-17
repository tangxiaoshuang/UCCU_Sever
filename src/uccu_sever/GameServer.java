/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xiaoshuang
 */

public class GameServer implements Decoder, Register, Reaper{
    private Set<AioSession> gates; //处理多Gate情况
    private AioSession database;
    private AioModule aio;
    
    private boolean regEnable;
    private boolean createEnable;
    private int maxChar;
    
    private UCCUTimer timer;//记录服务器运行时间
    
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
        UccuLogger.kernel("GameServer/Init", "Initialize GameServer......");
        database = aio.connect(DBHost, DBPort, new DatabaseDecoder(), new DatabaseReaper());
        if(database == null)//连接失败
        {
            UccuLogger.log("GameServer/Init","Failed to connect the DatabaseServer at "+DBHost+":"+DBPort);
            return;
        }
        
        UccuLogger.log("GameServer/Init","Connected the DatabaseServer at "+DBHost+":"+DBPort);
        ByteBuffer msg = ByteBuffer.allocate(8);
        msg.putInt(12345);
        msg.flip();
        timer = new UCCUTimer();
        synchronized(timer)
        {
            database.write(Datagram.wrap(msg, Target.DB, 0x00));
            UccuLogger.log("GameServer/Init","Test connection with DatabaseServer......");
            database.asyncRead();
            try {
                timer.reset(0);
                timer.wait(5000);
            } catch (InterruptedException ex) {
                UccuLogger.warn("GameServer/Init", "Thread has been interrupted! "+ex);
                return;
            }
            if(timer.getMS()>4800)//等待超时，未收到回复
            {
                UccuLogger.warn("GameServer/Init", "Invalid connection. Init failed!");
                return;
            }
            UccuLogger.log("GameServer/Init", "Connection confirmed, try listening......");
            if(aio.asyncAccept())
                UccuLogger.log("GameServer/Init", "Started listening successfully!");
            else
            {
                UccuLogger.warn("GameServer/Init", "Failed to listen!");
                return;
            }
            timer.reset(0);
        }
        UccuLogger.kernel("GameServer/Init", "Init done!");
    }
    
    public Character loadCharacter(int sessionID, int id)
    {
        if(chars.containsKey(id))
        {
            return chars.get(id);
        }
        //本地无数据
        ByteBuffer msg = ByteBuffer.allocate(32);
        
        //按照数据包定义向DB申请得到角色id的角色信息
        //在DatabaseDecoder中发送消息给Gate
        UccuLogger.debug("GameServer/LoadCharacter", "Asking data from Database......");
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
                {
                    UccuLogger.debug("Database/Decode", "Get response from Database"+session.getRemoteSocketAddress()+".");
                    synchronized(timer)
                    {
                        timer.notify();
                    }
                    break;
                }
            }
        }
    }
    
    class DatabaseReaper implements Reaper
    {
        @Override
        public void reap(AioSession session)
        {
            UccuLogger.warn("DatabaseSession/Reap","Lost connection with Database"+session.getRemoteSocketAddress()+"!");
            //此处告诉Gate，需要断开连接。
        }
    }
    
    @Override
    public void decode(ByteBuffer buffer, AioSession session)//处理与Gate之间的数据交流
    {
        ByteBuffer datagram = Datagram.getDatagram(buffer);
        if(datagram == null)
            return;
        //此处添加安全处理部分，检测非法连接
        char sn = Datagram.trim(datagram);
        ByteBuffer msg = ByteBuffer.allocate(256);
        switch(sn)
        {
            case 0x0100://验证连接
            {
                UccuLogger.debug("GameServer/Decode", "Get hello from gate.");
                int hello = datagram.getInt();
                byte reg = (byte)(regEnable?1:0);
                byte crt = (byte)(createEnable?1:0);
                msg.put(reg);
                msg.put(crt);
                msg.putInt(maxChar);
                msg.flip();
                session.write(Datagram.wrap(msg, Target.Gate, 0x01));
                UccuLogger.debug("GameServer/Decode", "Send status to gate.");
                break;
            }
            case 0x0109://新角色加入游戏
            {
                int sessionID = datagram.getInt();
                int id = datagram.getInt();
                UccuLogger.log("GameServer/Decode", "Add new character("+id+")!");
                Character c = loadCharacter(sessionID, id);
                if(c != null)//缓存命中！
                {
                    msg.putInt(sessionID);
                    msg.put(c.pack());//写入打包后的人物信息
                    //外观等
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x0A));
                    UccuLogger.debug("GameServer/Decode", "Send character("+id+") data to gate.");
                }
                break;
            }   
            case 0x010B://角色移动意图
            {
                int id = datagram.getInt(4);
                int posX = datagram.getInt(8);
                int posY = datagram.getInt(12);
                UccuLogger.debug("GameServer/Decode", "Character("+id+") try to move to "+pos(posX, posY)+".");
                synchronized(chars.get(id))
                {
                    Character c = chars.get(id);
                    if((Math.abs(c.posX-posX) < 10) && (Math.abs(c.posY-posY) < 10))
                    {
                        c.posX = posX;
                        c.posY = posY;
                        session.write(Datagram.wrap(datagram, Target.Gate, 0x0C));
                        UccuLogger.debug("GameServer/Decode", "Character("+id+") moved to "+pos(posX, posY)+".");
                    }
                    else
                        UccuLogger.debug("GameServer/Decode", "Character("+id+") can't move to "+pos(posX, posY)+".");
                }
                break;
            }
            case 0x010D://全局喇叭请求
            {
                int id = datagram.getInt(4);
                UccuLogger.debug("GameServer/Decode", "Character("+id+") try to yell.");
                synchronized(chars.get(id))
                {
                    Character c = chars.get(id);
                    if(c.canChat())
                    {
                        c.resetTimer(0, 0L);
                        session.write(Datagram.wrap(datagram, Target.Gate, 0x0F));
                        UccuLogger.debug("GameServer/Decode", "Character("+id+") yelled.");
                    }
                    else
                    {
                        msg.putInt(datagram.getInt());//添加sessionID
                        msg.putInt(0);//拒绝由于说话间隔太短
                        msg.flip();
                        session.write(Datagram.wrap(msg, Target.Gate, 0x0E));
                        UccuLogger.debug("GameServer/Decode", "Character("+id+") can't yell.");
                    }
                }
                break;
            }
            case 0x0110:
            {
                int sendid = datagram.getInt(4);
                int recvid = datagram.getInt(8);
                UccuLogger.debug("GameServer/Decode", "Character("+sendid+") try to chat with "+"Character("+recvid+").");
                synchronized(chars.get(sendid))
                {
                    Character c = chars.get(sendid);
                    if(c.canChat())
                    {
                        c.resetTimer(0, 0L);
                        session.write(Datagram.wrap(datagram, Target.Gate, 0x12));
                        UccuLogger.debug("GameServer/Decode", "Character("+sendid+") chatted with "+"Character("+recvid+").");
                    }
                    else
                    {
                        msg.putInt(datagram.getInt(0));
                        msg.putInt(0);
                        msg.putInt(datagram.getInt(8));
                        msg.flip();
                        session.write(Datagram.wrap(msg, Target.Gate, 0x11));
                        UccuLogger.debug("GameServer/Decode", "Character("+sendid+") can't chat with "+"Character("+recvid+").");
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
        UccuLogger.warn("GateSession/Reap","Lost connection with GateServer"+session.getRemoteSocketAddress()+"!");
        //停止向该Gate发送信息，主要注意DatabaseDecoder中行为。
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        Shell sh = new Shell();
        UccuLogger.setOptions("logs/GameServer/",LogMode.NORMAL);
        
        GameServer gs = new GameServer(true, true, 100);
        
        UccuLogger.kernel("Main", "GameServer started!");
        AioModule aio = new AioModule(gs, gs, gs);
        try {
            aio.init(InetAddress.getLocalHost().getHostAddress(), 8998, 8);
        }
        catch (Exception e) {
            UccuLogger.warn("Main", "Can't get localhost name. "+e);
            return;
        }
        gs.init(aio, Const.DBAddress, Const.DBPort);
        sh.startShell();
    }
    private String pos(int x, int y)
    {
        return "("+x+", "+y+")";
    }
}
