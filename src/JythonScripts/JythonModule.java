/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JythonScripts;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 * @param <T>
 */
public class JythonModule<T> extends JythonScript{
    public T obj;
    String instanceName;
    Class<T> tClass;
    
    public JythonModule(String name, String path, Class<T> tClass) {
        super(name, path);
        instanceName = name + "_instance";
        this.tClass = tClass;
        try {
            interp.exec(instanceName+" = "+this.name+"()");
            obj = interp.get(instanceName, tClass);
        } catch (Exception e) {
            UccuLogger.warn("JythonModule/Constructor", e.toString());
        }
        lock = new ReentrantReadWriteLock();
    }
    
    @Override
    public void reload()
    {
        super.reload();
        lockWrite();
        try {
            interp.exec(instanceName+".__class__ = "+name);
            //obj = interp.get(instanceName, tClass);
        } catch (Exception e) {
            UccuLogger.warn("JythonModule/Constructor", e.toString());
        }
        unlockWrite();
    }
}
