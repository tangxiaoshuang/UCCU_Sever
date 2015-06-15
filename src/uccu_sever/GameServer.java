/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import Entities.Managers;
import Entities.Character;
import GameServer.Daemons;
import GameServer.Gate;
import GameServer.LogicExecutorService;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Timer;

/**
 *
 * @author xiaoshuang
 */

public class GameServer implements Decoder, Register, Reaper{
    
    public AioSession database;
    private AioModule aio;
    
    public boolean regEnable;
    public boolean createEnable;
    public int maxChar;
    
    private UccuTimer timer;//记录服务器运行时间
    private Timer deamonTimer;
    private LogicExecutorService logicService;
    
    
    public GameServer(boolean reg, boolean crt, int max)
    {
        regEnable = reg;
        createEnable = crt;
        maxChar = max;
        deamonTimer = new Timer(true);
        logicService = new LogicExecutorService(8);
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
        Daemons.start(15000, 30000);
        UccuLogger.log("GameServer/Init", "RestoreDeamon started!");
        UccuLogger.kernel("GameServer/Init", "Init done!");
    }
    
    public Character loadCharacter(int gateID, int sessionID, int id)
    {
        try {
            return Managers.getCharacter(id);
        } catch (Exception e) {
            UccuLogger.debug("GameServer/LoadCharacter", "Character "+id +" cache missed!");
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
            ByteBuffer msg = ByteBuffer.allocate(2048);
            while (true) {
                msg.clear();
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
                        
                        Character cha = Managers.newCharacter(datagram);
                        
                        cha.gate = Managers.getGate(gateID);
                        cha.online = true;
                        
                        datagram.position(4);
                        datagram.compact();
                        datagram.flip();
                        
                        UccuLogger.debug("Database/Decode", "Get character info id="+id +" from Database");
                        cha.gate.session.write(Datagram.wrap(datagram, Target.Gate, 0x0A));
                        UccuLogger.debug("Database/Decode", "Send character info id="+id +" to GateServer"+gateID);
                        break;
                    }
                    case 0x0305:
                    {
                        int id = datagram.getInt();
                        if(Managers.hasCharacter(id))//数据库数据比本地数据旧，不需要这个包的数据
                        {
                            UccuLogger.debug("Database/Decode", "Cancelled cache character info id="+id +" from Database, info already cached!");
                            return;
                        }
                        datagram.position(0);
                        Managers.newCharacter(datagram);
                        UccuLogger.debug("Database/Decode", "Cached character info id="+id +" from Database");
                    }
                    case 0x0306: // 背包信息
                    {
                        int gateID = datagram.getInt();
                        int sessionID = datagram.getInt();
                        int id = datagram.getInt();
                        UccuLogger.debug("Database/Decode", "Get Inventory info id="+id +" from Database");
                        
                        while(!Managers.hasCharacter(id));
                        Character cha;
                        try {
                            cha = Managers.getCharacter(id);
                        } catch (Exception e) {
                            UccuLogger.warn("Database/Decode", e.toString());
                            break;
                        }
                        if(datagram.getInt(datagram.position()) == 0)//新玩家初次进入游戏
                            cha.dirty = true;
                        cha.loadInventory(datagram);
                        
                        if(!cha.hasItem("小型修复水晶"))
                            cha.addItem("小型修复水晶", 5);
                        
                        msg.putInt(sessionID);
                        cha.packInventoryToClient(msg);
                        msg.flip();
                        
                        Gate gate = Managers.getGate(gateID);
                        
                        gate.session.write(Datagram.wrap(msg, Target.Gate, 0x19));
                        UccuLogger.debug("Database/Decode", "Send  Inventory info id="+id +" to Gate");
                       
                        break;
                    }
                    case 0x0308: // 冷却信息
                    {
                        int gateID = datagram.getInt();
                        int sessionID = datagram.getInt();
                        int id = datagram.getInt();
                        UccuLogger.debug("Database/Decode", "Get colddown info id="+id +" from Database");

                        
                        while(!Managers.hasCharacter(id));
                        Character cha;
                        try {
                            cha = Managers.getCharacter(id);
                        } catch (Exception e) {
                            UccuLogger.warn("Database/Decode", e.toString());
                            break;
                        }
                        cha.loadColdDown(datagram);
                        
                        msg.putInt(sessionID);
                        cha.packColdDown(msg);
                        msg.flip();
                        
                        Gate gate = Managers.getGate(gateID);
                        
                        gate.session.write(Datagram.wrap(msg, Target.Gate, 0x1B));
                        UccuLogger.debug("Database/Decode", "Send ColdDown info id="+id +" to Gate");
                        
                        break;
                    }   
                    case 0x030A: // 好友信息
                    {
                        break;
                    }
                    case 0x030C: // 技能信息
                    {
                        int gateID = datagram.getInt();
                        int sessionID = datagram.getInt();
                        int id = datagram.getInt();
                        UccuLogger.debug("Database/Decode", "Get SkillScroll info id="+id +" from Database");
                        
                        while(!Managers.hasCharacter(id));
                        Character cha;
                        try {
                            cha = Managers.getCharacter(id);
                        } catch (Exception e) {
                            UccuLogger.warn("Database/Decode", e.toString());
                            break;
                        }
                        cha.loadSkillScroll(datagram);
                        
                        if(!cha.hasSkill("全局聊天"))
                            cha.addSkill("全局聊天", 1, 0);
                        if(!cha.hasSkill("私人聊天"))
                            cha.addSkill("私人聊天", 1, 0);
                        if(!cha.hasSkill("攻击"))
                            cha.addSkill("攻击", 1, 0);
                        
                        msg.putInt(sessionID);
                        cha.packSkillScrollToClient(msg);
                        msg.flip();
                        
                        Gate gate = Managers.getGate(gateID);
                        
                        gate.session.write(Datagram.wrap(msg, Target.Gate, 0x1A));
                        UccuLogger.debug("Database/Decode", "Send SkillScroll info id="+id +" to Gate");
                        
                        break;
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
    
    @Override
    public void decode(ByteBuffer buffer, AioSession session)//处理与Gate之间的数据交流
    {
        while (true) {            
            ByteBuffer datagram = Datagram.getDatagram(buffer);
            if(datagram == null)//直到没有有效的数据包才返回
                return;
            //此处添加安全处理部分，检测非法连接
            char sn = Datagram.trim(datagram);
            
            UccuLogger.note("GameServer/Decode", "Submit new task!");
            logicService.handle(sn, datagram, session);
        
        }
    }
    @Override
    public boolean register(AioSession session, AioModule aio)
    {
        Gate gate = Managers.newGate(session);
        UccuLogger.log("GateSession/Register","New GateServer"+session.getRemoteSocketAddress()+" connected with id = "+gate.id);
        return true;
    }
    @Override
    public void reap(AioSession session)
    {
        Gate gate = Managers.getGate(session);
        Managers.removeGate(session);
        UccuLogger.warn("GateSession/Reap","Lost connection with GateServer"+session.getRemoteSocketAddress()+" id = "+gate.id);
        //停止向该Gate发送信息，主要注意DatabaseDecoder中行为。
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
        
        Managers.load();
        
        AioModule aio = new AioModule(gs, gs, gs);
        try {
            aio.init(InetAddress.getLocalHost().getHostAddress(), 8998, 4);
        }
        catch (Exception e) {
            UccuLogger.warn("Main", "Can't get localhost name. "+e);
            return;
        }
        gs.init(aio, Const.DBAddress, Const.DBPort);
        
        sh.setCore(gs);
        sh.startShell();
    }
}