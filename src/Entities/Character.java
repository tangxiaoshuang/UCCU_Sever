/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import uccu_sever.Point;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class Character extends AttributionEntity{
    
    public boolean dirty; //标记是否被修改过
    public boolean online;
    ColdDownManager cdManager; //冷却管理模块
    Inventory inventory;//背包
    SkillScroll skillScroll;//技能
    Equipment equipment;//装备
    
    private Character(int id, String name, String description,
            int level, int gender, 
            int life, int curLife, int mana, int curMana, int atk, int def, int exp, int movespeed,
            int posX, int posY, int feature)
    {
        super(id, name, description, level, gender, life, curLife, mana, curMana, atk, def, exp, movespeed, posX, posY, feature);
        dirty = false;
        online = true;
        cdManager = new ColdDownManager();
        inventory = new Inventory(32);
    }
    public Character(ByteBuffer bf)
    {
        super(bf);
        dirty = false;
        online = true;
        skillScroll = null;
        inventory = null;
        cdManager = null;
    }
    public void loadSkillScroll(ByteBuffer bf)
    {
        skillScroll = new SkillScroll(bf);
    }
    
    public void loadInventory(ByteBuffer bf)
    {
        inventory = new Inventory(bf);
    }
    public void loadColdDown(ByteBuffer bf)
    {
        cdManager = new ColdDownManager(bf);
    }
    @Override
    public void pack(ByteBuffer bf)
    {
        super.pack(bf);
    }
    public void packSkillScroll(ByteBuffer bf)
    {
        skillScroll.pack(bf);
    }
    public void packSkillScrollToClient(ByteBuffer bf)
    {
        skillScroll.packToClient(bf);
    }
    public void packInventory(ByteBuffer bf)
    {
        inventory.pack(bf);
    }
    public void packInventoryToClient(ByteBuffer bf)
    {
        inventory.packToClient(bf);
    }
    public void packColdDown(ByteBuffer bf)
    {
        cdManager.pack(bf);
    }
    public Point getPos()
    {
        lockRead();
        try {
            return new Point(posX, posY);
        } finally {
            unlockRead();
        }
    }
    public void setPos(Point p)
    {
        lockWrite();
        this.posX = p.x;
        this.posY = p.y;
        unlockWrite();
    }
    
    
    public boolean cdCompleted(int id)
    {
        return cdManager.isCompleted(id);
    }
    public boolean cdCompleted(String name)
    {
        return cdManager.isCompleted(name);
    }
    public void startCd(int id)
    {
        cdManager.start(id);
    }
    public void startCd(String name)
    {
        cdManager.start(name);
    }
    
    
    public boolean hasSkill(int id)
    {
        return skillScroll.hasSkill(id);
    }
    public boolean hasSkill(String name)
    {
        return skillScroll.hasSkill(name);
    }
    
    public void addSkill(int id, int level, int exp)
    {
        try {
            skillScroll.add(Managers.newSkillInstance(id, level, exp));
        } catch (Exception ex) {
            UccuLogger.warn("Character/AddSkill", ex.toString());
        }
    }
    public void addSkill(String name, int level, int exp)
    {
        try {
            skillScroll.add(Managers.newSkillInstance(name, level, exp));
        } catch (Exception ex) {
            UccuLogger.warn("Character/AddSkill", ex.toString());
        }
    }
}
