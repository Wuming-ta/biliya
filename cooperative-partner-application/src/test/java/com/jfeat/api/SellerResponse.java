/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jingfei on 2016/3/31.
 */
public class SellerResponse extends Response{

    private Sellers data;

    public Sellers getData() {
        return data;
    }
}

class Sellers extends HashMap{

    public String toString(){
        StringBuilder s = new StringBuilder();
        for (int i=0;i<this.size();i++){
            s.append(this.get(i));
            s.append("\r\n");
        }
        return s.toString();
    }

    public int valueSize(){
        List<Object> objects = (List<Object>)this.get("children");
        if (objects!= null){
            return objects.size();
        }
        return -1;
    }
}