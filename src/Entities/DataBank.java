/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.util.Scanner;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
/*
    定义XXX_f 表示 float
    定义XXX_d 表示double

*/
public class DataBank extends MutexValueManager<String, DataItem>{//灵活地储存数据
    public DataBank()
    {
        super();
    }
    public DataBank(Scanner scr)//扫描器已读到DataBank项
    {
        super();
        String name;
        while(!(name = scr.next()).equalsIgnoreCase("[/DATABANK]"))
        {
            if(name.endsWith("_f")||name.endsWith("_F"))//float
            {
                Float f = scr.nextFloat();
                try {
                    this.add(new DataItem(name, f));
                } catch (Exception ex) {
                    UccuLogger.warn("DataBank/Constructor", ex.getMessage());
                }
            }
            else if(name.endsWith("_d")||name.endsWith("_D"))//float
            {
                Double d = scr.nextDouble();
                try {
                    this.add(new DataItem(name, d));
                } catch (Exception ex) {
                    UccuLogger.warn("DataBank/Constructor", ex.getMessage());
                }
            }
            else if(name.endsWith("_s")||name.endsWith("_S"))//float
            {
                String s = scr.next();
                try {
                    this.add(new DataItem(name, s));
                } catch (Exception ex) {
                    UccuLogger.warn("DataBank/Constructor", ex.getMessage());
                }
            }
            else
            {
                Integer i = scr.nextInt();
                try {
                    this.add(new DataItem(name, i));
                } catch (Exception ex) {
                    UccuLogger.warn("DataBank/Constructor", ex.getMessage());
                }
            }
        }
    }
}
