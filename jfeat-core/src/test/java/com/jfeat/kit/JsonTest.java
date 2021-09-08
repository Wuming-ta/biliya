/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.kit;

import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 6/17/2015.
 */
public class JsonTest {

    //@Test
    public void test() throws IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream("temp.out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        IMessage obj = new Message();
        obj.setName("ABC");
        Map<String,Object> map = new HashMap<>();
        map.put("version", 2);
        map.put("city", "gz");
        obj.setObject(com.jfinal.kit.JsonKit.toJson(map));
        oos.writeObject(obj);
        oos.flush();
        oos.close();


        FileInputStream fis = new FileInputStream("temp.out");
        ObjectInputStream oin = new ObjectInputStream(fis);
        IMessage o = (IMessage) oin.readObject();
        System.out.println(o.getObject());
    }
}

interface IMessage {
    public String getName();
    public void setName(String name);
    public Object getObject();
    public void setObject(Object object);
}
class Message implements IMessage, Serializable {
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;
    private String name;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
