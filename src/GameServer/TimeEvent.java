/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.Character;
import Entities.Managers;
import uccu_sever.Point;
import uccu_sever.UccuLogger;
/**
 *
 * @author xiaoshuang
 */
public class TimeEvent {
    long step;//时间增加的步长，每过step的时间执行一次事件
    long dueTime;//截至时间，守护进程什么时间删除这个事件
    long nextTime;//下次执行的时间，一定小于等于DueTime

    public TimeEvent(long step) {
        this.step = step;
    }
    public TimeEvent(int tx, int ty, int id, long step) {
        this.tx = tx;
        this.ty = ty;
        this.id = id;
        this.step = step;
    }
    
    
    public void exec()
    {
        //每次要执行的内容
    }
    //暂时只有移动
    int tx;
    int ty;
    int id;
    public void playerMove()
    {
        UccuLogger.note("TimeEvent/PlayerMove", "New step!");
        Character cha = null;
        try {
            cha = Managers.getCharacter(id);
        } catch (Exception e) {
            UccuLogger.warn("TimeEvent/PlayerMove", e.toString());
            return;
        }
        cha.lockWrite();
        
        int speed = cha.moveSpeed;
        Point tar = new Point(tx,ty);
        Point src = cha.getPos();
        
        int d = (int) (speed * step / 1000);//这一步移动的距离
        
        Point end = src.movePointTrunc(tar, d);
        if(end.equals(tar))//已经移动到目标点
            step = -1;//取消事件
        cha.setPos(end);
        
        cha.unlockWrite();
    }
}
