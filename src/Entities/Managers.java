/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import GameServer.Gate;
import GameServer.GateManager;
import JythonScripts.JythonScriptManager;
import Logics.Logic;
import Logics.LogicManager;
import java.nio.ByteBuffer;
import java.util.Collection;
import uccu_sever.AioSession;
import uccu_sever.UccuLogger;

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
    static public GateManager gateManager = new GateManager();
    
    
    public static void load()
    {
        UccuLogger.log("Managers/Load", "Start initalize data...");
        UccuLogger.log("Managers/Load", "Load jython logics...");
        jythonScriptManager.loadLogics();
        UccuLogger.log("Managers/Load", "Jython logics.....100%");
        UccuLogger.log("Managers/Load", "Load item data...");
        itemManager.load();
        UccuLogger.log("Managers/Load", "Item data.....100%");
        UccuLogger.log("Managers/Load", "Load skill data...");
        skillManager.load();
        UccuLogger.log("Managers/Load", "Skill data.....100%");
        UccuLogger.kernel("Managers/Load", "Data initialization done!");
    }
    
    public static Gate newGate(AioSession session)
    {
        return gateManager.newGate(session);
    }
    public static Gate getGate(AioSession session)
    {
        return gateManager.get(session);
    }
    public static Gate getGate(int id)
    {
        return gateManager.get(id);
    }
    public static void removeGate(AioSession session)
    {
        gateManager.remove(session);
    }
    
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
    public static ItemInstance newItemInstance(String name, int quantity) throws Exception
    {
        return itemInstanceManager.newItemInstance(name, quantity);
    }
    
    public static ItemInstance getItemInstance(int id) throws Exception
    {
        return itemInstanceManager.get(id);
    }
    public static void removeItemInstance(ItemInstance itemIns)
    {
        itemInstanceManager.remove(itemIns);
    }
    
    public static SkillInstance newSkillInstance(int skillId, int level, int exp) throws Exception
    {
        return skillInstanceManager.newSkillInstance(skillId, level, exp);
    }
    public static SkillInstance newSkillInstance(String name, int level, int exp) throws Exception
    {
        return skillInstanceManager.newSkillInstance(name, level, exp);
    }
    
    public static SkillInstance getSkillInstance(int id) throws Exception
    {
        return skillInstanceManager.get(id);
    }
    
    public static Character getCharacter(int id) throws Exception
    {
        return characterManager.get(id);
    }
    public static Character getCharacter(String name) throws Exception
    {
        return characterManager.get(name);
    }
    public static boolean hasCharacter(int id)
    {
        return characterManager.has(id);
    }
    public static boolean hasCharacter(String name)
    {
        return characterManager.has(name);
    }
    public static Character newCharacter(ByteBuffer bf)
    {
        return characterManager.newCharacter(bf);
    }
    public static Collection<KvPair> characters()
    {
        return characterManager.characters();
    }
}
