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

package com.jfeat.jms;

/**
 * Created by ehngjen on 3/24/2015.
 */
public class Receiver {
    private String name;
    private Integer messageType;
    private String resolver;

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Receiver() {
    }

    public Receiver(String name, Integer messageType, String resolver) {
        this.name = name;
        this.messageType = messageType;
        this.resolver = resolver;
    }


    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result
                + ((messageType == null) ? 0 : messageType.hashCode());
        result = PRIME * result
                + ((resolver == null) ? 0 : resolver.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Receiver other = (Receiver) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (messageType == null)
        {
            if (other.messageType != null)
                return false;
        } else if (!messageType.equals(other.messageType))
            return false;
        if (resolver == null)
        {
            if (other.resolver != null)
                return false;
        } else if (!resolver.equals(other.resolver))
            return false;
        return true;
    }
}
