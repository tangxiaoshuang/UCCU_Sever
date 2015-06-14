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
    
    public void trigger(Character player, KvPair a1, KvPair a2)
    {
        lockRead();
        item.trigger(this, player, a1, a2);
        unlockRead();
    }
    
    public boolean hasTag(String name)
    {
        lockRead();
        try {
            return item.hasTag(name);
        } finally {
            unlockRead();
        }
    }
    public String getName() throws Exception
    {
        return item.name;
    }
    @Override
    public boolean equals(Object obj)
    {
        ItemInstance itemObj = (ItemInstance)obj;
        return obj == null ? false : this.item == itemObj.item;
    }
    
}
