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

package com.jfeat.core;

import com.jfeat.core.handler.HttpServletRequestWrapper;
import com.jfeat.flash.FlashKit;
import com.jfeat.flash.IFlashManager;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.OreillyCos;
import com.jfinal.upload.UploadFile;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FileRenamePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseController/* <M extends BaseModel<M>> */extends Controller {

    private static final FileRenamePolicy fileRenamePolicy = new DefaultFileRenamePolicy();

    private boolean setFlashFalg = false;
    private IFlashManager flashManager;
    private Res res;

    public static Logger logger = LoggerFactory.getLogger(BaseController.class);

    public IFlashManager getFlashManager(){
        return this.flashManager;
    }

    public BaseController() {
        // this.setModelClass(getClazz());
        this.flashManager = FlashKit.getFlashManager();
    }

    public Res getRes() {
        res = getAttr("_res");
        if (res == null) {
            res = I18n.use();
        }
        return res;
    }

    // /**
    // * get M's class
    // *
    // * @return M
    // */
    // @SuppressWarnings("unchecked")
    // public Class<M> getClazz() {
    // Type t = getClass().getGenericSuperclass();
    // Type[] params = ((ParameterizedType) t).getActualTypeArguments();
    // return (Class<M>) params[0];
    // }
    //
    // protected Class<M> modelClass;
    //
    // public Class<M> getModelClass() {
    // return modelClass;
    // }
    //
    // public void setModelClass(Class<M> modelClass) {
    // this.modelClass = modelClass;
    // }

    public <T> List<T> getBeans(Class<T> beanClass) {
        return getBeans(beanClass, StrKit.firstCharToLowerCase(beanClass.getSimpleName()));
    }

    public <T> List<T> getBeans(Class<T> beanClass, String beanName) {
        List<String> indexes = getIndexes(beanName);
        List<T> list = new ArrayList<T>();
        for (String index : indexes) {
            T m = getBean(beanClass, beanName + "[" + index + "]");
            if (m != null) {
                list.add(m);
            }
        }
        return list;
    }


    public <T> List<T> getModels(Class<T> modelClass) {
        return getModels(modelClass, StrKit.firstCharToLowerCase(modelClass.getSimpleName()));
    }

    /**
     * 获取前端传来的数组对象并响应成Model列表
     */
    public <T> List<T> getModels(Class<T> modelClass, String modelName) {
        List<String> indexes = getIndexes(modelName);
        List<T> list = new ArrayList<T>();
        for (String index : indexes) {
            T m = getModel(modelClass, modelName + "[" + index + "]", true);
            if (m != null) {
                list.add(m);
            }
        }
        return list;
    }

    /**
     * 提取model对象数组的标号
     */
    public List<String> getIndexes(String modelName) {
        // 提取标号
        List<String> list = new ArrayList<String>();
        String modelNameAndLeft = modelName + "[";
        Map<String, String[]> parasMap = getRequest().getParameterMap();
        for (Map.Entry<String, String[]> e : parasMap.entrySet()) {
            String paraKey = e.getKey();
            if (paraKey.startsWith(modelNameAndLeft)) {
                String no = paraKey.substring(paraKey.indexOf("[") + 1, paraKey.indexOf("]"));
                if (!list.contains(no)) {
                    list.add(no);
                }
            }
        }
        return list;
    }

    public String parsePath(String currentActionPath, String url){
        if(url.startsWith("/")){//完整路径
            return url.split("\\?")[0];
        }else if(!url.contains("/")){//类似于detail的路径。
            return "/"+ currentActionPath.split("/")[1] + "/" + url.split("\\?")[0];
        }else if(url.contains("http:")|| url.contains("https:")){
            return null;
        }
        ///abc/def","bcd/efg?abc
        return currentActionPath + "/" + url.split("\\?")[0];
    }

    public void setFlash(String key, Object value){
        String actionPath = this.getRequest().getRequestURI();
        flashManager.setFlash(this.getSession(true),actionPath, key, value);
        setFlashFalg = true;
    }

    public void forwardAction(String actionUrl) {
        if(setFlashFalg){//若有新加入的Flash。更换key。
            String actionPath = this.getRequest().getRequestURI();
            //将以当前actionPath为key更替为下一个请求actionPath作为key
            flashManager.updateFlashKey(this.getSession(true), actionPath, actionUrl);
            setFlashFalg =false;
        }
        super.forwardAction(actionUrl);
    }

    public void redirect(String url) {
        if(setFlashFalg){
            String actionPath = this.getRequest().getRequestURI();
            String newActionPath = parsePath(actionPath, url);
            flashManager.updateFlashKey(this.getSession(true), actionPath, newActionPath);
            setFlashFalg = false;
        }
        super.redirect(url);
    }

    /**
     * Redirect to url
     */
    public void redirect(String url, boolean withQueryString) {
        if(setFlashFalg){
            String actionPath = this.getRequest().getRequestURI();
            String newActionPath = parsePath(actionPath, url);
            flashManager.updateFlashKey(this.getSession(true), actionPath, newActionPath);
            setFlashFalg = false;
        }
        super.redirect(url, withQueryString);
    }


    // --------
    /**
     * Get upload file from multipart request.
     */

    /**
     * call Super
     */
    public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize,
                                     String encoding, FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        List<UploadFile> files = super.getFiles(saveDirectory, maxPostSize, encoding);
        setXssRequest();
        return files;
    }

    public List<UploadFile> getFiles(String saveDirectory, int maxPostSize, FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        List<UploadFile> files = super.getFiles(saveDirectory, maxPostSize);
        setXssRequest();
        return files;
    }

    public List<UploadFile> getFiles(String saveDirectory, FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        List<UploadFile> files =  super.getFiles(saveDirectory);
        setXssRequest();
        return files;
    }

    public List<UploadFile> getFiles(FileRenamePolicy fileRenamePolicy) {
        OreillyCos.setFileRenamePolicy(fileRenamePolicy);
        List<UploadFile> files = super.getFiles();
        setXssRequest();
        return files;
    }

    private void setXssRequest() {
        String exclude = JFeatConfigKit.me().getProperty("xss.exclude");
        if (!(exclude != null && getRequest().getServletPath().startsWith(exclude))) {
            setHttpServletRequest(new HttpServletRequestWrapper(getRequest()));
        }
    }

    /**
     * call self
     */

    /**
     * File
     */
    public UploadFile getFile(String parameterName, String saveDirectory, Integer maxPostSize, String encoding) {
        return this.getFile(parameterName, saveDirectory, maxPostSize, encoding, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory,
                              Integer maxPostSize, String encoding,
                              FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(saveDirectory, maxPostSize, encoding, fileRenamePolicy);
        return getFile(parameterName, uploadFiles);
    }

    public UploadFile getFile(String parameterName, String saveDirectory, int maxPostSize) {
        return this.getFile(parameterName, saveDirectory, maxPostSize, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory, int maxPostSize, FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(saveDirectory, maxPostSize, fileRenamePolicy);
        return this.getFile(parameterName, uploadFiles);
    }

    public UploadFile getFile(String parameterName, String saveDirectory) {
        return this.getFile(parameterName, saveDirectory, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, String saveDirectory, FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(saveDirectory, fileRenamePolicy);
        return getFile(parameterName, uploadFiles);
    }

    public UploadFile getFile() {
        return this.getFile(fileRenamePolicy);
    }

    public UploadFile getFile(FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(fileRenamePolicy);
        return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
    }

    public UploadFile getFile(String parameterName) {
        return this.getFile(parameterName, fileRenamePolicy);
    }

    public UploadFile getFile(String parameterName, FileRenamePolicy fileRenamePolicy) {
        List<UploadFile> uploadFiles = this.getFiles(fileRenamePolicy);
        return getFile(parameterName, uploadFiles);
    }


    private UploadFile getFile(String parameterName, List<UploadFile> uploadFiles) {
        for (UploadFile uploadFile : uploadFiles) {
            if (uploadFile.getParameterName().equals(parameterName)) {
                return uploadFile;
            }
        }
        return null;
    }

    /**
     * Files
     */
    public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize, String encoding) {
        return this.getFiles(saveDirectory, maxPostSize, encoding, fileRenamePolicy);
    }

    public List<UploadFile> getFiles(String saveDirectory, int maxPostSize) {
        return this.getFiles(saveDirectory, maxPostSize, fileRenamePolicy);
    }

    public List<UploadFile> getFiles(String saveDirectory) {
        return this.getFiles(saveDirectory, fileRenamePolicy);
    }

    public void index() {
        throw new RuntimeException("Unsupported action.");
    }

    public void add() {
        throw new RuntimeException("Unsupported action.");
    }

    public void save() {
        throw new RuntimeException("Unsupported action.");
    }

    public void edit() {
        throw new RuntimeException("Unsupported action.");
    }

    public void update() {
        throw new RuntimeException("Unsupported action.");
    }

    public void delete() {
        throw new RuntimeException("Unsupported action.");
    }

}
