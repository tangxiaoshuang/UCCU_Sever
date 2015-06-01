/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logics;

import Entities.KvPair;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import uccu_sever.UccuException;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class LogicManager {
    HashMap<String, Logic> name2logic;
    
    
    ReentrantReadWriteLock lock;

    public LogicManager() {
        name2logic = new HashMap<>();
        lock = new ReentrantReadWriteLock();
    }
    public void add(Logic obj) throws Exception
    {
        String name = obj.name;
        lockRead();
        if(name2logic.containsKey(name))
        {
            unlockRead();
            throw new UccuException("Logic "+name + " has been already  added!");
        }
        unlockRead();
        lockWrite();
        name2logic.put(name, obj);
        unlockWrite();
    }
    public Logic get(String name) throws Exception
    {
        lockRead();
        try
        {
            if(!name2logic.containsKey(name))
            {
                throw new UccuException("Logic "+name+ " don't exist!");
            }
            return name2logic.get(name);
        }
        finally{
            unlockRead();
        }
    }
    public void replace(String name, Logic logic) throws Exception
    {
        lockWrite();
        try
        {
            if(!this.has(name))
                throw new UccuException("Logic "+name+ " don't exist!");
            name2logic.replace(name, logic);
        }finally
        {
            unlockWrite();
        }
    }
    public boolean has(String name)
    {
        lockRead();
        try {
            return name2logic.containsKey(name);
        } finally {
            unlockRead();
        }
    }
    
    public void doAction(String name, KvPair a1, KvPair a2, KvPair a3, KvPair a4)
    {
        try {
            get(name).doAction(a1, a2, a3, a4);
        } catch (Exception ex) {
            UccuLogger.warn("LogicManager/DoAction", ex.getMessage());
        }
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
