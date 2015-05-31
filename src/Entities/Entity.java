/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import uccu_sever.Datagram;

/**
 *
 * @author xiaoshuang
 */
public class Entity extends KvPair{//实体会展示在游戏中，因此具有描述
    
    String description;
    public Entity(int id, String name, String description)
    {
        super(id, name);
        this.description = description;
    }
    public Entity(ByteBuffer bf)
    {
        super(bf);
        this.description = Datagram.extractString(bf);
    }
    @Override
    public void pack(ByteBuffer bf)
    {
        lockRead();
        super.pack(bf);
        Datagram.restoreString(bf, description);
        unlockRead();
    }
}
