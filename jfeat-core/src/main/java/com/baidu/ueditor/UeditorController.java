package com.baidu.ueditor;

import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.google.gson.GsonBuilder;
import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.kit.JsonKit;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangjacky on 16/5/31.
 */
public class UeditorController extends BaseController {

    /**
     * 上传到本地图片服务器
     */
    public void upload() {
        String htmlText = new ActionEnter(getRequest(), PhotoGalleryConstants.me().getUploadPath(), PhotoGalleryConstants.me().getHost()).exec();
        renderHtml(htmlText);
    }

    /**
     * 上传到七牛云服务器,
     * 需要在系统启动时设置QiniuKit.me().setAk("xxx").setSk("yyy").setBucketName("bucket").init();
     * config.properties 设置
     * uploadHost = http://o9ixtumvv.bkt.clouddn.com/
     *
     */
    public void qiniu() {
        if (QiniuKit.me().isInited()) {
            String htmlText = new ActionEnter(getRequest(), QiniuKit.me().getTmpdir(), QiniuKit.me().getUrl()).exec();
            try {
                Map<String, Object> map = JsonKit.convertToMap(htmlText);
                String actionType = getPara("action");
                if ("SUCCESS".equals(map.get("state")) && ActionMap.getType(actionType) == ActionMap.UPLOAD_IMAGE) {
                    StringBuilder fileName = new StringBuilder(QiniuKit.me().getTmpdir());
                    fileName.append(map.get("url"));
                    String qiniuFileName = QiniuKit.me().upload(fileName.toString());
                    if (qiniuFileName != null) {
                        File file = new File(fileName.toString());
                        file.delete();
                        map.put("url", qiniuFileName);
                        renderJson(map);
                        return;
                    }
                    State state = new BaseState(false, AppInfo.IO_ERROR);
                    renderJson(state);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                State state = new BaseState(false, AppInfo.IO_ERROR);
                renderJson(state);
            }
            renderHtml(htmlText);
        }
        else {
            String htmlText = new ActionEnter(getRequest(), PhotoGalleryConstants.me().getUploadPath(), PhotoGalleryConstants.me().getHost()).exec();
            renderHtml(htmlText);
        }
    }
}
