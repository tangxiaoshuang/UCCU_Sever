/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import uccu_sever.UccuException;

/**
 *
 * @author xiaoshuang
 */
public class Inventory extends KvPair{
    int size;//容器容量
    ArrayList<ItemInstance> itemInstances;

    public Inventory(int size) {
        super(-1, "");
        this.size = size;
        itemInstances = new ArrayList<>(size);
        ItemInstance it = new ItemInstance();
        for(int i = 0; i < size; ++i)
        {
            itemInstances.add(it);
        }
    }
    public Inventory(ByteBuffer bf)
    {
        super(-1, "");
        this.size = bf.getInt();
        itemInstances = new ArrayList<>(size);
        for(int i = 0; i < this.size; ++i)
        {
            int data = bf.getInt();
            int quantity = data & 0x1111111;
            int id = data >> 7;
            itemInstances.add(ItemInstance.newItemInstance(id, quantity));
        }
    }
    public void add(ItemInstance itemIns) throws Exception
    {
        lockWrite();
        try{
            int idx;
            int id = itemIns.id;
            String name = itemIns.name;
            Item item = itemIns.item;
            
            item.lockRead();
            boolean canPile = item.canPile;
            item.unlockRead();
            
            ItemInstance empty = new ItemInstance();
            
            if(!canPile)//物品不能堆叠
            {
                
                while(itemIns.quantity>0)//一个一个放
                {
                    idx = itemInstances.indexOf(empty);//找空位
                    if(idx == -1)//容器满了
                        throw new UccuException("Inventory is Full!");
                    itemInstances.set(idx, new ItemInstance(id, name, item, 1));
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
                    itemInstances.set(idx, new ItemInstance(id, name, item, num));
                    itemIns.quantity -= num;
                }
                else//包里有这个物品
                {
                    itemIns.quantity += itemInstances.get(idx).quantity;//全部取出来一起放
                    num = Math.min(100, itemIns.quantity);
                    itemInstances.set(idx, new ItemInstance(id, name, item, num));
                    itemIns.quantity -= num;
                }
                while(itemIns.quantity>0)
                {
                    idx = itemInstances.indexOf(empty);
                    if(idx == -1)
                        throw new UccuException("Inventory is Full!");
                    num = Math.min(100, itemIns.quantity);
                    itemInstances.set(idx, new ItemInstance(id, name, item, num));
                    itemIns.quantity -= num;
                }
            }
        }
        finally{
            unlockWrite();
        }
    }
    @Override
    public void pack(ByteBuffer bf)
    {
        lockRead();
        bf.putInt(size);
        for(int i = 0; i < size; ++i)
        {
            int data = itemInstances.get(i).id;
            data <<= 7;
            data |= itemInstances.get(i).quantity;
            bf.putInt(data);
        }
        unlockRead();
    }
}
