/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JythonScripts;

import Entities.Managers;
import Logics.Logic;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class JythonLogic extends JythonModule<Logic>{

    public JythonLogic(String filename, String path) {
        super(filename, path, Logic.class);
        obj.setName(filename);
        try {
            Managers.logicManager.add(obj);
        } catch (Exception e) {
            UccuLogger.warn("JythonLogic/Constructor", e.getMessage());
        }
    }
    
    @Override
    public void reload()//DeadLock
    {
        try {
            Managers.logicManager.lockWrite();
            obj.lockWrite();
            super.reload();
            //Managers.logicManager.replace(name, obj);
        } catch (Exception ex) {
            UccuLogger.warn("JythonLogic/Reload", ex.getMessage());
        } finally{
            obj.unlockWrite();
            Managers.logicManager.unlockWrite();
        }
    }
    
}
