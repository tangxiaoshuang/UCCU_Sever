/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class ColdDownManager extends KvPairManager<ColdDown>{

    public ColdDownManager() {
        super();
    }
    public ColdDownManager(ByteBuffer bf) {
        super();
        int size = bf.getInt();
        for(int i = 0; i < size; i++)
        {
            int id = bf.getInt();
            long startTime = bf.getInt();
            start(id);
            ColdDown cd;
            try {
                cd = this.get(id);
                cd.restart(startTime);
            } catch (Exception ex) {
                UccuLogger.warn("ColdDownManager/Constructor", ex.getMessage());
            }
        }
    }
    public void start(int id)//开始指定id冷却
    {
        ColdDown cd = null;
        lockWrite();
        try {
            cd = this.get(id);
            cd.restart(0);
        } catch (Exception ex) {
            try {
                cd = ColdDown.newColdDown(id);
                this.add(cd);
            } catch (Exception ex1) {
            }
        }
        finally{
            unlockWrite();
        }
    }
    public void start(String name)//开始指定id技能冷却
    {
        ColdDown cd = null;
        lockWrite();
        try {
            cd = this.get(name);
            cd.restart(0);
        } catch (Exception ex) {
            try {
                cd = ColdDown.newColdDown(name);
                this.add(cd);
            } catch (Exception ex1) {
            }
        }
        finally {
            unlockWrite();
        }
    }
    public boolean isCompleted(int id)
    {
        ColdDown cd = null;
        lockRead();
        try {
            cd = this.get(id);
            return cd.isCompleted();
        } catch (Exception e) {
            return true;
        }
        finally{
            unlockRead();
        }
    }
    public boolean isCompleted(String name)
    {
        ColdDown cd = null;
        lockRead();
        try {
            cd = this.get(name);
            return cd.isCompleted();
        } catch (Exception e) {
            return true;
        }
        finally{
            unlockRead();
        }
    }
    public void pack(ByteBuffer bf)
    {
        lockRead();
        Collection<KvPair> cs = id2kvpair.values();
        int size = 0;
        //ByteBuffer msg = ByteBuffer.allocate(1024);
        int sizepos = bf.position();
        bf.putInt(size);
        
        Iterator itr = cs.iterator();
        while(itr.hasNext())
        {
            ColdDown cd = (ColdDown)(itr.next());//冷却好的技能可以不添加
            if(!cd.isCompleted())
            {
                bf.putInt(cd.id);
                bf.putInt((int)cd.getMS());
                size ++;
            }
        }
        bf.putInt(sizepos, size);
        unlockRead();
    }
}
