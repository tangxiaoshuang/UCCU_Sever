/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Scanner;
import org.python.util.PythonInterpreter;

/**
 *
 * @author xiaoshuang
 */
public class Shell
{
    private String dir = "shell/";
    
    private Scanner scr;
    private PythonInterpreter interp;//python解释器
    private String[] classes;//环境变量
    private Path workPath;
    private String pack;
    
    private Object core;
    
    private ByteArrayOutputStream pyResult;
    
    public Shell()
    {
        scr = new Scanner(System.in);
        interp = new PythonInterpreter();
        classes = new String[4];
        classes[0] = "sys";
        classes[1] = "GameServer";
        classes[2] = "Character";
        classes[3] = "BasicLib";
        workPath = Paths.get(dir);
        pack = getClass().getPackage().getName();
        pyResult = new ByteArrayOutputStream();
        interp.setOut(pyResult);
        //内置环境变量
    }
    public void setCore(Object c) 
    {
        core = c;
    }
    public void startShell()
    {
        UccuLogger.log("Shell/Start", "Initialize shell mode ...");
        info("Load Environment Variables...");
        info("Package: "+pack);
        info("Directory: "+workPath.toAbsolutePath().toString());
        loadClasses();
        if(core != null)
        {
            interp.set("core", core);
            info("Core linked! "+core.getClass().getName());
        }
        UccuLogger.log("Shell/Start", "Done! Shell mode is on!");
        shell();
    }
    private void shell()
    {
        userPrint();
        while(scr.hasNextLine())
        {
            String str = scr.nextLine();
            try {
                interp.exec(str);
                String res = pyResult.toString().trim();
                if(!res.equals(""))
                    info(res);
            } catch (Exception e) {
                warn("Error! "+e);
            }
            userPrint();
        }
    }
    private void loadClasses()
    {
        interp.exec("import sys");
        for(int i = 1;i < classes.length;++i)
        {
            loadClass(classes[i]);
        }
    }
    private void loadClass(String name)
    {
        try {
            interp.exec("from "+pack + " import "+ name);
            info("class "+name+" done.");
        } catch (Exception e) {
            info("class "+name+" failed! "+e);
        }
    }
    public void info(String str)
    {
        if(!str.endsWith("\n"))
            str += "\n";
        synchronized(System.out)
        {
            System.out.print("\r" + decorate("INFO", str));
            System.out.print("> ");
        }
    }
    public void warn(String str)
    {
        if(!str.endsWith("\n"))
            str += "\n";
        synchronized(System.out)
        {
            System.out.print("\r" + decorate("WARN", str));
            System.out.print("> ");
        }
    }
    public void userPrint()
    {
        synchronized(System.out)
        {
            System.out.print("\r> ");
        }
    }
    public String decorate(String type, String str)
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        return String.format("[%02d:%02d:%02d %s]: %s", hour, min, sec, type, str);
    }
    public void pause()
    {
        scr.hasNextLine();
        scr.nextLine();
    }
}