/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

/**
 *
 * @author xiaoshuang
 */
public class Daemons {
    public static TimeEventDaemon timeEventDaemon = new TimeEventDaemon(10);
    public static void addSingleEvent(TimeEvent event)
    {
        timeEventDaemon.addSingleEvent(event);
    }
}
