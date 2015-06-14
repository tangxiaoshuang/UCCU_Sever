/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import Entities.Character;
import Entities.KvPair;
import Entities.Managers;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import uccu_sever.AioSession;
import uccu_sever.Datagram;
import uccu_sever.Server;
import uccu_sever.Target;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class RestoreDaemon extends TimerTask{
    AioSession database = Server.gameServer.database;
    @Override
    public void run() {
        UccuLogger.debug("RestoreDeamon/Run", "RestoreDeamon start restoring characters to Database.");
        ByteBuffer msg = ByteBuffer.allocate(2048);
        Managers.characterManager.lockRead();
        Collection<KvPair> chas = Managers.characters();
        UccuLogger.debug("RestoreDeamon/Run", "Total characters in cache: "+ chas.size());
        
        int cnt = 0;
        
        Iterator itr = chas.iterator();
        while(itr.hasNext())
        {
            Character cha = (Character)itr.next();
            cha.lockWrite();
            if(cha.dirty)
            {
                cha.lockRead();
                cha.dirty = false;
                cha.unlockWrite();
                
                cha.pack(msg);
                database.write(Datagram.wrap(msg, Target.DB, 0x04));
                msg.clear();
                
                cha.packSkillScroll(msg);
                database.write(Datagram.wrap(msg, Target.DB, 0x0D));
                msg.clear();
                
                cha.packInventory(msg);
                database.write(Datagram.wrap(msg, Target.DB, 0x07));
                msg.clear();
                
                cha.packColdDown(msg);
                database.write(Datagram.wrap(msg, Target.DB, 0x09));
                
                UccuLogger.debug("RestoreDeamon/Run", "Restore character id ="+ cha.id +" to Database!");
                cnt++;
                cha.unlockRead();
            }
            else
                cha.unlockWrite();
            
        }
        UccuLogger.kernel("RestoreDeamon/Run", cnt+" chacters restored.");
        Managers.characterManager.unlockRead();
    }
    
}
