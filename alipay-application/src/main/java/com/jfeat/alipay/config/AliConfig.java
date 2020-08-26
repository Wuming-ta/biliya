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

package com.jfeat.alipay.config;

import com.jfeat.config.model.Config;
import com.jfinal.kit.StrKit;

/**
 * Created by jackyhuang on 16/9/1.
 */
public class AliConfig {

    //String APP_ID = "2016092200567241";
    //String APP_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC3LDXypR6zK7qIPuB0lKdMBLFNZOnkySQ5E36TwPidwpPTxrnGEffsUlztr5iXCzC6yv+64y9AUz66rmei5NujQvC/WrygN4AjsLSjWb7mUPgbLbRtrfMKAufeLcfs8HLKWBlBhNrlCoS+qrrCQ93+mN9C84/XqGRwNlp2TuwbS+zkhQc3ai8fzIwTw2lZQBsSLZvkunMjJyP2hdaYT9kVqR9DRRNSRgrRql2iQcPlUceDb9CcjorfGe7Vs6t/ghTXzaMEWTMC8KbKwX/IwXllgVV8g7twVyUgMbKM1Zq8hp+ukFSxs7cGwcpOXwOHZ6IpCFFH0LTBiU9Jim1aXZO/AgMBAAECggEBAIBAfVly0acEBCwnUkNuXiD03Cvjb30YdLtd8e+EWs8jERxj9WlA5YVbOePWYhHH7Qus3Re+wDPa1X4iZUAB2+NZGHXzE0Ve9uKGdEQ3r1lV8hIdk0qEVDp7RFwlU+mLheQ1gSGF4zohfkZ+BhvFJyd/ZDtdjCakqgaDmB1kLx8Afm5GoMZdA0IX70Qntew4nlwyLaFkvuA3YFW8MllSEOFEeMCJDMK1rEfUzkWJZYje9la1qtSJ9JyhObE4smHb+R0KCqc523Ea2nGtPfhjRNuSy7fiDkKSzaMWfbT4YVB9Z+cE+HAgRYj43BNVOAsCvX7zf7+V4UaXgTsHHkMWn2kCgYEA8Fb/miptdzlb5ivJEqc1vVskXjO4QzOK2WgnXgzllc83saEeFudvAnM/N6PO6TNXocJOe52PeeRLItD/+vbLehAke5Yay5Y8YMf9OMXJU1nsMSyXVObjVNA0bn3PEVE9D/X65C7l/en9EnMaqC5mJzdap1eeL6Eke0mWgRgCjV0CgYEAwxuh+/RyWh+bLgUsKd/ySMGJx8LQekDFcaFZ4953WCCX/VobcQpcewYl+vkT97LT8/hOQvsH61MFystJfGERR2trAa00Ytgz6oktBJ5w/3N97/6vNzf0hK37dwq5RGLgOqsBX1JeeFnT2RPyfVgnRk2V56VxHcY9XSNN9DPSt8sCgYEAyYeJ6RHunGyCfOz2PCftzgwg4MTpm6Aaq9bEO/8AfLA3eUEq4/XIhftxZQx8sdsVflRaTxl9d65Quq8aFqzfTkk+7kqdyVz+4ACSewqOmM/sWAeWFeFNeo7jiEsCWPkQBNQ0PyZ8QH/2b4QJNEFD81pdL7kusa2j06WPCEzDxxkCgYBq9cMftSrbbWhNGljTvAm/CRrXgF16J/gWCzaUTWlvzq6EHdndZqi8aOp5ZAMmT8sSfJWq8qOsxp7uRfPLVAt888595M5LQet0MC+V0KZx5IauAxlbVDmgYyDwadiXPP1gLE9cSnOv1s7kuqK9RI6DZBVCINiaUgth+/DIZ4ClcQKBgQDLkNrILoH84bVdgqct7J+qyh6NKbNgb6L8tRpLs4qiKIFxkq55trnEb3Wz0JXNw2Bcy4xrCe5ja5drxlP78IU63NsmkfdO/YhqYUIMu0wCLJXsRGkeK8PASX07xcSc1cFVP/dhmTD1bq374NQSfhcyT90UvDz9odWZM23wvS5Q9w==";


    public static final String APP_ID_KEY = "ali.app_id";
    public static final String APP_SECRET_KEY = "ali.app_secret";
    public static final String ALIPAY_PUBLIC_KEY_KEY = "ali.alipay_public_key";

    private static final String defaultAlipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1k/X+9T/7emBVegm2sNx+X35fp3xgCuq3tO8GGo51HfWybX89pAwsJYszXkdFCny7R3oJ7qSXRxi54IsdJVNJxlEoQkKvieuGxIjyNQLw705EQA0TCIGiJzDk22gXVrHmO2Okv8G4O+HEKMiea9qrWNYPXTHCxfrZ90YyYetbBql6LLxVyoMkX/vhQdTG3ww6G/t1ADRa1epHLjm9vrMf4AuYSKAgOflUVHA08gpnWrC5mq90VZIs8P5LyDex3AeNaHVYPlPqa6L10DKJb86IsUuWeiHrz7wlbnrvobsenOrabhAD9PLlCZyVdglVV7O6Vb4cUp24yivqZnlg330QIDAQAB";

    private static Config getConfig(String key) {
        Config config = Config.dao.findByKey(key);
            if (config == null) {
                throw new RuntimeException("ali config " + key + " is not set.");
            }
        return config;
    }


    public static String getAppId() {
        return getConfig(APP_ID_KEY).getValue();
    }

    public static String getAppSecret() {
        return getConfig(APP_SECRET_KEY).getValue();
    }

    public static String getAlipayPublicKey() {
        String value = getConfig(ALIPAY_PUBLIC_KEY_KEY).getValue();
        if (StrKit.notBlank(value)) {
            return value;
        }
        return defaultAlipayPublicKey;
    }

}
