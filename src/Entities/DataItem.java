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
public class DataItem extends MutexValue<String>{
    Object data;
    public DataItem(String name, Object data) {
        super(name);
        this.data = data;
    }
}
