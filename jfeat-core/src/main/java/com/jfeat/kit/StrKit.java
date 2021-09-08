package com.jfeat.kit;

/**
 * @author jackyhuang
 * @date 2019/12/1
 */
public class StrKit {

    /**
     * get substring of length bytes
     * @param str original string
     * @param length bytes
     * @return
     */
    public static String getSubString(String str, int length) {
        int count = 0;
        int offset = 0;
        char[] c = str.toCharArray();
        int size = c.length;
        if(size >= length){
            for (int i = 0; i < c.length; i++) {
                if (c[i] > 256) {
                    offset = 2;
                    count += 2;
                } else {
                    offset = 1;
                    count++;
                }
                if (count == length) {
                    return str.substring(0, i + 1);
                }
                if ((count == length + 1 && offset == 2)) {
                    return str.substring(0, i);
                }
            }
        }else{
            return str;
        }
        return "";
    }

    /**
     * negotiate a sub-string of specific length
     * @param str
     * @param length
     * @return
     */
    public static String negoString(String str, int length) {
        if (com.jfinal.kit.StrKit.notBlank(str)) {
            return getSubString(str, length);
        }
        return str;
    }
}
