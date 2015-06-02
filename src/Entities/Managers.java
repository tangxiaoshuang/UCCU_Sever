/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import JythonScripts.JythonScriptManager;
import Logics.Logic;
import Logics.LogicManager;

/**
 *
 * @author xiaoshuang
 */
public class Managers {
    static public CharacterManager characterManager = new CharacterManager();
    static public SkillManager skillManager = new SkillManager();
    static public ItemManager itemManager = new ItemManager();
    static public LogicManager logicManager = new LogicManager();
    static public JythonScriptManager jythonScriptManager = new JythonScriptManager();
    static public ItemInstanceManager itemInstanceManager = new ItemInstanceManager();
    static public SkillInstanceManager skillInstanceManager = new SkillInstanceManager();
    
    
    public static Item getItem(int id) throws Exception
    {
        return itemManager.get(id);
    }
    public static Item getItem(String name) throws Exception
    {
        return itemManager.get(name);
    }
    public static Skill getSkill(int id) throws Exception
    {
        return skillManager.get(id);
    }
    public static Skill getSkill(String name) throws Exception
    {
        return skillManager.get(name);
    }
    public static Logic getLogic(String name) throws Exception
    {
        return logicManager.get(name);
    }
    public static ItemInstance newItemInstance(int itemId, int quantity) throws Exception
    {
        return itemInstanceManager.newItemInstance(itemId, quantity);
    }
    public static SkillInstance newSkillInstance(int skillId, int level, int exp) throws Exception
    {
        return skillInstanceManager.newSkillInstance(skillId, level, exp);
    }
}
