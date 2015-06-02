/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author xiaoshuang
 */
public class ItemInstance extends MutexValue<Integer>{

    //物品在游戏中的实例
    public static ItemInstance empty = new ItemInstance(-1, Item.empty, 0);
    
    
    int id;
    Item item;
    int quantity;
    int equipType;
    public ItemInstance() {//构造空实例
        this(-1, Item.empty,0);
    }
    public ItemInstance(int id, Item item, int quantity) {
        super(id);
        this.id = id;
        this.item = item;
        this.quantity = quantity;
    }
    public int getItemId() throws Exception
    {
        return item.id;
    }
    public String getName() throws Exception
    {
        return item.name;
    }
    @Override
    public boolean equals(Object obj)
    {
        ItemInstance itemObj = (ItemInstance)obj;
//        if(this.id == -1 || itemObj.id == -1)
//            return itemObj.id == -1 && this.id == -1;
//if(obj == null)return false;
        return obj == null ? false : this.item == itemObj.item;
    }
//    public static ItemInstance newItemInstance(int id, int quantity)
//    {
//        try {
//            Item item = Managers.itemManager.get(id);
//            return new ItemInstance(item.id, item.name, item, quantity);
//        } catch (Exception e) {
//            UccuLogger.warn("ItemInstance/NewItemInstance", e.getMessage());
//            return null;
//        }
//    }
//    public static ItemInstance newItemInstance(String name, int quantity)
//    {
//        try {
//            Item item = Managers.itemManager.get(name);
//            return new ItemInstance(item.id, item.name, item, quantity);
//        } catch (Exception e) {
//            UccuLogger.warn("ItemInstance/NewItemInstance", e.getMessage());
//            return null;
//        }
//    }
}
