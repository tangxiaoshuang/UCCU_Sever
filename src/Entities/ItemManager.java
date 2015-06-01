/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class ItemManager extends KvPairManager<Item>{
    String[] paths = {"data\\data\\items\\"};
    String manifest = "manifest";
    
    public ItemManager()
    {
        super();
    }
    public void load()
    {
        lockWrite();
        try {
            for(String path : paths)
            {
                try {
                    FileInputStream is = new FileInputStream(path+manifest);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);
                    String name;
                    while((name = in.readLine())!=null)
                    {
                        File tmp = new File(path+name+".dat");
                        this.add(new Item(new Scanner(tmp, "UTF-8")));
                    }
                    in.close();
                    is.close();
                } catch (Exception e) {
                    UccuLogger.warn("ItemManager/Load", e.toString());
                }
            }
        } finally {
            unlockWrite();
        }
    }
}
