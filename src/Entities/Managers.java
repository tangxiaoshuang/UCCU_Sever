/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import JythonScripts.JythonScriptManager;
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
}
