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

import java.text.*;

/**
 * Created by jacky on 8/16/14.
 */
public class MessageKit {

    public static String format(String fmt, Object ...args) {
        MessageFormat messageFormat = new MessageFormat(fmt);
        return messageFormat.format(args);
    }
}
