/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uccu_sever;

/**
 *
 * @author xiaoshuang
 */
public class UccuException extends Exception{
    String msg;
    public UccuException(String msg) {
        this.msg = msg;
    }
    @Override
    public String getMessage()
    {
        return msg;
    }
}
