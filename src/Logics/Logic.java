/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logics;

import Entities.AttributionEntity;
import Entities.KvPair;
import Entities.MutexValue;
import Entities.Character;
import GameServer.Gate;
import java.nio.ByteBuffer;
import uccu_sever.Datagram;
import uccu_sever.Target;
import uccu_sever.UccuLogger;
/**
 *
 * @author xiaoshuang
 */
public class Logic extends MutexValue<String>{
    //一些基本动作的封装
    //示例：
    public void setName(String name)
    {
        this.name = name;
    }
    public Logic() {
        super(null);
    }
    
    public void doAction(KvPair a1, KvPair a2, KvPair a3, KvPair a4)//涉及的参数
    {
        lockRead();
        //执行一些行为
        unlockRead();
    }
    public void doAction()
    {
        
    }
    public static void playerEffect(Character a, Character b, int no)//a->b
    {
        ByteBuffer msg = ByteBuffer.allocate(128);
        msg.putInt(no);
        msg.putInt(a.id);
        msg.putInt(b.id);
        msg.flip();
        
        a.gate.session.write(Datagram.wrap(msg, Target.Gate, 0x1D));
        UccuLogger.debug("Logic/PlayerEffect", "Player "+a.id+" cast effect "+no+" to player "+b.id);
    }
    public static void playerEffect(Character a, int no)//a->b
    {
        ByteBuffer msg = ByteBuffer.allocate(128);
        msg.putInt(no);
        msg.putInt(a.id);
        msg.putInt(a.id);
        msg.flip();
        
        a.gate.session.write(Datagram.wrap(msg, Target.Gate, 0x1D));
        UccuLogger.debug("Logic/PlayerEffect", "Player "+a.id+" add effect "+no);
    }
    public static void attrUpdate(AttributionEntity a, Gate gate)//a->b
    {
        ByteBuffer msg = ByteBuffer.allocate(1024);
        a.pack(msg);
        msg.flip();
        gate.session.write(Datagram.wrap(msg, Target.Gate, 0x1C));
        UccuLogger.debug("Logic/PlayerEffect", "Update entity "+a.id+" ATTR");
    }
}
