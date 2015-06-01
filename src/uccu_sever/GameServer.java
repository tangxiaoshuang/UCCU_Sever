/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author xiaoshuang
 */

public class GameServer implements Decoder, Register, Reaper{
    private HashMap<AioSession, Integer> gates2id; //处理多Gate情况
    private HashMap<Integer, AioSession> id2gates;
    private Integer gaten = 0;//当前在线Gate数 
    private Integer nextGateId = 0;//下一个分配给Gate的ID
    
    private AioSession database;
    private AioModule aio;
    
    private boolean regEnable;
    private boolean createEnable;
    private int maxChar;
    
    private UccuTimer timer;//记录服务器运行时间
    private Timer deamonTimer;
    
    private HashMap<Integer, Character> chars; 
    
    public GameServer(boolean reg, boolean crt, int max)
    {
        regEnable = reg;
        createEnable = crt;
        maxChar = max;
        gates2id = new HashMap<>();
        id2gates = new HashMap<>();
        chars = new HashMap<>();
        deamonTimer = new Timer(true);
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
        timer = new UccuTimer();
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
        deamonTimer.schedule(new RestoreDeamon(), 15000, 30000);
        UccuLogger.log("GameServer/Init", "RestoreDeamon started!");
        UccuLogger.kernel("GameServer/Init", "Init done!");
    }
    
    public Character loadCharacter(int gateID, int sessionID, int id)
    {
        synchronized(chars)
        {
            if(chars.containsKey(id))
            {
                return chars.get(id);
            }    
        }
        //本地无数据
        ByteBuffer msg = ByteBuffer.allocate(32);
        msg.putInt(gateID);
        msg.putInt(sessionID);
        msg.putInt(id);
        msg.flip();
        database.write(Datagram.wrap(msg, Target.DB, 0x02));
        UccuLogger.debug("GameServer/LoadCharacter", "Asking data from Database......");
        return null;
    }
    
    class DatabaseDecoder implements Decoder
    {
        @Override
        public void decode(ByteBuffer buffer, AioSession session)
        {
            while (true) {                
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
                    case 0x0303:
                    {
                        int gateID = datagram.getInt();
                        int id = datagram.getInt(8);
                        datagram.position(8);
                        Character newchar = Character.unpack(datagram);
                        datagram.position(4);
                        datagram.compact();
                        datagram.flip();
                        synchronized(chars)
                        {
                            chars.put(id, newchar);//加载到游戏服务器本地
                        }
                        UccuLogger.debug("Database/Decode", "Get character info id="+id +" from Database");
                        id2gates.get(gateID).write(Datagram.wrap(datagram, Target.Gate, 0x0A));
                        UccuLogger.debug("Database/Decode", "Send character info id="+id +" to GateServer"+gateID);
                        break;
                    }
                    case 0x0305:
                    {
                        int id = datagram.getInt();
                        synchronized(chars)
                        {
                            if(!chars.containsKey(id))
                            {
                                Character newchar = Character.unpack(datagram);
                                chars.put(id, newchar);
                                UccuLogger.debug("Database/Decode", "Cached character info id="+id +" from Database");
                            }
                            else
                            {
                                UccuLogger.debug("Database/Decode", "Cancelled cache character info id="+id +" from Database, info already cached!");
                            }
                        }
                    }
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
    
    public int getGateId(AioSession session)//安全隐患
    {
        return gates2id.get(session);
    }
    
    @Override
    public void decode(ByteBuffer buffer, AioSession session)//处理与Gate之间的数据交流
    {
        while (true) {            
            ByteBuffer datagram = Datagram.getDatagram(buffer);
            if(datagram == null)
                return;
            //此处添加安全处理部分，检测非法连接
            char sn = Datagram.trim(datagram);
            int gateID = gates2id.get(session);
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
                    Character c = loadCharacter(gateID, sessionID, id);
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
                            c.dirty = true;
                            session.write(Datagram.wrap(datagram, Target.Gate, 0x0C));
                            UccuLogger.debug("GameServer/Decode", "Character("+id+") moved to "+pos(posX, posY)+".");
                            UccuLogger.debug("GameServer/Decode", "Character("+id+") is at"+pos(c.posX, c.posY)+" now.");
                        }
                        else
                            UccuLogger.debug("GameServer/Decode", "Character("+id+") CAN'T move to "+pos(posX, posY)+".");
                            UccuLogger.debug("GameServer/Decode", "Character("+id+") is at"+pos(c.posX, c.posY)+" now.");
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
    }
    @Override
    public boolean register(AioSession session, AioModule aio)
    {
        int id;
        synchronized(gaten)
        {
            ++gaten;
            gates2id.put(session, nextGateId);
            id2gates.put(nextGateId, session);
            id = nextGateId;
            ++nextGateId;
        }
        UccuLogger.log("GateSession/Register","New GateServer"+session.getRemoteSocketAddress()+" connected with id = "+id);
        return true;
    }
    @Override
    public void reap(AioSession session)
    {
        
        int id;
        synchronized(gaten)
        {
            --gaten;
            id = gates2id.get(session);
            gates2id.remove(session, id);
            id2gates.remove(id, session);
        }
        UccuLogger.warn("GateSession/Reap","Lost connection with GateServer"+session.getRemoteSocketAddress()+"id = "+id);
        //停止向该Gate发送信息，主要注意DatabaseDecoder中行为。
    }
    
    private class RestoreDeamon extends TimerTask
    {
        @Override
        public void run() {
            UccuLogger.debug("RestoreDeamon/Run", "RestoreDeamon start restoring characters to Database.");
            
            ByteBuffer msg;
            int id, cnt = 0;
            HashMap<Integer, Character> tmp;
            Collection<Character> cs;
            
            synchronized(chars)
            {
                tmp = (HashMap<Integer, Character>)chars.clone();
            }
            cs = tmp.values();
            UccuLogger.debug("RestoreDeamon/Run", "Total characters in cache: "+ cs.size());
            
            Iterator itr = cs.iterator();
            while(itr.hasNext())
            {
                Character c = (Character)itr.next();
                synchronized(c)
                {
                    if(!c.dirty)
                        continue;
                    
                    msg = c.pack();
                    c.dirty = false;
                    id = c.id;
                    UccuLogger.debug("RestoreDeamon/Run", "Character id ="+ id +" Position "+pos(c.posX, c.posY));
                
                }
                database.write(Datagram.wrap(msg, Target.DB, 0x04));
                cnt++;
                UccuLogger.debug("RestoreDeamon/Run", "Restore character id ="+ id +" to Database!");
            }
            UccuLogger.kernel("RestoreDeamon/Run", cnt+" chacters restored.");
        }
    }
    public void time()
    {
        synchronized(System.out)
        {
            System.out.println(timer.getString());
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        Shell sh = new Shell();
        UccuLogger.setOptions("logs/GameServer/",LogMode.DEBUG);
        
        GameServer gs = Server.gameServer;
        
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
        
        sh.setCore(gs);
        sh.startShell();
    }
    private String pos(int x, int y)
    {
        return "("+x+", "+y+")";
    }
}
