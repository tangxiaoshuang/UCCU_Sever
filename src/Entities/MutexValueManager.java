/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.HashMap;
import uccu_sever.UccuException;

/**
 *
 * @author xiaoshuang
 * @param <T>
 * @param <T2>
 */
public class MutexValueManager<T, T2> extends MutexObject{
    HashMap<T, MutexValue<T> > name2obj;
    
    public MutexValueManager()
    {
        super();
        name2obj = new HashMap<>();
    }
    public void add(MutexValue<T> obj) throws Exception
    {
        T name = obj.name;
        lockWrite();
        try {
            if(this.has(name))
                throw new UccuException("" + obj.getClass()+name + " has been already  added!");
            name2obj.put(name, obj);
        } finally {
            unlockWrite();
        }
    }
    public T2 get(T name) throws Exception
    {
        lockRead();
        try {
            if(!this.has(name))
                throw new UccuException("" + name.getClass()+name + " don't exist!");
            return (T2)name2obj.get(name);
        } finally {
            unlockRead();
        }
    }
    public boolean has(T name)
    {
        lockRead();
        try
        {
            return name2obj.containsKey(name);
        }finally
        {
            unlockRead();
        }
    }
    public T2 replace(T name, MutexValue<T> obj) throws Exception
    {
        lockWrite();
        try {
            if(!this.has(name))
                throw new UccuException("" + obj.getClass()+name + " don't exist!");
            return (T2)name2obj.replace(name, obj);
        } finally {
            unlockWrite();
        }
    }
    public void remove(MutexValue<T> obj)
    {
        lockWrite();
        try {
            if(!this.has(obj.name))
                return;
            name2obj.remove(obj.name);
        } finally {
            unlockWrite();
        }
    }
}
