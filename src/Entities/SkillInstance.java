/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author xiaoshuang
 */
public class SkillInstance extends MutexValue<Integer>{//技能在游戏中的实例
    
    public static SkillInstance empty = new SkillInstance(-1, Skill.empty, 0, 0);
    
    int id;
    Skill skill;
    int level;
    int exp;//技能升级经验
    public SkillInstance()
    {
        this(-1, Skill.empty, 0, 0);
    }
    
    public SkillInstance(int id, Skill skill, int level, int exp)
    {
        super(id);
        this.id = id;
        this.skill = skill;
        this.level = level;
        this.exp = exp;
    }
    
    public SkillInstance(Skill skill)
    {
        this(-1, skill, 0, 0);
    }
    
    public int getSkillId() throws Exception
    {
        return skill.id;
    }
    
    public boolean hasTag(String name)
    {
        lockRead();
        try {
            return skill.hasTag(name);
        } finally {
            unlockRead();
        }
    }
    
    public void cast(Character player, KvPair a1, KvPair a2)
    {
        lockRead();
        skill.cast(this, player, a1, a2);
        unlockRead();
    }
    public String getName() throws Exception
    {
        return skill.name;
    }
    @Override
    public boolean equals(Object obj)
    {
        SkillInstance skillObj = (SkillInstance) obj;
        return obj == null ? false : this.skill == skillObj.skill;
    }
    

}
