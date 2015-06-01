/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logics;

import Entities.KvPair;
import Entities.MutexValue;

/**
 *
 * @author xiaoshuang
 */
public class Logic extends MutexValue<String>{
    //一些基本动作的封装
    //示例：
    public void setName(String name)
    {
        this.name = name;
    }
    public Logic() {
        super(null);
    }
    
    public void doAction(KvPair a1, KvPair a2, KvPair a3, KvPair a4)//涉及的参数
    {
        lockRead();
        //执行一些行为
        unlockRead();
    }
    public void doAction()
    {
        
    }
}
