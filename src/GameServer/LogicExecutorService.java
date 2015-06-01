/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameServer;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uccu_sever.AioSession;

/**
 *
 * @author xiaoshuang
 */
public class LogicExecutorService {
    ExecutorService executorService;
    public LogicExecutorService(int size)
    {
        executorService = Executors.newFixedThreadPool(size);
    }
    public void handle(char sn, ByteBuffer buf, AioSession session)
    {
        executorService.execute(new LogicExecutor(sn, buf, session));
    }
}
