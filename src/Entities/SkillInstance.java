/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class SkillInstance extends KvPair{//技能在游戏中的实例
    Skill skill;
    int level;
    int exp;//技能升级经验
    public SkillInstance()
    {
        super(-1, "");
    }
    
    public SkillInstance(int id, String name, Skill skill, int level, int exp)
    {
        super(id, name);
        this.skill = skill;
        this.level = level;
        this.exp = exp;
    }
    public static SkillInstance newSkillInstance(int id, int level, int exp)
    {
        try {
            Skill skill = Managers.skillManager.get(id);
            return new SkillInstance(skill.id, skill.name, skill, level, exp);
        } catch (Exception e) {
            UccuLogger.warn("ItemInstance/NewItemInstance", e.getMessage());
            return null;
        }
    }
    public static SkillInstance newSkillInstance(String name, int level, int exp)
    {
        try {
            Skill skill = Managers.skillManager.get(name);
            return new SkillInstance(skill.id, skill.name, skill, level, exp);
        } catch (Exception e) {
            UccuLogger.warn("ItemInstance/NewItemInstance", e.getMessage());
            return null;
        }
    }
}
