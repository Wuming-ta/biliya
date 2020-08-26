package com.jfeat.marketing.trial.service;

import com.jfeat.core.BaseService;
import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.model.TrialApplication;
import com.jfinal.kit.Ret;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class TrialService extends BaseService {
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;
    private String uploadDir = "trial";

    public String getUploadDir() {
        return uploadDir;
    }

    public boolean republish(Trial trial) {
        Trial old = Trial.dao.findById(trial.getId());
        trial.setVersion(old.getVersion() + 1);
        trial.setEnabled(Trial.Enabled.YES.getValue());
        return trial.update();
    }

    public Ret switchEnabled(Integer id) {
        Trial trial = Trial.dao.findById(id);
        if (Trial.Enabled.YES.getValue().equals(trial.getEnabled())) {
            trial.setEnabled(Trial.Enabled.NO.getValue());
        } else {
            if (!between(new Date(), trial.getStartTime(), trial.getEndTime())) {
                return failure("marketing.trial.enabled.failure.expired");
            }
            trial.setEnabled(Trial.Enabled.YES.getValue());
        }
        trial.update();
        return success("action.success");
    }

    /**
     * @return testDate 是否在 startDate 和 endDate 之间
     * 约定：startDate或endDate其中一个为null，都认为无限制，返回true
     */
    private boolean between(Date testDate, Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return true;
        }
        return testDate.getTime() >= startDate.getTime() && testDate.getTime() <= endDate.getTime();
    }

    /**
     * 过滤掉过期的试用活动
     */
    private List<Trial> filterExpired(List<Trial> trials) {
        return updateExpired(trials).stream()
                .filter(trial -> trial.getEnabled().equals(Trial.Enabled.YES.getValue()))
                .collect(Collectors.toList());
    }

    public List<Trial> updateExpired(List<Trial> trials) {
        Date now = new Date();
        return trials.stream().peek(trial -> {
            if (!between(now, trial.getStartTime(), trial.getEndTime())) {
                trial.setEnabled(Trial.Enabled.NO.getValue());
                trial.update();
            }
        }).collect(Collectors.toList());
    }

    /**
     * @return 某用户是否已参与过某活动
     */
    public boolean isPartaken(Trial trial, Integer userId) {
        if (userId == null) {
            return false;
        }
        List<TrialApplication> trialApplications = TrialApplication.dao.find(userId, trial.getId(), trial.getVersion());
        Integer count = 0;
        List<String> statuses = Arrays.asList(
                TrialApplication.Status.APPLYING.toString(),
                TrialApplication.Status.AUDITING.toString(),
                TrialApplication.Status.DELIVERING.toString(),
                TrialApplication.Status.DELIVERED.toString()
        );
        for (TrialApplication trialApplication : trialApplications) {
            if (statuses.contains(trialApplication.getStatus())) {
                count++;
            }
        }
        return count > 0;
    }

    /**
     * 某个用户已经参与过的试用活动进行标记
     */
    private List<Trial> filterPartaken(List<Trial> trials, Integer userId) {
        for (Trial trial : trials) {
            boolean partaken = false;
            if (isPartaken(trial, userId)) {
                partaken = true;
            }
            trial.put("partaken", partaken);
        }
        return trials;
    }

    /**
     * 返回某个用户可见到的试用活动列表
     * 注：某个试用活动若满足以下条件之一，则从返回列表中剔除：
     * 1.试用活动的enabled是0（系统定时扫描过期的试用活动（若同时设置了start_time和end_time)，更新此状态）
     * 2.若试用活动同时设置了start_time和end_time，且当前时间不在start_time和end_time之内
     * 3.在“申领申请单表”中，有与该用户相关的"AUDITING或AUDITED记录"，并且"该记录的版本号等于该试用活动的版本号"
     */
    public List<Trial> available(Integer userId) {
        List<Trial> enabledTrials = Trial.dao.findEnabled();
        List<Trial> enabledNotExpiredTrials = filterExpired(enabledTrials);
        List<Trial> result = filterPartaken(enabledNotExpiredTrials, userId);
        return result;
    }

    /**
     * 检查试用活动是否可用
     */
    public boolean checkTrialAvailable(Integer id) {
        Trial trial = Trial.dao.findById(id);
        if (trial == null) {
            return false;
        }
        if (trial.getEnabled() == null || trial.getEnabled() != 1) {
            return false;
        }
        return true;
    }

    private Ret setEnabled(int enabled) {
        MarketingConfig marketingConfig = MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString(),
                MarketingConfig.Type.TRIAL.toString());
        if (marketingConfig == null) {
            return failure("marketingConfig is null.");
        }
        marketingConfig.setEnabled(enabled);
        marketingConfig.update();
        return success();
    }

    public Ret enable() {
        return setEnabled(ENABLED);
    }

    public Ret disable() {
        return setEnabled(DISABLED);
    }

}
