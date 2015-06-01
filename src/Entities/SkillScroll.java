/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import uccu_sever.UccuException;

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
            skillInstances.add(SkillInstance.newSkillInstance(id, level, exp));
        }
    }
    public void add(SkillInstance skillIns) throws Exception
    {
        lockWrite();
        try {
            if(has(skillIns.id))
                throw new UccuException("Has already learned!");
            skillInstances.add(skillIns);
        } finally {
            unlockWrite();
        }
    }
    public boolean has(int id)
    {
        lockRead();
        try {
            SkillInstance skill = new SkillInstance(id, null, null, 0, 0);
            return skillInstances.contains(skill);
        } finally {
            unlockRead();
        }
    }
    public void pack(ByteBuffer bf)
    {
        lockRead();
        size = skillInstances.size();
        bf.putInt(size);
        for(int i = 0; i < size; ++i)
        {
            int data = skillInstances.get(i).id;
            bf.putInt(data);
            data = skillInstances.get(i).exp;
            data <<= 7;
            data |= skillInstances.get(i).level;
            bf.putInt(data);
        }
        unlockRead();
    }
}
