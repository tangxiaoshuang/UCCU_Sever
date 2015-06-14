/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author xiaoshuang
 */
public class ItemInstanceManager extends MutexValueManager<Integer, ItemInstance>{
    
    PriorityBlockingQueue<Integer> idleID;//存储空闲ID
    int maxID;//记录分配ID的上限
    
    public ItemInstanceManager()
    {
        maxID = 0;
        idleID = new PriorityBlockingQueue<>();
    }
    public ItemInstance newItemInstance(int itemId, int quantity) throws Exception
    {
        lockWrite();
        try {
            Integer id = idleID.poll();
            if(id == null)
                id = maxID++;
            Item item = Managers.getItem(itemId);
            ItemInstance itemIns = new ItemInstance(id, item, quantity);
            this.add(itemIns);
            return itemIns;
        } finally {
            unlockWrite();
        }
    }
}
        