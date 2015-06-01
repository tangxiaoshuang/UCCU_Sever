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
    
    public int hp;
    public int mp;
    public int atk;
    public int def;
    public int exp;
    public int moveSpeed;
    
    public int posX;
    public int posY;
    
    public AttributionEntity(int id, String name, String description,
            int level, int gender, 
            int hp, int mp, int atk, int def, int exp, int movespeed,
            int posX, int posY) {
        super(id, name, description);
        this.level = level;
        this.gender = gender;
        this.hp = hp;
        this.mp = mp;
        this.atk = atk;
        this.def = def;
        this.exp = exp;
        this.moveSpeed = movespeed;
        this.posX = posX;
        this.posY = posY;
    }

    public AttributionEntity(ByteBuffer bf) {
        super(bf);
        this.level = bf.getInt();
        this.gender = bf.getInt();
        this.hp = bf.getInt();
        this.mp = bf.getInt();
        this.atk = bf.getInt();
        this.def = bf.getInt();
        this.exp = bf.getInt();
        this.moveSpeed = bf.getInt();
        this.posX = bf.getInt();
        this.posY = bf.getInt();
    }
    @Override
    public void pack(ByteBuffer bf)
    {
        lockRead();
        super.pack(bf);
        bf.put((byte)level);
        bf.put((byte)gender);
        bf.putInt(hp);
        bf.putInt(mp);
        bf.putInt(atk);
        bf.putInt(def);
        bf.putInt(exp);
        bf.putInt(moveSpeed);
        bf.putInt(posX);
        bf.putInt(posY);
        unlockRead();
    }
    
}
