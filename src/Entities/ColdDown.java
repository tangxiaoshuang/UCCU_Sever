/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import uccu_sever.UccuLogger;
import uccu_sever.UccuTimer;

/**
 *
 * @author xiaoshuang
 */
public class ColdDown extends KvPair{//记录冷却的单元
    UccuTimer timer;
    Skill skill;
    
    public ColdDown(int id)
    {
        super(id, null);
        try {
            this.skill = Managers.skillManager.get(id);
            this.name = this.skill.name;
        } catch (Exception e) {
            UccuLogger.warn("ColdDown/Constructor", e.getMessage());
        }
        timer.reset(0);
    }
    public ColdDown(String name)
    {
        super(0, name);
        try {
            this.skill = Managers.skillManager.get(name);
            this.id = this.skill.id;
        } catch (Exception e) {
            UccuLogger.warn("ColdDown/Constructor", e.getMessage());
        }
        timer.reset(0);
    }
    public ColdDown(ByteBuffer bf)
    {
        super(0, null);
        
    }
            
    public boolean isCompleted()//冷却结束， 改变技能冷却的逻辑可以在这里扩展
    {
        lockRead();
        try
        {
            return timer.getMS()>skill.coldDown;
        }
        finally {
           unlockRead();
        }
    }
    public void restart()
    {
        lockWrite();
        try {
            this.timer.reset(0);
        } finally {
            unlockWrite();
        }
    }
}
