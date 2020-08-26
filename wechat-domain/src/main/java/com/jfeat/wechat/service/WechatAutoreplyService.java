package com.jfeat.wechat.service;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseService;
import com.jfeat.wechat.model.WechatKeywordAutoreply;
import com.jfeat.wechat.model.WechatKeywordAutoreplyItem;
import com.jfeat.wechat.model.WechatMessageAutoreply;
import com.jfeat.wechat.model.WechatSubscribeAutoreply;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by kang on 2017/5/6.
 */
public class WechatAutoreplyService extends BaseService {

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    public Ret enableOrDisableWechatSubscribeAutoreplyText(Integer wechatSubscribeAutoreplyId, Integer enabled) {
        if (wechatSubscribeAutoreplyId == null) {
            return failure("wechatSubscribeAutoreplyId.is.null");
        }
        WechatSubscribeAutoreply wechatSubscribeAutoreply = WechatSubscribeAutoreply.dao.findById(wechatSubscribeAutoreplyId);
        if (wechatSubscribeAutoreply == null) {
            return failure("wechatSubscribeAutoreply.not.found");
        }
        if (enabled == null || (!enabled.equals(ENABLED) && !enabled.equals(DISABLED))) {
            return failure("enabled.must.be.0.or.1");
        }
        if (enabled.equals(ENABLED)) {
            enableOrDisableAllWechatSubscribeAutoreplyNewses(DISABLED);
        }
        wechatSubscribeAutoreply.setEnabled(enabled);
        wechatSubscribeAutoreply.update();
        return success();
    }

    public Ret enableOrDisableWechatSubscribeAutoreplyNews(Integer wechatSubscribeAutoreplyId, Integer enabled) {
        if (wechatSubscribeAutoreplyId == null) {
            return failure("wechatSubscribeAutoreplyId.is.null");
        }
        WechatSubscribeAutoreply wechatSubscribeAutoreply = WechatSubscribeAutoreply.dao.findById(wechatSubscribeAutoreplyId);
        if (wechatSubscribeAutoreply == null) {
            return failure("wechatSubscribeAutoreply.not.found");
        }
        if (enabled == null || (!enabled.equals(ENABLED) && !enabled.equals(DISABLED))) {
            return failure("enabled.must.be.0.or.1");
        }
        if (enabled.equals(ENABLED)) {
            enableOrDisableWechatSubscribeAutoreplyText(WechatSubscribeAutoreply.dao.findFirstByField(WechatSubscribeAutoreply.Fields.TYPE.toString(), WechatSubscribeAutoreply.Type.TEXT.toString()).getId(), DISABLED);
        }
        wechatSubscribeAutoreply.setEnabled(enabled);
        wechatSubscribeAutoreply.update();
        return success();
    }

    public Ret enableOrDisableAllWechatSubscribeAutoreplyNewses(Integer enabled) {
        if (enabled == null || !(enabled.equals(ENABLED) || enabled.equals(DISABLED))) {
            throw new RuntimeException("enabled.must.be.0.or.1");
        }
        List<WechatSubscribeAutoreply> wechatSubscribeAutoreplyList = WechatSubscribeAutoreply.dao.findByTypeAndEnabled(WechatSubscribeAutoreply.Type.NEWS.toString(), null);
        for (WechatSubscribeAutoreply wechatSubscribeAutoreply : wechatSubscribeAutoreplyList) {
            wechatSubscribeAutoreply.setEnabled(enabled);
            wechatSubscribeAutoreply.update();
        }
        return success();
    }

    public Ret enableOrDisableWechatMessageAutoreplyText(Integer wechatMessageAutoreplyId, Integer enabled) {
        if (wechatMessageAutoreplyId == null) {
            return failure("wechatMessageAutoreplyId.is.null");
        }
        WechatMessageAutoreply wechatMessageAutoreply = WechatMessageAutoreply.dao.findById(wechatMessageAutoreplyId);
        if (wechatMessageAutoreply == null) {
            return failure("wechatMessageAutoreply.not.found");
        }
        if (enabled == null || (!enabled.equals(ENABLED) && !enabled.equals(DISABLED))) {
            return failure("enabled.must.be.0.or.1");
        }
        if (enabled.equals(ENABLED)) {
            enableOrDisableAllWechatMessageAutoreplyNewses(DISABLED);
        }
        wechatMessageAutoreply.setEnabled(enabled);
        wechatMessageAutoreply.update();
        return success();
    }

    public Ret enableOrDisableWechatMessageAutoreplyNews(Integer wechatMessageAutoreplyId, Integer enabled) {
        if (wechatMessageAutoreplyId == null) {
            return failure("wechatMessageAutoreplyId.is.null");
        }
        WechatMessageAutoreply wechatMessageAutoreply = WechatMessageAutoreply.dao.findById(wechatMessageAutoreplyId);
        if (wechatMessageAutoreply == null) {
            return failure("wechatMessageAutoreply.not.found");
        }
        if (enabled == null || (!enabled.equals(ENABLED) && !enabled.equals(DISABLED))) {
            return failure("enabled.must.be.0.or.1");
        }
        if (enabled.equals(ENABLED)) {
            enableOrDisableWechatMessageAutoreplyText(WechatMessageAutoreply.dao.findFirstByField(WechatMessageAutoreply.Fields.TYPE.toString(), WechatMessageAutoreply.Type.TEXT.toString()).getId(), DISABLED);
        }
        wechatMessageAutoreply.setEnabled(enabled);
        wechatMessageAutoreply.update();
        return success();
    }

    public Ret enableOrDisableAllWechatMessageAutoreplyNewses(Integer enabled) {
        if (enabled == null || !(enabled.equals(ENABLED) || enabled.equals(DISABLED))) {
            throw new RuntimeException("enabled.must.be.0.or.1");
        }
        List<WechatMessageAutoreply> wechatMessageAutoreplyList = WechatMessageAutoreply.dao.findByTypeAndEnabled(WechatMessageAutoreply.Type.NEWS.toString(), null);
        for (WechatMessageAutoreply wechatMessageAutoreply : wechatMessageAutoreplyList) {
            wechatMessageAutoreply.setEnabled(enabled);
            wechatMessageAutoreply.update();
        }
        return success();
    }

    public Ret enableOrDisableWechatKeywordAutoreplyItemText(Integer wechatKeywordAutoreplyItemId, Integer enabled) {
        if (wechatKeywordAutoreplyItemId == null) {
            return failure("wechatKeywordAutoreplyItemId.is.null");
        }
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findById(wechatKeywordAutoreplyItemId);
        if (wechatKeywordAutoreplyItem == null) {
            return failure("wechatKeywordAutoreplyItem.not.found");
        }
        if (enabled == null || (!enabled.equals(ENABLED) && !enabled.equals(DISABLED))) {
            return failure("enabled.must.be.0.or.1");
        }
        if (enabled.equals(ENABLED)) {
            enableOrDisableAllWechatKeywordAutoreplyItemNewses(wechatKeywordAutoreplyItem.getKeywordId(), DISABLED);
        }
        wechatKeywordAutoreplyItem.setEnabled(enabled);
        wechatKeywordAutoreplyItem.update();
        return success();
    }

    public Ret enableOrDisableWechatKeywordAutoreplyItemNews(Integer wechatKeywordAutoreplyItemId, Integer enabled) {
        if (wechatKeywordAutoreplyItemId == null) {
            return failure("wechatKeywordAutoreplyItemId.is.null");
        }
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItem.dao.findById(wechatKeywordAutoreplyItemId);
        if (wechatKeywordAutoreplyItem == null) {
            return failure("wechatKeywordAutoreplyItem.not.found");
        }
        if (enabled == null || (!enabled.equals(ENABLED) && !enabled.equals(DISABLED))) {
            return failure("enabled.must.be.0.or.1");
        }
        if (enabled.equals(ENABLED)) {
            enableOrDisableWechatKeywordAutoreplyItemText(wechatKeywordAutoreplyItem.getWechatKeywordAutoreply().getText().getId(), DISABLED);
        }
        wechatKeywordAutoreplyItem.setEnabled(enabled);
        wechatKeywordAutoreplyItem.update();
        return success();
    }

    public Ret enableOrDisableAllWechatKeywordAutoreplyItemNewses(Integer wechatKeywordAutoreplyId, Integer enabled) {
        if (wechatKeywordAutoreplyId == null) {
            return failure("wechatKeywordAutoreplyId.is.null");
        }
        WechatKeywordAutoreply wechatKeywordAutoreply = WechatKeywordAutoreply.dao.findById(wechatKeywordAutoreplyId);
        if (wechatKeywordAutoreply == null) {
            return failure("wechatKeywordAutoreply.not.found");
        }
        if (enabled == null || !(enabled.equals(ENABLED) || enabled.equals(DISABLED))) {
            throw new RuntimeException("enabled.must.be.0.or.1");
        }
        List<WechatKeywordAutoreplyItem> wechatKeywordAutoreplyItemList = wechatKeywordAutoreply.findByTypeAndEnabled(WechatKeywordAutoreplyItem.Type.NEWS.toString(), null);
        for (WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem : wechatKeywordAutoreplyItemList) {
            wechatKeywordAutoreplyItem.setEnabled(enabled);
            wechatKeywordAutoreplyItem.update();
        }
        return success();
    }

    public OutMsg getSubscribeOutMsg(InMsg inMsg) {
        WechatSubscribeAutoreply wechatSubscribeAutoreplyText = WechatSubscribeAutoreply.dao.findFirstByTypeAndEnabled(WechatSubscribeAutoreply.Type.TEXT.toString(), ENABLED);
        List<WechatSubscribeAutoreply> wechatSubscribeAutoreplynNewses = WechatSubscribeAutoreply.dao.findByTypeAndEnabled(WechatSubscribeAutoreply.Type.NEWS.toString(), ENABLED);
        List<WechatSubscribeAutoreply> wechatSubscribeAutoreplyAll = Lists.newLinkedList();
        if (wechatSubscribeAutoreplyText != null&&StrKit.notBlank(wechatSubscribeAutoreplyText.getContent())) {
            wechatSubscribeAutoreplyAll.add(wechatSubscribeAutoreplyText);
        }
        if (wechatSubscribeAutoreplynNewses.size() > 0) {
            wechatSubscribeAutoreplyAll.addAll(wechatSubscribeAutoreplynNewses);
        }
        if (wechatSubscribeAutoreplyAll.size() == 0) {
            return null;
        }
        int ranNum = new Random().nextInt(wechatSubscribeAutoreplyAll.size());
        WechatSubscribeAutoreply wechatSubscribeAutoreply = wechatSubscribeAutoreplyAll.get(ranNum);
        if (WechatSubscribeAutoreply.Type.TEXT.toString().equals(wechatSubscribeAutoreply.getType())) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            StringBuilder contentBuilder = new StringBuilder();
            String split = "";
            for (String content : wechatSubscribeAutoreply.getContent().split("\\n")) {
                contentBuilder.append(split).append(content);
                split = "\\n";
            }
            outTextMsg.setContent(contentBuilder.toString());
            outTextMsg.setCreateTime((int) (System.currentTimeMillis() / 1000));
            return outTextMsg;
        }
        if (WechatSubscribeAutoreply.Type.NEWS.toString().equals(wechatSubscribeAutoreply.getType())) {
            List<News> newsList = Lists.newLinkedList();
            News news = new News();
            news.setTitle(wechatSubscribeAutoreply.getTitle());
            news.setUrl(wechatSubscribeAutoreply.getUrl());
            news.setPicUrl(wechatSubscribeAutoreply.getThumbUrl());
            newsList.add(news);
            OutNewsMsg outNewsMsg = new OutNewsMsg(inMsg);
            outNewsMsg.setArticles(newsList);
            outNewsMsg.setCreateTime((int) (System.currentTimeMillis() / 1000));
            return outNewsMsg;
        }
        return null;
    }

    public OutMsg getMessageOutMsg(InMsg inMsg) {
        WechatMessageAutoreply wechatMessageAutoreplyText = WechatMessageAutoreply.dao.findFirstByTypeAndEnabled(WechatMessageAutoreply.Type.TEXT.toString(), ENABLED);
        List<WechatMessageAutoreply> wechatMessageAutoreplynNewses = WechatMessageAutoreply.dao.findByTypeAndEnabled(WechatMessageAutoreply.Type.NEWS.toString(), ENABLED);
        List<WechatMessageAutoreply> wechatMessageAutoreplyAll = Lists.newLinkedList();
        if (wechatMessageAutoreplyText != null && StrKit.notBlank(wechatMessageAutoreplyText.getContent())) {
            wechatMessageAutoreplyAll.add(wechatMessageAutoreplyText);
        }
        if (wechatMessageAutoreplynNewses.size() > 0) {
            wechatMessageAutoreplyAll.addAll(wechatMessageAutoreplynNewses);
        }
        if (wechatMessageAutoreplyAll.size() == 0) {
            return null;
        }
        int ranNum = new Random().nextInt(wechatMessageAutoreplyAll.size());
        WechatMessageAutoreply wechatMessageAutoreply = wechatMessageAutoreplyAll.get(ranNum);
        if (WechatMessageAutoreply.Type.TEXT.toString().equals(wechatMessageAutoreply.getType())) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            StringBuilder contentBuilder = new StringBuilder();
            String split = "";
            for (String content : wechatMessageAutoreply.getContent().split("\\n")) {
                contentBuilder.append(split).append(content);
                split = "\\n";
            }
            outTextMsg.setContent(contentBuilder.toString());
            outTextMsg.setCreateTime((int) (System.currentTimeMillis() / 1000));
            return outTextMsg;
        }
        if (WechatMessageAutoreply.Type.NEWS.toString().equals(wechatMessageAutoreply.getType())) {
            List<News> newsList = Lists.newLinkedList();
            News news = new News();
            news.setTitle(wechatMessageAutoreply.getTitle());
            news.setUrl(wechatMessageAutoreply.getUrl());
            news.setPicUrl(wechatMessageAutoreply.getThumbUrl());
            newsList.add(news);
            OutNewsMsg outNewsMsg = new OutNewsMsg(inMsg);
            outNewsMsg.setArticles(newsList);
            outNewsMsg.setCreateTime((int) (System.currentTimeMillis() / 1000));
            return outNewsMsg;
        }
        return null;
    }

    public WechatKeywordAutoreply getKeywordToSend(String contentFromUser) {
        if (StrKit.isBlank(contentFromUser)) {
            return null;
        }
        List<WechatKeywordAutoreply> wechatKeywordAutoreplies = WechatKeywordAutoreply.dao.findByField(WechatKeywordAutoreply.Fields.ENABLED.toString(), ENABLED);
        if (wechatKeywordAutoreplies.size() == 0) {
            return null;
        }
        for (WechatKeywordAutoreply wechatKeywordAutoreply : wechatKeywordAutoreplies) {
            if (StrKit.notBlank(wechatKeywordAutoreply.getKeyword())) {
                String keywordWithSeparator = wechatKeywordAutoreply.getKeyword().trim();
                String[] keywordArr = keywordWithSeparator.split("\\|");
                for (String keyword : keywordArr) {
                    if (contentFromUser.contains(keyword)) {
                        return wechatKeywordAutoreply;
                    }
                }
            }
        }
        return null;
    }

    public OutMsg getKeywordMsg(WechatKeywordAutoreply wechatKeywordAutoreply, InMsg inMsg) {
        if (wechatKeywordAutoreply == null) {
            return null;
        }
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItemText = wechatKeywordAutoreply.findFirstByTypeAndEnabled(WechatKeywordAutoreplyItem.Type.TEXT.toString(), ENABLED);
        List<WechatKeywordAutoreplyItem> wechatKeywordAutoreplyItemNews = wechatKeywordAutoreply.findByTypeAndEnabled(WechatKeywordAutoreplyItem.Type.NEWS.toString(), ENABLED);
        List<WechatKeywordAutoreplyItem> WechatKeywordAutoreplyItemAll = Lists.newLinkedList();
        if (wechatKeywordAutoreplyItemText != null && StrKit.notBlank(wechatKeywordAutoreplyItemText.getContent())) {
            WechatKeywordAutoreplyItemAll.add(wechatKeywordAutoreplyItemText);
        }
        if (wechatKeywordAutoreplyItemNews.size() > 0) {
            WechatKeywordAutoreplyItemAll.addAll(wechatKeywordAutoreplyItemNews);
        }
        if (WechatKeywordAutoreplyItemAll.size() == 0) {
            return null;
        }
        int ranNum = new Random().nextInt(WechatKeywordAutoreplyItemAll.size());
        WechatKeywordAutoreplyItem wechatKeywordAutoreplyItem = WechatKeywordAutoreplyItemAll.get(ranNum);
        if (WechatKeywordAutoreplyItem.Type.TEXT.toString().equals(wechatKeywordAutoreplyItem.getType())) {
            OutTextMsg outTextMsg = new OutTextMsg(inMsg);
            StringBuilder contentBuilder = new StringBuilder();
            String split = "";
            for (String content : wechatKeywordAutoreplyItem.getContent().split("\\n")) {
                contentBuilder.append(split).append(content);
                split = "\\n";
            }
            outTextMsg.setContent(contentBuilder.toString());
            outTextMsg.setCreateTime((int) (System.currentTimeMillis() / 1000));
            return outTextMsg;
        }
        if (WechatKeywordAutoreplyItem.Type.NEWS.toString().equals(wechatKeywordAutoreplyItem.getType())) {
            List<News> newsList = Lists.newLinkedList();
            News news = new News();
            news.setTitle(wechatKeywordAutoreplyItem.getTitle());
            news.setUrl(wechatKeywordAutoreplyItem.getUrl());
            news.setPicUrl(wechatKeywordAutoreplyItem.getThumbUrl());
            newsList.add(news);
            OutNewsMsg outNewsMsg = new OutNewsMsg(inMsg);
            outNewsMsg.setArticles(newsList);
            outNewsMsg.setCreateTime((int) (System.currentTimeMillis() / 1000));
            return outNewsMsg;
        }
        return null;
    }
}
