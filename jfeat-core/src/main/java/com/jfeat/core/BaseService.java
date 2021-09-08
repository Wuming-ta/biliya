package com.jfeat.core;

import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jacky on 3/8/16.
 */
public abstract class BaseService {
    public static Logger logger = LoggerFactory.getLogger(BaseService.class);
    public static String RESULT = "result";
    public static String MESSAGE = "message";
    public static String DATA = "data";
    public static boolean SUCCESS = true;
    public static boolean FAILURE = false;

    public static boolean isSucceed(Ret ret) {
        Boolean result = ret.get(RESULT);
        return result == SUCCESS;
    }

    public static String getMessage(Ret ret) {
        return ret.get(MESSAGE);
    }

    public Ret success() {
        return Ret.create(RESULT, SUCCESS);
    }

    public Ret success(String message) {
        Ret ret = success();
        ret.put(MESSAGE, message);
        return ret;
    }

    public Ret success(String message, Object data) {
        Ret ret = success(message);
        ret.put(DATA, data);
        return ret;
    }

    public Ret failure() {
        return Ret.create(RESULT, FAILURE);
    }

    public Ret failure(String message) {
        Ret ret = failure();
        ret.put(MESSAGE, message);
        return ret;
    }
}
