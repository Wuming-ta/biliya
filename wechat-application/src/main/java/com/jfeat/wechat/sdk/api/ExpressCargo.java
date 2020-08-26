package com.jfeat.wechat.sdk.api;

import com.jfeat.kit.StrKit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2019/11/9
 */
public class ExpressCargo {
    /**
     * count 	number 		是 	包裹数量, 需要和detail_list size保持一致
     * weight 	number 		是 	包裹总重量，单位是千克(kg)
     * space_x 	number 		是 	包裹长度，单位厘米(cm)
     * space_y 	number 		是 	包裹宽度，单位厘米(cm)
     * space_z 	number 		是 	包裹高度，单位厘米(cm)
     * detail_list 	Array.<Object> 		是 	包裹中商品详情列表
     */


    private Integer count;
    private Integer weight;
    private Integer space_x;
    private Integer space_y;
    private Integer space_z;
    private List<Detail> detail_list = new ArrayList<>();

    public Integer getCount() {
        return count;
    }

    public ExpressCargo setCount(Integer count) {
        this.count = count;
        return this;
    }

    public Integer getWeight() {
        return weight;
    }

    public ExpressCargo setWeight(Integer weight) {
        this.weight = weight;
        return this;
    }

    public Integer getSpace_x() {
        return space_x;
    }

    public ExpressCargo setSpace_x(Integer space_x) {
        this.space_x = space_x;
        return this;
    }

    public Integer getSpace_y() {
        return space_y;
    }

    public ExpressCargo setSpace_y(Integer space_y) {
        this.space_y = space_y;
        return this;
    }

    public Integer getSpace_z() {
        return space_z;
    }

    public ExpressCargo setSpace_z(Integer space_z) {
        this.space_z = space_z;
        return this;
    }

    public List<Detail> getDetail_list() {
        return detail_list;
    }

    public ExpressCargo setDetail_list(List<Detail> detail_list) {
        this.detail_list = detail_list;
        return this;
    }

    public static class Detail {
        private String name;
        private Integer count;

        public String getName() {
            return name;
        }

        public Detail setName(String name) {
            this.name = StrKit.negoString(name, 128);
            return this;
        }

        public Integer getCount() {
            return count;
        }

        public Detail setCount(Integer count) {
            this.count = count;
            return this;
        }
    }
}
