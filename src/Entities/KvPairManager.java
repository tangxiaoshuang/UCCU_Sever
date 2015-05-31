/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import uccu_sever.UccuException;

/**
 *
 * @author xiaoshuang
 * @param <T>
 */
public class KvPairManager<T> {
    ReentrantReadWriteLock lock;
    HashMap<Integer, KvPair> id2kvpair;
    HashMap<String, KvPair> name2kvpair;
    
    public KvPairManager() {
        lock = new ReentrantReadWriteLock();
        id2kvpair = new HashMap<>();
        name2kvpair = new HashMap<>();
    }
    
    public void add(KvPair obj) throws Exception
    {
        int id = obj.id;
        String name = obj.name;
        lock.readLock().lock();
        
        if(id2kvpair.containsKey(id))
        {
            lock.readLock().unlock();
            throw new UccuException("KvPair "+id+" "+name+" has been already  added!");
        }
        
        lock.readLock().unlock();
        
        lock.writeLock().lock();
        
        id2kvpair.put(id, obj);
        name2kvpair.put(name, obj);
        
        lock.writeLock().unlock();
    }
    public void remove(KvPair obj) throws Exception
    {
        int id = obj.id;
        String name = obj.name;
        
        lock.readLock().lock();
        
        if(!id2kvpair.containsKey(id))
        {
            lock.readLock().unlock();
            throw new UccuException("KvPair "+id+" "+name+" DON'T exist!");
        }
        
        lock.readLock().unlock();
        
        lock.writeLock().lock();
        
        id2kvpair.remove(id);
        name2kvpair.remove(name);
        
        lock.writeLock().unlock();
        
    }
    public T get(int id) throws Exception
    {
        lock.readLock().lock();
        KvPair obj = null;
        if(!id2kvpair.containsKey(id))
        {
            lock.readLock().unlock();
            throw new UccuException("KvPair "+id+ " don't exist!");
        }
        
        obj = id2kvpair.get(id);
        lock.readLock().unlock();
        return (T)obj;
    }
    public T get(String name) throws Exception
    {
        lock.readLock().lock();
        KvPair obj = null;
        if(!name2kvpair.containsKey(name))
        {
            lock.readLock().unlock();
            throw new UccuException("KvPair "+name+ " don't exist!");
        }
        
        obj = name2kvpair.get(name);
        lock.readLock().unlock();
        return (T)obj;
    }
    public boolean has(String name)
    {
        lock.readLock().lock();
        if(name2kvpair.containsKey(name))
        {
            lock.readLock().unlock();
            return true;
        }
        else
        {
            lock.readLock().unlock();
            return false;
        }
    }
    public boolean has(int id)
    {
        lock.readLock().lock();
        if(id2kvpair.containsKey(id))
        {
            lock.readLock().unlock();
            return true;
        }
        else
        {
            lock.readLock().unlock();
            return false;
        }
    }
    //可能还有一些加载函数
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
