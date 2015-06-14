/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import Entities.Managers;
import java.util.Scanner;
import org.python.util.PythonInterpreter;
import org.python.core.*;

/**
 *
 * @author xiaoshuang
 */

class Dick extends PyObject
{
    private String s = "hello";
    synchronized public String hello()
    {
        return s;
    }
    
}



public class Test {
    
    public void t1(Integer a)
    {
        a = a + 5;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        PythonInterpreter interp = new PythonInterpreter();
        interp.execfile("data\\import.py");
        interp.execfile("A.py");
        Scanner scr = new Scanner(System.in);
        
        //Float fl = 2.33f;
        //Object tmp = fl;
        
        //System.out.println(tmp.getClass().cast(tmp));
        Test t = new Test();
        
        Integer a = 1;
        t.t1(a);
        System.out.println(a);
        
        Managers.jythonScriptManager.loadLogics();
        Managers.itemManager.load();
        Managers.skillManager.load();
        
        try {
            Managers.itemManager.get(1).itemLogic.doAction();
        } catch (Exception ex) {
            UccuLogger.warn("Test", ex.toString());
        }
        
        scr.hasNextLine();
        scr.nextLine();
        
        try {
            Managers.jythonScriptManager.get("test").reload();
        } catch (Exception ex) {
            UccuLogger.warn("Test", ex.toString());
        }
        
        try {
            Managers.itemManager.get(1).itemLogic.doAction();
        } catch (Exception ex) {
            UccuLogger.warn("Test", ex.toString());
        }
        
        
        
        
        while(scr.hasNextLine())
        {
            try {
                interp.exec(scr.nextLine());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
