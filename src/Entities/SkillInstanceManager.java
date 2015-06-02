/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author xiaoshuang
 */
public class SkillInstanceManager extends MutexValueManager<Integer, SkillInstance>{
    
    PriorityBlockingQueue<Integer> idleID;//存储空闲ID
    int maxID;//记录分配ID的上限
    
    public SkillInstanceManager()
    {
        maxID = 0;
        idleID = new PriorityBlockingQueue<>();
    }
    public SkillInstance newSkillInstance(int skillId, int level, int exp) throws Exception
    {
        lockWrite();
        try {
            Integer id = idleID.poll();
            if(id == null)
                id = maxID;
            Skill skill = Managers.getSkill(skillId);
            SkillInstance skillIns = new SkillInstance(id, skill, level, exp);
            this.add(skillIns);
            return skillIns;
        } finally {
            unlockWrite();
        }
    }
}
