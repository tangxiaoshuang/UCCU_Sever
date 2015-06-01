/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.Scanner;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class TagManager extends MutexValueManager<String, Tag>{

    public TagManager() {
        super();
    }
    public TagManager(Scanner scr)
    {
        super();
        String name;
        while(!(name = scr.next()).equalsIgnoreCase("[/TAGS]"))
        {
            try {
                this.add(new Tag(name));
            } catch (Exception ex) {
                UccuLogger.warn("DataBank/Constructor", ex.getMessage());
            }
        }
    }
}
