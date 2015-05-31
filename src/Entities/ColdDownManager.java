/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xiaoshuang
 */
public class ColdDownManager extends KvPairManager<ColdDown>{

    public ColdDownManager() {
        super();
    }
    public void start(int id)//开始指定id技能冷却
    {
        ColdDown cd = null;
        try {
            cd = this.get(id);
            cd.restart();
        } catch (Exception ex) {
            try {
                cd = new ColdDown(id);
                this.add(cd);
            } catch (Exception ex1) {
            }
        }
    }
    public void start(String name)//开始指定id技能冷却
    {
        ColdDown cd = null;
        try {
            cd = this.get(name);
            cd.restart();
        } catch (Exception ex) {
            try {
                cd = new ColdDown(name);
                this.add(cd);
            } catch (Exception ex1) {
            }
        }
    }
    public boolean isCompleted(int id)
    {
        ColdDown cd = null;
        try {
            cd = this.get(id);
            return cd.isCompleted();
        } catch (Exception e) {
            return true;
        }
    }
    public boolean isCompleted(String name)
    {
        ColdDown cd = null;
        try {
            cd = this.get(name);
            return cd.isCompleted();
        } catch (Exception e) {
            return true;
        }
    }
    
}
