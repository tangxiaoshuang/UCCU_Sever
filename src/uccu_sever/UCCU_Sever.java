/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

/**
 * 测试SSH提交
 * @author Xiaoshuang
 */

import java.net.InetAddress;

public class UCCU_Sever {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        // TODO code application logic here
        Shell sh = new Shell();
        UccuLogger.setOptions("logs/GameServer/",LogMode.DEBUG);
        
        GameServer gs = new GameServer(true, true, 100);
        AioModule aio = new AioModule(gs, gs, gs);
        try {
            aio.init(InetAddress.getLocalHost().getHostAddress(), 8998, 8);
        }
        catch (Exception e) {
        }
        gs.init(aio, Const.DBAddress, Const.DBPort);
        sh.startShell();
    }
    
}
