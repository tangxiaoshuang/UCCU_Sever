/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JythonScripts;

import Entities.MutexValueManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class JythonScriptManager extends MutexValueManager<String, JythonScript>{
    String[] paths = {"data\\scripts\\commands\\",
                      "data\\scripts\\logics\\events\\",
                      "data\\scripts\\logics\\items\\",
                      "data\\scripts\\logics\\maps\\",
                      "data\\scripts\\logics\\skills\\"};
    
    String manifest = "manifest";
    String importFile = "data\\import.py";
    public JythonScriptManager() {
        super();
        JythonScript.interp.execfile(importFile);
    }
    public void loadLogics()
    {
        lockWrite();
        for (String path : paths) {
            try {
                FileInputStream is = new FileInputStream(path+manifest);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(isr);
                String name;//文件名
                while((name = in.readLine())!=null)
                {
                    this.add(new JythonLogic(name, path));
                }
                in.close();
                is.close();
            } catch (Exception e) {
                UccuLogger.warn("JythonScriptManager/Load", e.toString());
            }
        }
        unlockWrite();
    }
}
