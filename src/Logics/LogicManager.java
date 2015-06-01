/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logics;

import Entities.KvPair;
import Entities.MutexValueManager;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class LogicManager extends MutexValueManager<String, Logic>{

    public LogicManager() {
        super();
    }
    
    public void doAction(String name, KvPair a1, KvPair a2, KvPair a3, KvPair a4)
    {
        try {
            get(name).doAction(a1, a2, a3, a4);
        } catch (Exception ex) {
            UccuLogger.warn("LogicManager/DoAction", ex.getMessage());
        }
    }
}
