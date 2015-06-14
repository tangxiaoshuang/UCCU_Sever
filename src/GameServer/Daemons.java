/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author xiaoshuang
 */
public class Daemons {
    public static TimeEventDaemon timeEventDaemon = new TimeEventDaemon(10);
    public static RestoreDaemon restoreDaemon = new RestoreDaemon();
    public static Timer eventTimer = new Timer(true);
    public static void addSingleEvent(TimeEvent event)
    {
        timeEventDaemon.addSingleEvent(event);
    }
    public static void addTimerTask(TimerTask task, int delay)
    {
        eventTimer.schedule(task, delay);
    }
    public static void start(int delay, int period)
    {
        //timeEventDaemon.start(30);
        restoreDaemon.start(delay, period);
    }
}
