/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author xiaoshuang
 */
public class MutexObject {
    protected ReentrantReadWriteLock lock;

    public MutexObject() {
        lock = new ReentrantReadWriteLock();
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
