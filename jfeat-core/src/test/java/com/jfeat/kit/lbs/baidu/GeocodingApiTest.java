package com.jfeat.kit.lbs.baidu;

import com.jfeat.kit.lbs.baidu.model.GeoAddressResult;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ehngjen on 1/11/2016.
 */
public class GeocodingApiTest {

    @Before
    public void setup() {
        ApiConfigKit.me().setAk("F22i25OO7Svw8cQY1lr6g5hr");
        ApiConfigKit.me().setSk("LbvHyhHxWFpF104wGDG2hdf92duxxQ4V");
    }

    //@Test
    public void testLocation() throws UnsupportedEncodingException {
        Double[] lats = {
                  24.28127568888857
                , 24.282440618848977
                , 24.275487305156105
                , 24.27346160273996
                , 24.28584620318067
                , 24.308043334996032
                , 24.29006781772964
                , 24.27346160273996
                , 24.29984434781404
                , 24.2951354202061
                , 24.313975979373517
                , 24.284780374930875
                , 24.32189789135463
                , 24.318880282454515
                , 24.2979155085418
                , 24.32335743311788
                , 24.295187348044234
                , 24.28540551667955
                , 24.296573858819738
                , 24.332927588729394
                , 24.28158732968262
                , 24.29298982196302
                , 24.317279519250324
                , 24.318629707449574
                , 24.31267596053993
                , 24.31760688141588
                , 24.2859808170111
                , 24.293898034998705
                , 24.319359716631183
                , 24.27992773487921
                , 24.332927588729394
                , 24.31955914851864
        };
        Double[] lngs = {
                  116.10692155037341
                , 116.09471761009196
                , 116.09505953571134
                , 116.09073300352912
                , 116.10178741189391
                , 116.12018933949707
                , 116.09697813302569
                , 116.09073300352912
                , 116.10486845426661
                , 116.10165070865975
                , 116.1102029669765
                , 116.10305019543193
                , 116.1188185252893
                , 116.11147783946092
                , 116.07815133656052
                , 116.11532232024561
                , 116.10125026023303
                , 116.08555024246729
                , 116.10584177816772
                , 116.13381124424588
                , 116.10781230652358
                , 116.10316375157886
                , 116.10986161088283
                , 116.11101943685424
                , 116.10929648688668
                , 116.11054988447958
                , 116.08710509252484
                , 116.10116126510619
                , 116.10582891263621
                , 116.0891554179927
                , 116.13381124424588
                , 116.10598987910218
        };
        for (int i = 0; i < lats.length; i++) {
            GeoAddressResult result = (GeoAddressResult) GeocodingApi.location(lats[i], lngs[i]);
            System.out.println(result.getJson());
            System.out.println(result.getFormattedAddress() + "|" + lats[i] + "|" + lngs[i]);
            //System.out.println(result.getAddressComponent());
        }
    }

    //@Test
    public void testAddress() throws UnsupportedEncodingException {
        String[] addresses = {
                  "梅州市梅县区新县城扶外路74-26"
                , "梅州市梅县区新县城步行街248号（距离皇朝假日酒店50米）"
                , "梅州市梅县新县城沟湖路华景美都6号店"
                , "梅州市梅县区宪梓中路新县城文化城旁（宪梓中路与莲心路交界）"
                , "梅州市梅县区大新城和安花园新贵街68号（大铜钱直入100米左右）"
                , "梅州市梅县区沿江半岛彩虹巷15-16号（德龙桥头）"
                , "梅州市梅县区人民南路154号(锦绣花城斜对面)"
                , "梅州市梅县区新县城扶外路（365连锁酒店斜对面）"
                , "梅州市梅县区科技路卜蜂莲花超市正门侧"
                , "梅州市梅县区巡警路与中环一路交叉口向东转，（米其林轮轮胎斜对面）"
                , "梅州市梅县区新峰路38号（乐育中学斜对面）"
                , "梅州市梅县区宪梓中路70号"
                , "梅州市梅县区八一大道11号（五洲城车站往前200米）"
                , "梅州市梅县区广梅三路76号（梦家商务酒店旁边）"
                , "梅州市梅县区新城健身中心内"
                , "梅州市梅县区五洲城五洲路中国银行旁边"
                , "梅州市梅县区巡警路华银新区新都豪庭C4栋"
                , "梅州市梅县区新县城广场对面，合和花园13、14号（即售楼处侧）"
                , "梅州市梅县区华侨城世纪大道中国银行南侧"
                , "梅州市梅县区嘉应学院校本部嘉园路入口20米2楼（嘉应学院东区宿舍）"
                , "梅州市梅县区美食街（东山中学分校后门直入100米）"
                , "梅州市梅县区人民大道新区和盛花园（人民南路与扶山路交叉口南行50米，百福大药房旁边）"
                , "梅州市梅县区黄塘路6号，育智幼儿园斜对面"
                , "梅州市梅县区白围巷1号"
                , "梅州市梅县区中高峰市场1号"
                , "梅州市梅县区黄塘十字路口96号店（大地琴行北邻）"
                , "梅州市梅县区新县城人民广场对面合和花园售楼处旁"
                , "梅州市梅县区新县城鸿运豪庭A2栋11号（新县城蜀香源斜对面）"
                , "梅州市梅县区黄塘路120号"
                , "梅州市梅县区大新路394号"
                , "梅州市梅县区嘉应学院嘉园路口入25米1楼"
                , "梅州市梅县区黄塘路黄塘医学院正门门口"
        };
        for (String address : addresses) {
            ApiResult apiResult = GeocodingApi.address(address);
            Map result = apiResult.get("result");
            Map location = (Map) result.get("location");
            double lng = (double) location.get("lng");
            double lat = (double) location.get("lat");
            System.out.println(address + "|" + lat + "|" + lng);
        }
    }

    @Test
    public void a() throws UnsupportedEncodingException {
        double lat = 23.129163;
        double lng = 113.264435;
        GeoAddressResult result = (GeoAddressResult) GeocodingApi.location(lat, lng);
        System.out.println(result.getJson());
    }
}
