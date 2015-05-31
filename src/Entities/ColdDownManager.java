/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.parser.DTDConstants;
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
        while(bf.hasRemaining())
        {
            int id = bf.getInt();
            long startTime = bf.getLong();
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
    }
    public void start(String name)//开始指定id技能冷却
    {
        ColdDown cd = null;
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
    }
    public boolean isCompleted(int id)
    {
        ColdDown cd = null;
        try {
            cd = this.get(id);
            return cd.isCompleted();
        } catch (Exception e) {
            return true;
        }
    }
    public boolean isCompleted(String name)
    {
        ColdDown cd = null;
        try {
            cd = this.get(name);
            return cd.isCompleted();
        } catch (Exception e) {
            return true;
        }
    }
    public void pack(ByteBuffer bf)
    {
        lockRead();
        Collection<KvPair> cs = id2kvpair.values();
        Iterator itr = cs.iterator();
        while(itr.hasNext())
        {
            ColdDown cd = (ColdDown)(itr.next());
            bf.putInt(cd.id);
            bf.putLong(cd.getMS());
        }
        unlockRead();
    }
}
