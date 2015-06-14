/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;

/**
 *
 * @author xiaoshuang
 */
public class AttributionEntity extends Entity{
    
    public int level;//可以压缩到Byte
    public int gender;//可以压缩到Byte
    
    public int life;
    public int curLife;
    public int mana;
    public int curMana;
    public int atk;
    public int armor;
    public int exp;
    public int moveSpeed;
    
    public int posX;
    public int posY;
    public int feature;
    
    public AttributionEntity(int id, String name, String description,
            int level, int gender, 
            int life, int curLife, int mana, int curMana, int atk, int def, int exp, int movespeed,
            int posX, int posY, int feature) {
        super(id, name, description);
        this.level = level;
        this.gender = gender;
        this.life = life;
        this.curLife = curLife;
        this.mana = mana;
        this.curMana = curMana;
        this.atk = atk;
        this.armor = def;
        this.exp = exp;
        this.moveSpeed = movespeed;
        this.posX = posX;
        this.posY = posY;
        this.feature = feature;
    }

    public AttributionEntity(ByteBuffer bf) {
        super(bf);
        this.level = bf.get();
        this.gender = bf.get();
        this.life = bf.getInt();
        this.curLife = bf.getInt();
        this.mana = bf.getInt();
        this.curMana = bf.getInt();
        this.atk = bf.getInt();
        this.armor = bf.getInt();
        this.exp = bf.getInt();
        this.moveSpeed = bf.getInt();
        this.posX = bf.getInt();
        this.posY = bf.getInt();
        this.feature = bf.getInt();
    }
    @Override
    public void pack(ByteBuffer bf)
    {
        lockRead();
        super.pack(bf);
        bf.put((byte)level);
        bf.put((byte)gender);
        bf.putInt(life);
        bf.putInt(curLife);
        bf.putInt(mana);
        bf.putInt(curMana);
        bf.putInt(atk);
        bf.putInt(armor);
        bf.putInt(exp);
        bf.putInt(moveSpeed);
        bf.putInt(posX);
        bf.putInt(posY);
        bf.putInt(feature);
        unlockRead();
    }
    
}
