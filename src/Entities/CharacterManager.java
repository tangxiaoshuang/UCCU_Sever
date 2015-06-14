/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.nio.ByteBuffer;
import java.util.Collection;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class CharacterManager extends KvPairManager<Character>{
    public CharacterManager()
    {
        super();
    }
    public void newCharacter(Character cha)
    {
        lockWrite();
        try {
           this.add(cha);
        } catch (Exception e) {
            UccuLogger.warn("CharacterManager/NewCharacter", e.toString());
        }
        finally
        {
            unlockWrite();
        }
    }
    public Character newCharacter(ByteBuffer bf)
    {
        Character cha = new Character(bf);
        this.newCharacter(cha);
        return cha;
    }
    
    public Collection<KvPair> characters()
    {
        lockRead();
        try {
            return id2kvpair.values();
        } finally {
            unlockRead();
        }
    }
    
}
