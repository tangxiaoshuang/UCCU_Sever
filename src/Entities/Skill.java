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
public class Skill extends Entity{
    //封装逻辑块
    int coldDown;//释放间隔
    public Skill(int id, String name, String description,
            int coldDown) {
        super(id, name, description);
        this.coldDown = coldDown;
    }
    
}
