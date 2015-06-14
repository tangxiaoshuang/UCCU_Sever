/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Logics.ItemLogic;
import java.util.Scanner;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class Item extends Entity{
    
    public static Item empty = new Item(0, "空", "空", 0, false);
    
    public static int segment = 0;
    
    public static String[] properties = {"[ID]",
                                         "[NAME]",
                                         "[DESCRIPTION]",
                                         "[COLDDOWN]",
                                         "[CANPILE]",
                                         "[TAGS]",
                                         "[DATABANK]",
                                         "[LOGICS]"};
                                            
    
    int coldDown;
    boolean canPile; //可以堆叠？
    TagManager tags; //物品属性标签
    DataBank dataBank;//物品具体属性
    public ItemLogic itemLogic;//物品逻辑模块
    
    public Item(int id, String name, String description,
            int coldDown, boolean canPile) {
        super(id, name, description);
        this.coldDown = coldDown;
        this.canPile = canPile;
        this.tags = new TagManager();
        this.dataBank = new DataBank();
        itemLogic = new ItemLogic();
    }
    public Item(Scanner scr)
    {
        super(-1, null, null);
        for(int i = 0; i < properties.length; i++)
        {
            if(!scr.next().equalsIgnoreCase(properties[i]))
            {
                UccuLogger.warn("Item/Constructor", properties[i]+ " is missing!");
                return;
            }
            switch(i)
            {
                case 0:
                    this.id = scr.nextInt();
                    break;
                case 1:
                    this.name = scr.next();
                    break;
                case 2:
                    this.description = scr.nextLine().trim();
                    break;
                case 3:
                    this.coldDown = scr.nextInt();
                    break;
                case 4:
                    this.canPile = scr.nextInt()>0;
                    break;
                case 5:
                    this.tags = new TagManager(scr);
                    break;
                case 6:
                    this.dataBank = new DataBank(scr);
                    break;
                case 7:
                {
                    String name = scr.next();
                    if(name.equalsIgnoreCase("[/LOGICS]"))
                    {
                        this.itemLogic = new ItemLogic();
                        break;
                    }
                    try {
                        this.itemLogic = (ItemLogic)Managers.logicManager.get(name);
                    } catch (Exception e) {
                        UccuLogger.warn("Item/Constructor", "ItemLogic "+name + " DON'T exist!");
                    }
                }
            }
        }
    }
    public boolean canPile()
    {
        lockRead();
        try {
            return this.canPile;
        } finally {
            unlockRead();
        }
    }
    public void trigger(ItemInstance itemInstance, Character player, KvPair a1, KvPair a2)
    {
        lockRead();
        itemLogic.lockRead();
        itemLogic.trigger(itemInstance, player, a1, a2);
        itemLogic.unlockRead();
        unlockRead();
    }
    public boolean hasTag(String name)
    {
        lockRead();
        tags.lockRead();
        
        try {
            return tags.has(name);
        } finally {
            tags.unlockRead();
            unlockRead();
        }
    }
}
