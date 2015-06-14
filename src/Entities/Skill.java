/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import Logics.SkillLogic;
import java.util.Scanner;
import uccu_sever.UccuLogger;

/**
 *
 * @author xiaoshuang
 */
public class Skill extends Entity{
    //封装逻辑块
    public static Skill empty = new Skill(-1, "空", "空", 0);
    
    public static int segment = 1_000_000_000;//技能统一段偏移，方便处理一些技能和物品统一的问题
    
    public static String[] properties = {"[ID]",
                                         "[NAME]",
                                         "[DESCRIPTION]",
                                         "[COLDDOWN]",
                                         "[TAGS]",
                                         "[DATABANK]",
                                         "[LOGICS]"};
    
    
    public int coldDown;//释放间隔
    TagManager tags;
    DataBank dataBank;
    SkillLogic skillLogic;
    
    public Skill(int id, String name, String description,
            int coldDown) {
        super(id, name, description);
        this.coldDown = coldDown;
        this.tags = new TagManager();
        this.dataBank = new DataBank();
        this.skillLogic = new SkillLogic();
    }
    public Skill(Scanner scr)
    {
        super(-1, null, null);
        for(int i = 0; i < properties.length; i++)
        {
            if(!scr.next().equalsIgnoreCase(properties[i]))
            {
                UccuLogger.warn("Skill/Constructor", properties[i]+ " is missing!");
                return;
            }
            switch(i)
            {
                case 0:
                    this.id = scr.nextInt();
                    if(id<segment)
                        id+=segment;//自动补充偏移
                    break;
                case 1:
                    this.name = scr.next();
                    break;
                case 2:
                    this.description = scr.nextLine().trim();
                    break;
                case 3:
                    this.coldDown = scr.nextInt();
                    break;
                case 4:
                    this.tags = new TagManager(scr);
                    break;
                case 5:
                    this.dataBank = new DataBank(scr);
                    break;
                case 6:
                {
                    String name = scr.next();
                    if(name.equalsIgnoreCase("[/LOGICS]"))
                    {
                        this.skillLogic = new SkillLogic();
                        break;
                    }
                    try {
                        this.skillLogic = (SkillLogic)Managers.logicManager.get(name);
                    } catch (Exception e) {
                        UccuLogger.warn("Skill/Constructor", "SkillLogic "+name + " DON'T exist!");
                    }
                }
            }
        }
    }
    
    public void cast(SkillInstance skillins, Character player, KvPair a1, KvPair a2)
    {
        lockRead();
        skillLogic.lockRead();
        skillLogic.cast(skillins, player, a1, a2);
        skillLogic.unlockRead();
        unlockRead();
    }
            
    public static boolean isSkill(int id)
    {
        return id >= segment;
    }
    public boolean hasTag(String name)
    {
        lockRead();
        tags.lockRead();
        
        try {
            return tags.has(name);
        } finally {
            tags.unlockRead();
            unlockRead();
        }
    }
    
}
