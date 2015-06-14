/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.CharacterManager;
import Entities.Character;
import Entities.ItemInstance;
import Entities.Managers;
import Entities.SkillInstance;
import java.nio.ByteBuffer;
import uccu_sever.AioSession;
import uccu_sever.Datagram;
import uccu_sever.GameServer;
import uccu_sever.Point;
import uccu_sever.Server;
import uccu_sever.Target;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class LogicExecutor implements Runnable{
    
    public static CharacterManager characterManager = Managers.characterManager;
    public static GameServer gameServer = Server.gameServer;
    
    char sn;
    ByteBuffer datagram;
    AioSession session;//可以换成GateID来处理
    
    public LogicExecutor(char sn, ByteBuffer buf, AioSession session)
    {
        this.sn = sn;
        this.datagram = buf;
        this.session = session;
    }
    
    @Override
    public void run() {//          LogicExecutor/Run
        ByteBuffer msg = ByteBuffer.allocate(2048);
        
        Gate gate = Managers.getGate(session);
        
        switch(sn)
        {
            case 0x0100://验证连接
            {
                UccuLogger.debug("GameServer/Decode", "Get hello from gate.");
                int hello = datagram.getInt();
                byte reg = (byte)(gameServer.regEnable?1:0);
                byte crt = (byte)(gameServer.createEnable?1:0);
                msg.put(reg);
                msg.put(crt);
                msg.putInt(gameServer.maxChar);
                msg.flip();
                session.write(Datagram.wrap(msg, Target.Gate, 0x01));
                UccuLogger.debug("GameServer/Decode", "Send status to gate.");
                msg.clear();
                //发送 Ping
                msg.putLong(System.currentTimeMillis());
                session.write(Datagram.wrap(msg, Target.Gate, 0x14));
                UccuLogger.debug("GameServer/Decode", "Send Ping to gate.");
                break;
            }
            
            case 0x0109:
            {
                int sessionID = datagram.getInt();
                int id = datagram.getInt();
                UccuLogger.log("LogicExecutor/Run", "Add new character("+id+")!");
                Character cha = gameServer.loadCharacter(gate.id, sessionID, id);
                if(cha != null)//缓存命中！
                {
                    //发送基本信息
                    msg.putInt(sessionID);
                    cha.pack(msg);
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x0A));
                    msg.clear();
                    
                    //发送背包信息
                    msg.putInt(sessionID);
                    cha.packInventoryToClient(msg);
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x19));
                    msg.clear();
                    
                    //发送技能信息
                    msg.putInt(sessionID);
                    cha.packSkillScrollToClient(msg);
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x1A));
                    msg.clear();
                    
                    //发送冷却信息
                    msg.putInt(sessionID);
                    cha.packColdDown(msg);
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x1B));
                    msg.clear();
                    
                    if(!cha.hasSkill("全局聊天"))
                        cha.addSkill("全局聊天", 1, 0);
                    if(!cha.hasSkill("私人聊天"))
                        cha.addSkill("私人聊天", 1, 0);
                    
                    UccuLogger.debug("LogicExecutor/Run", "Send character("+id+") data to gate.");
                }
                break;
            }
            
            
            case 0x010B://角色移动意图
            {
                int id = datagram.getInt(4);
                int tx = datagram.getInt(8);
                int ty = datagram.getInt(12);
                long globalTime = datagram.getLong(16);
                Character cha = null;
                try {
                    cha = Managers.getCharacter(id);
                } catch (Exception e) {
                    UccuLogger.warn("LogicExecutor/Run", ""+(Integer)((int)sn)+e.toString());
                    break;
                }
                
                Point tp = new Point(tx, ty);
                Point sp = cha.getPos();
                
                long deltaT = System.currentTimeMillis() - globalTime;//已经移动了的时间
                int d = (int) (cha.moveSpeed * deltaT / 1000);//已经移动过的距离
                Point end = sp.movePointTrunc(tp, d);
                
                
                if(sp.disFrom(tp) < 2000.0)//单次移动距离上限
                {
                    cha.setPos(end);
                    TimeEvent event = new TimeEvent(tx, ty, id, 20)
                    {
                        @Override
                        public void exec()
                        {
                            playerMove();
                        }
                    };
                    Daemons.addSingleEvent(event);
                    session.write(Datagram.wrap(datagram, Target.Gate, 0x0C));
                    UccuLogger.debug("LogicExecutor/Run", "Player "+id+ " is moving to "+tp);
                }
                //应该处理无法移动的情况    
                break;
            }
            
            
            case 0x010D:
            {
                int sessionID = datagram.getInt();
                int id = datagram.getInt();
                String chat = Datagram.extractString(datagram);
                
                Character cha = null;
                try {
                    cha = Managers.getCharacter(id);
                } catch (Exception e) {
                    UccuLogger.warn("LogicExecutor/Run", ""+(Integer)((int)sn)+e.toString());
                    break;
                }
                int res = 0;
                if(!cha.hasSkill("全局聊天"))//没有全局聊天权限
                    res = 1;
                else if(!cha.cdCompleted("全局聊天"))//全局聊天没冷却好
                    res = 0;
                else //可以聊天
                {
                    msg.putInt(sessionID);
                    msg.putInt(id);
                    //可以过滤敏感词
                    Datagram.restoreString(msg, chat);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x0F));
                    UccuLogger.debug("LogicExecutor/Run", "Player "+id+ " is yelling!");
                    cha.startCd("全局聊天");
                    break;
                }
                //不能说话
                msg.putInt(sessionID);
                msg.putInt(res);
                session.write(Datagram.wrap(msg, Target.Gate, 0x0E));
                UccuLogger.debug("LogicExecutor/Run", "Player "+id+ " CAN'T yelling! For reason "+res);
                break;
            }
            
            case 0x0110:
            {
                int sessionID = datagram.getInt();
                int id = datagram.getInt();
                String receName = Datagram.extractString(datagram);
                String chat = Datagram.extractString(datagram);
                
                Character chaA = null;
                try {
                    chaA = Managers.getCharacter(id);
                } catch (Exception e) {
                    UccuLogger.warn("LogicExecutor/Run", ""+(Integer)((int)sn)+e.toString());
                    break;
                }
                
                Character chaB = null;
                try {
                    chaB = Managers.getCharacter(receName);
                } catch (Exception e) {//对象不存在游戏中
                    msg.putInt(sessionID);
                    msg.putInt(1);//这里简单处理，相当于在黑名单中
                    Datagram.restoreString(msg, receName);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x11));
                    UccuLogger.debug("LogicExecutor/Run", "player "+id + "CAN'T talk to "+receName +". Not ONLINE!");
                    break;
                }
                
                int res = 0;
                if(!chaA.hasSkill("私人聊天"))//没有全局聊天权限,有点问题
                    res = 1;
                else if(!chaA.cdCompleted("私人聊天"))//全局聊天没冷却好
                    res = 0;
                else //可以聊天
                {
                    msg.putInt(sessionID);
                    msg.putInt(id);
                    Datagram.restoreString(msg, receName);
                    //可以过滤敏感词
                    Datagram.restoreString(msg, chat);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x12));
                    UccuLogger.debug("LogicExecutor/Run", "Player "+id+ " is talking to "+receName);
                    chaA.startCd("私人聊天");
                    break;
                }
                //不能说话
                msg.putInt(sessionID);
                msg.putInt(res);
                Datagram.restoreString(msg, receName);
                session.write(Datagram.wrap(msg, Target.Gate, 0x0E));
                UccuLogger.debug("LogicExecutor/Run", "Player "+id+ " CAN'T talk! For reason "+res);
                break;
            }
            
            case 0x0113:
            {
                int id = datagram.getInt();
                int res = datagram.getInt();
                
                Character chaA = null;
                try {
                    chaA = Managers.getCharacter(id);
                } catch (Exception e) {
                    UccuLogger.warn("LogicExecutor/Run", ""+(Integer)((int)sn)+e.toString());
                    break;
                }
                chaA.lockWrite();
                chaA.online = false;
                chaA.unlockWrite();
                
                UccuLogger.debug("LogicExecutor/Run", "Player "+id+ " is OFF LINE!"+res);
                break;
            }
            
            case 0x0115:
            {
                long timestamp = datagram.getLong();
                
                gate.ping = (System.currentTimeMillis() - timestamp)/2;
                long globalTime = System.currentTimeMillis() + gate.ping;
                msg.putLong(globalTime);
                session.write(Datagram.wrap(msg, Target.Gate, 0x16));
                UccuLogger.note("LogicExecutor/Run", "Gate "+gate.id+ " return Ping : "+gate.ping + "ms.");
                UccuLogger.log("LogicExecutor/Run", "Gate "+gate.id+ " GlobalTime : " + globalTime);

                break;
            }
            
            case 0x0117://使用物品数据包
            {
                int sessionID = datagram.getInt();
                int chaId1 = datagram.getInt();
                int itemInsId = datagram.getInt();
                int optype = datagram.getInt();
                int chaId2 = datagram.getInt();
                
                ItemInstance itemIns = null;
                Character chaA = null;
                Character chaB = null;
                try {
                    itemIns = Managers.getItemInstance(itemInsId);
                    chaA = Managers.getCharacter(chaId1);
                    if(chaId2 != -1)
                        chaB = Managers.getCharacter(chaId2);
                    else
                        chaB = chaA;
                } catch (Exception e) {
                    UccuLogger.warn("LogicExecutor/Run", e.toString());
                }
                
                if(optype == 0)
                    itemIns.trigger(chaA, chaB, null);
                //处理丢弃
                
                if(itemIns.hasTag("attr1"))
                {
                    msg.putInt(chaId1);
                    chaA.pack(msg);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x1C));
                    UccuLogger.debug("LogicExecutor/Run", "Update Player "+chaId1+ " attr!");
                    msg.clear();
                }
                if(itemIns.hasTag("attr2"))
                {
                    msg.putInt(chaId2);
                    chaA.pack(msg);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x1C));
                    UccuLogger.debug("LogicExecutor/Run", "Update Player "+chaId2+ " attr!");
                }
                
                break;
            }
            
            case 0x0118://使用技能数据包
            {
                int sessionID = datagram.getInt();
                int chaId1 = datagram.getInt();
                int skillInsId = datagram.getInt();
                int optype = datagram.getInt();
                int chaId2 = datagram.getInt();
                
                SkillInstance skillIns = null;
                Character chaA = null;
                Character chaB = null;
                try {
                    skillIns = Managers.getSkillInstance(skillInsId);
                    chaA = Managers.getCharacter(chaId1);
                    if(chaId2 != -1)
                        chaB = Managers.getCharacter(chaId2);
                    else
                        chaB = chaA;
                } catch (Exception e) {
                    UccuLogger.warn("LogicExecutor/Run", e.toString());
                }
                
                if(optype == 0)
                    skillIns.cast(chaA, chaB, null);
                
                //处理丢弃
                if(skillIns.hasTag("attr1"))
                {
                    msg.putInt(chaId1);
                    chaA.pack(msg);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x1C));
                    UccuLogger.debug("LogicExecutor/Run", "Update Player "+chaId1+ " attr!");
                    msg.clear();
                }
                if(skillIns.hasTag("attr2"))
                {
                    msg.putInt(chaId2);
                    chaA.pack(msg);
                    session.write(Datagram.wrap(msg, Target.Gate, 0x1C));
                    UccuLogger.debug("LogicExecutor/Run", "Update Player "+chaId2+ " attr!");
                }
                //发送更新包在脚本中执行
                
                break;
            }
        }
    }
}
