/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JythonScripts;

import Entities.MutexValue;
import java.io.File;
import org.python.util.PythonInterpreter;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class JythonScript extends MutexValue<String>{//负责加载管理jythonScript的类
    static PythonInterpreter interp = new PythonInterpreter();
    String path;
    public JythonScript(String name, String path) {
        super(name);
        this.path = path;
        String fullpath = path+name+".py";
        
        File file = new File(fullpath);
        if(!file.exists())
        {
            UccuLogger.warn("JythonScript/Constructor","Script "+name+ " don't exist!");
        }
        else
        {
            try {
                interp.execfile(fullpath);
                
            } catch (Exception e) {
                UccuLogger.warn("JythonScript/Constructor", e.toString());
            }
        }
    }
    public void reload()
    {
        lockWrite();
        String fullpath = path+name+".py";
        File file = new File(fullpath);
        if(!file.exists())
        {
            UccuLogger.warn("JythonScript/Reload","Script "+name+ " don't exist!");
        }
        else
        {
            try {
                interp.execfile(fullpath);
                
            } catch (Exception e) {
                UccuLogger.warn("JythonScript/Reload", e.toString());
            }
        }
        unlockWrite();
    }
}
