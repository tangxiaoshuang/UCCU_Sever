/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.ArrayList;

/**
 *
 * @author xiaoshuang
 */
public class Item extends Entity{
    public static int segment = 0;
    
    int coldDown;
    boolean canPile; //可以堆叠？
    ArrayList<String> tags; //物品属性标签
    DataBank dataBank;//物品具体属性
    
    public Item(int id, String name, String description,
            int coldDown, boolean canPile) {
        super(id, name, description);
        this.coldDown = coldDown;
        this.canPile = canPile;
    }
    
    
}
