package com.jfeat.identity.authc;

import com.jfinal.kit.StrKit;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 后台用户登录后，保存登录subject，当权限更新的时候，强制退出这些用户
 * @author jackyhuang
 * @date 2018/11/28
 */
public class LoginUserStore {

    private static final Logger logger = LoggerFactory.getLogger(LoginUserStore.class);

    public static LoginUserStore me = new LoginUserStore();

    public static LoginUserStore me() {
        return me;
    }

    private LoginUserStore() {

    }

    /**
     * userId, sessionId list
     */
    private Map<Integer, List<String>> userSessionMap = new ConcurrentHashMap<>();

    /**
     * sessionId, userId
     */
    private Map<String, Integer> sessionUserMap = new ConcurrentHashMap<>();

    /**
     * sessionId, Subject
     */
    private Map<String, Subject> sessionSubjectMap = new ConcurrentHashMap<>();

    public Map<Integer, List<String>> getUserSessionMap() {
        return userSessionMap;
    }

    /**
     * 登录的时候记录 登录用户和session.
     * @param subject
     * @param sessionId
     */
    public synchronized void store(Subject subject, String sessionId) {
        PrincipalCollection principalCollection = subject.getPrincipals();
        ShiroUser shiroUser = (ShiroUser) principalCollection.iterator().next();
        Integer userId = shiroUser.id;
        List<String> sessionIds = userSessionMap.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        sessionIds.add(sessionId);
        userSessionMap.putIfAbsent(userId, sessionIds);
        sessionUserMap.put(sessionId, userId);
        sessionSubjectMap.put(sessionId, subject);
        logger.debug("storing userId {}, sessionId {}. userSessionIdCount = {}, totalSessionSubjectCount = {}, totalSessionUserCount = {}",
                shiroUser.id, sessionId, sessionIds.size(), sessionSubjectMap.size(), sessionUserMap.size());
    }

    /**
     * 强制退出登录用户
     * @param userId
     */
    public synchronized void forceLogout(Integer userId) {
        List<String> sessionIds = userSessionMap.get(userId);
        if (sessionIds != null && !sessionIds.isEmpty()) {
            List<Subject> subjects = new ArrayList<>();
            List<String> toRemoveSessions = new ArrayList<>();
            for (String sessionId : sessionIds) {
                Subject subject = sessionSubjectMap.get(sessionId);
                if (subject != null) {
                    subjects.add(subject);
                    logger.debug("force logout userId {}, sessionId {}", userId, sessionId);
                }
                else {
                    sessionUserMap.remove(sessionId);
                    sessionSubjectMap.remove(sessionId);
                    toRemoveSessions.add(sessionId);
                }
            }
            sessionIds.removeAll(toRemoveSessions);
            for (Subject subject : subjects) {
                subject.logout();
            }
        }
    }

    public synchronized void clear(String sessionId) {
        if (StrKit.isBlank(sessionId)) {
            return;
        }
        int userSessionIdCount = 0;
        Integer userId = sessionUserMap.get(sessionId);
        if (userId != null) {
            List<String> sessionIds = userSessionMap.get(userId);
            sessionIds.remove(sessionId);
            userSessionIdCount = sessionIds.size();
        }
        sessionSubjectMap.remove(sessionId);
        sessionUserMap.remove(sessionId);
        logger.debug("clearing sessionId {}. userSessionIdCount = {}, totalSessionSubjectCount = {}, totalSessionUserCount = {}",
                sessionId, userSessionIdCount, sessionSubjectMap.size(), sessionUserMap.size());

    }
}
