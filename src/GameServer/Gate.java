/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.MutexObject;
import uccu_sever.AioSession;

/**
 *
 * @author xiaoshuang
 */
public class Gate extends MutexObject{
    public int id;
    public AioSession session;
    public long ping;
    public Gate(int id, AioSession session)
    {
        ping = -1;
        this.id = id;
        this.session = session;
    }
}
