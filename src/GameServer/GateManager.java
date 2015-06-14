/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.MutexObject;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;
import uccu_sever.AioSession;

/**
 *
 * @author xiaoshuang
 */
public class GateManager extends MutexObject{
    
    PriorityBlockingQueue<Integer> idleID;//存储空闲ID
    int maxID;
    
    public HashMap<Integer, Gate> id2gates;
    public HashMap<AioSession, Gate> session2gates;
    
    public GateManager()
    {
        maxID = 0;
        idleID = new PriorityBlockingQueue<>();
        id2gates = new HashMap<>();
        session2gates = new HashMap<>();
    }
    public Gate newGate(AioSession session)
    {
        lockWrite();
        try {
            Integer id = idleID.poll();
            if(id == null)
                id = maxID++;
            Gate gate = new Gate(id, session);
            this.add(gate);
            return gate;
        } finally {
            unlockWrite();
        }
    }
    public void add(Gate gate)//gate不会重新加入
    {
        lockWrite();
        try {
            id2gates.put(gate.id, gate);
            session2gates.put(gate.session, gate);
        } finally {
            unlockWrite();
        }
    }
    public void remove(int id)//gate不会重新加入
    {
        lockWrite();
        try {
            if(!this.has(id))
                return;
            Gate gate = this.get(id);
            id2gates.remove(id);
            session2gates.remove(gate.session);
            idleID.add(id);
        } finally {
            unlockWrite();
        }
    }
    
    public void remove(AioSession session)//gate不会重新加入
    {
        lockWrite();
        try {
            if(!this.has(session))
                return;
            Gate gate = this.get(session);
            id2gates.remove(gate.id);
            session2gates.remove(gate.session);
            idleID.add(gate.id);
        } finally {
            unlockWrite();
        }
    }
    
    public Gate get(int id)
    {
        return id2gates.get(id);
    }
    public Gate get(AioSession session)
    {
        return session2gates.get(session);
    }
    public boolean has(int id)
    {
        lockRead();
        try {
            return id2gates.containsKey(id);
        } finally {
            unlockRead();
        }
    }
    public boolean has(AioSession session)
    {
        lockRead();
        try {
            return session2gates.containsKey(session);
        } finally {
            unlockRead();
        }
    }
}
