/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.Objects;
import uccu_sever.Datagram;

/**
 *
 * @author xiaoshuang
 */
public class KvPair extends MutexObject{//所有逻辑元素基础：键值对，且对键值对的修改可以采用读写互斥
    public int id;
    public String name;
    public KvPair(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    public KvPair(ByteBuffer bf)//从打包数据中还原
    {
        super();
        this.id = bf.getInt();
        this.name = Datagram.extractString(bf);
    }
    public void pack(ByteBuffer bf)
    {
        lock.readLock().lock();
        try {
            bf.putInt(id);
            Datagram.restoreString(bf, name);
        } finally {
            lock.readLock().unlock();
        }
    }
    @Override
    public boolean equals(Object obj)
    {
        KvPair kvpair = (KvPair)obj;
        if(this.name == null)
            return this.id == kvpair.id;
        return this.id == kvpair.id && this.name.equals(kvpair.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
    }
}
