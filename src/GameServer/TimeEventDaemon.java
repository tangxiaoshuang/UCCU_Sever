/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import java.util.Comparator;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author xiaoshuang
 */
public class TimeEventDaemon extends TimerTask{
    int cycle = 10;
    PriorityBlockingQueue<TimeEvent> eventQueue;
    
    public TimeEventDaemon(int cycle)
    {
        this.cycle = cycle;
        Comparator<TimeEvent> order = new Comparator<TimeEvent>(){
            @Override
            public int compare(TimeEvent o1, TimeEvent o2) {
                long nxt1 = o1.nextTime;
                long nxt2 = o2.nextTime;
                if(nxt2 > nxt1)
                    return 1;
                else if(nxt2 < nxt1)
                    return -1;
                else
                    return 0;
            }
        };
        eventQueue = new PriorityBlockingQueue<>(11, order);
    }
    
    public void addSingleEvent(TimeEvent event)
    {
        event.nextTime = System.currentTimeMillis() + event.step;
        eventQueue.add(event);
    }
    
    @Override
    public void run()
    {
        TimeEvent event = eventQueue.poll();
        if(event == null)
            return;
        long current = System.currentTimeMillis();
        long nxt = event.nextTime;
        if(nxt > current)//不应该执行事件
            return;
        
        event.exec();
        
        if(event.step == -1)//时间不需再次执行
            return;
        
        eventQueue.add(event);
    }
}
