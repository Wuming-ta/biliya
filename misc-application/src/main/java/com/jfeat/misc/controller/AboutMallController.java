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

package com.jfeat.misc.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.flash.Flash;
import com.jfeat.misc.model.AboutMall;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.upload.UploadFile;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by jingfei on 2016/5/11.
 */
public class AboutMallController extends BaseController {

    @Override
    @Before(Flash.class)
    public void index(){
        setAttr("about", AboutMall.dao.getDefault());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void update() {
        AboutMall defaultAbout = AboutMall.dao.getDefault();
        String subDir = "about";
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        UploadFile about = this.getFile("about", PhotoGalleryConstants.me().getUploadPath(), policy);
        if(about != null){
            UploadedFile.remove(defaultAbout.getImage());
            String url = UploadedFile.buildUrl(about, subDir);
            defaultAbout.setImage(url);
        }
        AboutMall aboutMall = getModel(AboutMall.class);
        defaultAbout.setContent(aboutMall.getContent());

        defaultAbout.update();
        setFlash("message", getRes().get("misc.aboutMall.update.success"));
        redirect("/about_mall");
    }

}
