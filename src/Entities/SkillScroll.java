/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import uccu_sever.UccuException;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class SkillScroll extends MutexObject{
    int size;
    ArrayList<SkillInstance> skillInstances;
    
    public SkillScroll(int size)
    {
        super();
        this.size = size;
        skillInstances = new ArrayList<>(size);
    }
    public SkillScroll(ByteBuffer bf)
    {
        super();
        this.size = bf.getInt();
        this.skillInstances = new ArrayList<>(size);
        for(int i = 0; i < this.size; ++i)
        {
            int id = bf.getInt();
            int data = bf.getInt();
            int level = data & 0x1111111;
            int exp = data >> 7;
            try {
                skillInstances.add(Managers.newSkillInstance(id, level, exp));
            } catch (Exception ex) {
                UccuLogger.warn("SkillScroll/Constructor", ex.toString());
            }
        }
    }
    public void add(SkillInstance skillIns) throws Exception
    {
        lockWrite();
        try {
            if(has(skillIns))
                throw new UccuException("Has already learned!");
            skillInstances.add(skillIns);
        } finally {
            unlockWrite();
        }
    }
    public boolean has(SkillInstance skillIns)
    {
        lockRead();
        try {
            return skillInstances.contains(skillIns);
        } finally {
            unlockRead();
        }
    }
    public void pack(ByteBuffer bf)//BUG在此
    {
        lockRead();
        size = skillInstances.size();
        bf.putInt(size);
        for(int i = 0; i < size; ++i)
        {
            int data = -1;
            try {
                data = skillInstances.get(i).getSkillId();
            } catch (Exception ex) {
                UccuLogger.warn("SkillScroll/Pack", ex.toString());
            }
            bf.putInt(data);
            data = skillInstances.get(i).exp;
            data <<= 7;
            data |= skillInstances.get(i).level;
            bf.putInt(data);
        }
        unlockRead();
    }
}
