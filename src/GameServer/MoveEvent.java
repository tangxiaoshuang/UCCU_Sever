/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.Managers;
import java.util.TimerTask;
import uccu_sever.Point;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class MoveEvent extends TimerTask{
    Point tp;
    int id;
    
    int step = 50;
    
    public MoveEvent(Point tp, int id)
    {
        this.tp = new Point(tp);
        this.id = id;
    }
    @Override
    public void run() {
        //UccuLogger.note("TimeEvent/PlayerMove", "New step!");
        Entities.Character cha = null;
        try {
            cha = Managers.getCharacter(id);
        } catch (Exception e) {
            UccuLogger.warn("TimeEvent/PlayerMove", e.toString());
            return;
        }
        cha.lockWrite();
        if(!cha.target.equals(tp))//目标发生改变，取消该事件
        {
            UccuLogger.note("MoveEvent/Run", "Player "+id+" change target to "+cha.target+" now!");
            cha.unlockWrite();
            return;
        }
            
        
        int speed = cha.moveSpeed;
        
        Point src = cha.getPos();
        
        int d = (int) (speed * step / 1000);//这一步移动的距离
        
        Point end = src.movePointTrunc(tp, d);
        cha.setPos(end);
        cha.unlockWrite();
        if(!end.equals(tp))//未移动到目标点
            Daemons.addTimerTask(new MoveEvent(tp, id), step);
        //UccuLogger.note("MoveEvent/Run", "Player "+id+" is at "+end.toString()+" now!");
    }
    
}
