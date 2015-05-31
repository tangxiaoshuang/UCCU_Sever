/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import uccu_sever.Datagram;

/**
 *
 * @author xiaoshuang
 */
public class KvPair {//所有逻辑元素基础：键值对，且对键值对的修改可以采用读写互斥
    int id;
    String name;
    ReentrantReadWriteLock lock;
    public KvPair(int id, String name) {
        this.id = id;
        this.name = name;
        this.lock = new ReentrantReadWriteLock();
    }
    public KvPair(ByteBuffer bf)//从打包数据中还原
    {
        this.id = bf.getInt();
        this.name = Datagram.extractString(bf);
        this.lock = new ReentrantReadWriteLock();
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
    
    
    public void lockRead()
    {
        lock.readLock().lock();
    }
    public void unlockRead()
    {
        lock.readLock().unlock();
    }
    public void lockWrite()
    {
        lock.writeLock().lock();
    }
    public void unlockWrite()
    {
        lock.writeLock().unlock();
    }
}
