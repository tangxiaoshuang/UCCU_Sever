/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import uccu_sever.UccuTimer;

/**
 *
 * @author xiaoshuang
 */
public class Character extends AttributionEntity{
    
    boolean dirty; //标记是否被修改过
    ColdDownManager cdManager; //冷却管理模块
    
    private Character(int id, String name, String description,
            int level, int gender, 
            int hp, int mp, int atk, int def, int exp, int movespeed,
            int posX, int posY)
    {
        super(id, name, description, level, gender, hp, mp, atk, def, exp, movespeed, posX, posY);
        dirty = false;
        cdManager = new ColdDownManager();
    }
    private Character(ByteBuffer bf)
    {
        super(bf);
        dirty = false;
        cdManager = new ColdDownManager();
    }
    @Override
    public void pack(ByteBuffer bf)
    {
        super.pack(bf);
    }
}
