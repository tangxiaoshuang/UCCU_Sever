/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author xiaoshuang
 * @param <T>
 */
public class MutexValue<T> extends MutexObject{
    public T name;

    public MutexValue(T name) {
        super();
        this.name = name;
    }
    
}
