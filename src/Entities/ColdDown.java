/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import uccu_sever.UccuLogger;
import uccu_sever.UccuTimer;

/**
 *
 * @author xiaoshuang
 */
public class ColdDown extends KvPair{//记录冷却的单元
    UccuTimer timer;
    Entity entity;
    
    public ColdDown(int id, String name, Entity entity)
    {
        super(id, name);
        this.entity = entity;
        this.timer = new UccuTimer();
    }
    public static ColdDown SkillColdDown(int id)//为区分技能和物品，对ID采用段加偏移的方法
    {
        Skill skill = null;
        try {
            skill = Managers.skillManager.get(id);
            return new ColdDown(skill.id, skill.name, skill);
        } catch (Exception e) {
            UccuLogger.warn("ColdDown/SkillColdDown", e.getMessage());
            return null;
        }
    }
    public static ColdDown ItemColdDown(int id)//为区分技能和物品，对ID采用段加偏移的方法
    {
        Item item = null;
        try {
            item = Managers.itemManager.get(id);
            return new ColdDown(item.id, item.name, item);
        } catch (Exception e) {
            UccuLogger.warn("ColdDown/ItemColdDown", e.getMessage());
            return null;
        }
    }
    public static ColdDown newColdDown(int id)//根据Id自动判断物品或技能
    {
        if(Skill.isSkill(id))
        {
            return SkillColdDown(id);            
        }
        else
        {
            return ItemColdDown(id);
        }
    }
    public static ColdDown newColdDown(String name)//根据Id自动判断物品或技能
    {
        try {
            Item item = Managers.itemManager.get(name);
            return new ColdDown(item.id, item.name, item);
        } catch (Exception e) {
        }
        try {
            Skill skill = Managers.skillManager.get(name);
            return new ColdDown(skill.id, skill.name, skill);
        } catch (Exception e) {
        }
        return null;
    }
    public boolean isCompleted()//冷却结束， 改变技能冷却的逻辑可以在这里扩展
    {
        lockRead();
        try
        {
            if(Skill.isSkill(id))
            {
                return ((Skill)entity).coldDown <= timer.getMS();            
            }
            else
            {
                return ((Item)entity).coldDown <= timer.getMS();  
            }
        }
        finally {
           unlockRead();
        }
    }
    public long getMS()
    {
        lockRead();
        try {
            return timer.getMS();
        } finally {
            unlockRead();
        }
    }
    public void restart(long startTime)
    {
        lockWrite();
        try {
            this.timer.reset(startTime);
        } finally {
            unlockWrite();
        }
    }
}
