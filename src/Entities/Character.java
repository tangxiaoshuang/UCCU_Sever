/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import GameServer.Gate;
import java.nio.ByteBuffer;
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
    
    //临时帮助变量
    public Point target;
    public Gate gate;
    
    private Character(int id, String name, String description,
            int level, int gender, 
            int life, int curLife, int mana, int curMana, int atk, int def, int exp, int movespeed,
            int posX, int posY, int feature)
    {
        super(id, name, description, level, gender, life, curLife, mana, curMana, atk, def, exp, movespeed, posX, posY, feature);
        dirty = false;
        online = true;
        target = new Point(-1,-1);
        cdManager = new ColdDownManager();
        inventory = new Inventory(32);
    }
    public Character(ByteBuffer bf)
    {
        super(bf);
        dirty = false;
        online = true;
        target = new Point(-1,-1);
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
        this.dirty = true;
        unlockWrite();
    }
    public void setTarget(Point p)
    {
        lockWrite();
        this.target = new Point(p);
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
        lockWrite();
        cdManager.start(id);
        this.dirty = true;
        unlockWrite();
    }
    public void startCd(String name)
    {
        lockWrite();
        cdManager.start(name);
        this.dirty = true;
        unlockWrite();
    }
    public void startCd(SkillInstance skillIns)
    {
        lockWrite();
        try {
            cdManager.start(skillIns.getName());
            this.dirty = true;
        } catch (Exception e) {
            UccuLogger.warn("Character/StartCd", e.toString());
        }
        unlockWrite();
    }
    
    public void startCd(ItemInstance itemIns)
    {
        lockWrite();
        try {
            cdManager.start(itemIns.getName());
            this.dirty = true;
        } catch (Exception e) {
            UccuLogger.warn("Character/StartCd", e.toString());
        }
        unlockWrite();
    }
    
    public boolean hasSkill(int id)
    {
        return skillScroll.hasSkill(id);
    }
    public boolean hasSkill(String name)
    {
        return skillScroll.hasSkill(name);
    }
    
    public boolean hasItemIns(ItemInstance itemIns)
    {
        return inventory.has(itemIns);
    }
    
    public boolean hasItem(String name)
    {
        return inventory.has(name);
    }
    
    public boolean hasItem(int id)
    {
        return inventory.has(id);
    }
    
    public void checkItemIns(ItemInstance itemIns)
    {
        inventory.check(itemIns);
    }
    
    public void addSkill(int id, int level, int exp)
    {
        lockWrite();
        try {
            skillScroll.add(Managers.newSkillInstance(id, level, exp));
            this.dirty = true;
        } catch (Exception ex) {
            UccuLogger.warn("Character/AddSkill", ex.toString());
        }
        finally{
            unlockWrite();
        }
    }
    public void addSkill(String name, int level, int exp)
    {
        lockWrite();
        try {
            skillScroll.add(Managers.newSkillInstance(name, level, exp));
            this.dirty = true;
        } catch (Exception ex) {
            UccuLogger.warn("Character/AddSkill", ex.toString());
            ex.printStackTrace();
        }
        finally{
            unlockWrite();
        }
    }
    public void addItem(String name, int quantity)
    {
        lockWrite();
        try {
            inventory.add(Managers.newItemInstance(name, quantity));
            this.dirty = true;
        } catch (Exception e) {
            UccuLogger.warn("Character/AddItem", e.toString());
            e.printStackTrace();
        }
        finally{
            unlockWrite();
        }
    }
    
}
