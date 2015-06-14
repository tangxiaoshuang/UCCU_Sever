/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import uccu_sever.UccuException;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class Inventory extends MutexObject{
    int size;//容器容量
    ArrayList<ItemInstance> itemInstances;

    public Inventory(int size) {
        super();
        this.size = size;
        itemInstances = new ArrayList<>(size);
        ItemInstance it = ItemInstance.empty;
        for(int i = 0; i < size; ++i)
        {
            itemInstances.add(it);
        }
    }
    public Inventory(ByteBuffer bf)
    {
        super();
        this.size = bf.getInt();
        itemInstances = new ArrayList<>(size);
        for(int i = 0; i < this.size; ++i)
        {
            int data = bf.getInt();
            int quantity = data & 0x1111111;
            int id = data >> 7;
            try {
                itemInstances.add(Managers.newItemInstance(id, quantity));
            } catch (Exception ex) {
                UccuLogger.warn("Inventory/Constructor", "Item "+id+" "+ex.toString());
            }
        }
    }
    public void add(ItemInstance itemIns) throws Exception
    {
        lockWrite();
        try{
            int idx;
            int itemId = itemIns.getItemId();
            Item item = itemIns.item;
            
            ItemInstance empty = ItemInstance.empty;
            
            if(!item.canPile())//物品不能堆叠
            {
                while(itemIns.quantity>0)//一个一个放
                {
                    idx = itemInstances.indexOf(empty);//找空位
                    if(idx == -1)//容器满了
                        throw new UccuException("Inventory is Full!");
                    itemInstances.set(idx, Managers.newItemInstance(itemId, 1));
                    itemIns.quantity--;
                }
            }
            else//可以堆叠
            {
                int num = 100;
                idx = itemInstances.indexOf(itemIns);
                if(idx == -1)//包里没有这个物品
                {
                    idx = itemInstances.indexOf(empty);
                    if(idx == -1)
                        throw new UccuException("Inventory is Full!");
                    num = Math.min(100, itemIns.quantity);
                    itemInstances.set(idx, Managers.newItemInstance(itemId, num));
                    itemIns.quantity -= num;
                }
                else//包里有这个物品
                {
                    itemIns.quantity += itemInstances.get(idx).quantity;//全部取出来一起放
                    num = Math.min(100, itemIns.quantity);
                    itemInstances.set(idx, Managers.newItemInstance(itemId, num));
                    itemIns.quantity -= num;
                }
                while(itemIns.quantity>0)
                {
                    idx = itemInstances.indexOf(empty);
                    if(idx == -1)
                        throw new UccuException("Inventory is Full!");
                    num = Math.min(100, itemIns.quantity);
                    itemInstances.set(idx, Managers.newItemInstance(itemId, num));
                    itemIns.quantity -= num;
                }
            }
        }
        catch(Exception e)
        {
            UccuLogger.warn("Inventory/Add", e.toString());
        }
        finally{
            unlockWrite();
        }
    }
    public void pack(ByteBuffer bf)//BUG在此
    {
        lockRead();
        bf.putInt(size);
        for(int i = 0; i < size; ++i)
        {
            int data;
            try {
                data = itemInstances.get(i).getItemId();
            } catch (Exception ex) {
                UccuLogger.warn("Inventory/Pack", ex.toString());
                data = -1;
            }
            data <<= 7;
            data |= itemInstances.get(i).quantity;
            bf.putInt(data);
        }
        unlockRead();
    }
    public void packToClient(ByteBuffer bf)//BUG在此
    {
        lockRead();
        bf.putInt(size);
        for(int i = 0; i < size; ++i)
        {
            int data;
            bf.putInt(itemInstances.get(i).id);//InstanceID
            try {
                data = itemInstances.get(i).getItemId();//ItemID
            } catch (Exception ex) {
                UccuLogger.warn("Inventory/PackToClient", ex.toString());
                data = -1;
            }
            data <<= 7;
            data |= itemInstances.get(i).quantity;
            bf.putInt(data);
        }
        unlockRead();
    }
}
