/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

import java.nio.ByteBuffer;

/**
 *
 * @author xiaoshuang
 */
public class Character {
    int id;
    String name;
    int level;
    byte gender;
    int posX;
    int posY;
    public Character()
    {
       
    }
    public Character(int _id, String _name, int _level, byte _gender, int _posX, int _posY)
    {
        id = _id;
        name = _name;
        level = _level;
        gender = _gender;
        posX = _posX;
        posY = _posY;
    }
    public ByteBuffer pack()
    {
        ByteBuffer msg = ByteBuffer.allocate(128);
        msg.putInt(id);
        msg.put(name.getBytes());
        msg.put((byte)level);
        msg.put(gender);
        msg.putInt(posX);
        msg.putInt(posY);
        msg.flip();
        return msg;
    }
}
