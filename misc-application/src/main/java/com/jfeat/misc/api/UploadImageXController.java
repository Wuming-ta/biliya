package com.jfeat.misc.api;

import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.RestController;
import com.jfeat.core.UploadedFile;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerBind(controllerKey = "/rest/upload_image_x")
public class UploadImageXController extends RestController {

    /**
     * POST /rest/upload_image_x
     * data: multipart form
     */
    @Override
    public void save() {
        String subDir = "upload/" + DateKit.today("yyyy-MM-dd");
        List<UploadFile> uploadFiles = getUploadFiles(subDir);
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            renderFailure("upload.failure");
            return;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (UploadFile uploadFile : uploadFiles) {
            Map<String, Object> map = new HashMap<>();
            String url = getUploadFileUrl(uploadFile, subDir);
            map.put("file_name", uploadFile.getFileName());
            map.put("original_file_name", uploadFile.getOriginalFileName());
            map.put("size", uploadFile.getFile().length());
            map.put("url", url);
            list.add(map);
        }

        renderSuccess(list);
    }

    private List<UploadFile> getUploadFiles(String subDir) {
        if (QiniuKit.me().isInited()) {
            return getFiles(QiniuKit.me().getTmpdir());
        }

        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        return getFiles(PhotoGalleryConstants.me().getUploadPath(), policy);
    }

    private String getUploadFileUrl(UploadFile uploadFile, String subDir) {
        if (QiniuKit.me().isInited()) {
            return saveToQiniu(uploadFile);
        }
        return UploadedFile.buildUrl(uploadFile, subDir);
    }

    private String saveToQiniu(UploadFile uploadFile) {
        String url = QiniuKit.me().upload(uploadFile.getFile().getAbsolutePath());
        if (url != null) {
            logger.debug("deleted after saved to qiniu.");
            uploadFile.getFile().delete();
            return QiniuKit.me().getFullUrl(url);
        }
        return null;
    }
}
