/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class ItemInstance extends KvPair{//物品在游戏中的实例
    Item item;
    int quantity;

    public ItemInstance() {
        super(-1, "");
    }
    
    
    public ItemInstance(int id, String name, Item item, int quantity) {
        super(id, name);
        this.item = item;
        this.quantity = quantity;
    }
    public static ItemInstance newItemInstance(int id, int quantity)
    {
        try {
            Item item = Managers.itemManager.get(id);
            return new ItemInstance(item.id, item.name, item, quantity);
        } catch (Exception e) {
            UccuLogger.warn("ItemInstance/NewItemInstance", e.getMessage());
            return null;
        }
    }
    public static ItemInstance newItemInstance(String name, int quantity)
    {
        try {
            Item item = Managers.itemManager.get(name);
            return new ItemInstance(item.id, item.name, item, quantity);
        } catch (Exception e) {
            UccuLogger.warn("ItemInstance/NewItemInstance", e.getMessage());
            return null;
        }
    }
}
