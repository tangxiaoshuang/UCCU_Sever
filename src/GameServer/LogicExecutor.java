/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.CharacterManager;
import Entities.Managers;
import java.nio.ByteBuffer;
import uccu_sever.AioSession;
import uccu_sever.Datagram;
import uccu_sever.GameServer;
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
    }
    
    @Override
    public void run() {//          LogicExecutor/Run
        ByteBuffer msg = ByteBuffer.allocate(1024);
        
        int gateID = gameServer.getGateId(session);
        
        switch(sn)
        {
            case 0x0109:
            {
                int sessionID = datagram.getInt();
                int id = datagram.getInt();
                UccuLogger.log("LogicExecutor/Run", "Add new character("+id+")!");
                uccu_sever.Character c = gameServer.loadCharacter(gateID, sessionID, id);
                if(c != null)//缓存命中！
                {
                    msg.putInt(sessionID);
                    msg.put(c.pack());//写入打包后的人物信息
                    //外观等
                    msg.flip();
                    session.write(Datagram.wrap(msg, Target.Gate, 0x0A));
                    UccuLogger.debug("LogicExecutor/Run", "Send character("+id+") data to gate.");
                }
                break;
            }
            case 0x010B://角色移动意图
            {
//                int id = datagram.getInt(4);
//                int posX = datagram.getInt(8);
//                int posY = datagram.getInt(12);
//                UccuLogger.debug("LogicExecutor/Run", "Character("+id+") try to move to "+pos(posX, posY)+".");
//                try {
//                    characterManager.lockRead();
//                    characterManager.get(id).lockRead();
//                    
//                        Entities.Character c = characterManager.get(id);
//                        if((Math.abs(c.posX-posX) < 10) && (Math.abs(c.posY-posY) < 10))
//                        {
//                            c.posX = posX;
//                            c.posY = posY;
//                            c.dirty = true;
//                            session.write(Datagram.wrap(datagram, Target.Gate, 0x0C));
//                            UccuLogger.debug("LogicExecutor/Run", "Character("+id+") moved to "+pos(posX, posY)+".");
//                        }
//                        else
//                            UccuLogger.debug("LogicExecutor/Run", "Character("+id+") CAN'T move to "+pos(posX, posY)+".");
//                        UccuLogger.debug("LogicExecutor/Run", "Character("+id+") is at"+pos(c.posX, c.posY)+" now.");
//                    
//                } catch (Exception e) {
//                    UccuLogger.warn("LogicExecutor/Run", e.toString());
//                }
//                finally
//                {
//                    characterManager.get(id).lockRead();
//                    characterManager.unlockRead();
//                }
                break;
            }
            
        }
    }
    private String pos(int x, int y)
    {
        return "("+x+", "+y+")";
    }
}
