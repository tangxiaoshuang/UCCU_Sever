/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class TimeEventDaemon extends TimerTask{
    int cycle = 10;
    PriorityBlockingQueue<TimeEvent> eventQueue;
    Timer timer;
    
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
        timer = new Timer(true);
    }
    
    public void start(int delay)
    {
        timer.schedule(this, delay, cycle);
    }
    
    public void addSingleEvent(TimeEvent event)
    {
        UccuLogger.debug("TimeEventDaemon/AddSingleEvent", "Add new time event!");
        event.nextTime = System.currentTimeMillis() + event.step;
        eventQueue.add(event);
    }
    
    @Override
    public void run()
    {
        
        TimeEvent event = eventQueue.poll();
        if(event == null)
        {
            //UccuLogger.note("TimeEventDaemon/Run", " Idle.........................");
            return;
        }
            
        
        long current = System.currentTimeMillis();
        long nxt = event.nextTime;
        
        UccuLogger.note("TimeEventDaemon/Run", "Event get! current "+current + " next : "+nxt);
        if(nxt > current)//不应该执行事件
            return;
        UccuLogger.note("TimeEventDaemon/Run", "Execute!");
        event.exec();
        
        if(event.step == -1)//时间不需再次执行
            return;
        
        eventQueue.add(event);
    }
}
