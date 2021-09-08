package com.jfeat.mail.test;

import com.jfeat.ext.plugin.async.AsyncTaskKit;
import com.jfeat.ext.plugin.async.AsyncTaskPlugin;
import com.jfeat.mail.HtmlMailSender;
import com.jfeat.mail.MailSenderInfo;
import com.jfeat.mail.TextMailSender;
import com.jfeat.mail.TextMailTask;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by ehngjen on 4/17/2015.
 */
public class MailTest {

    //@Test
    public void send() {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("se-smtp.ericsson.se");
        mailInfo.setMailServerPort("25");
        mailInfo.setFromAddress("jacky.z.huang@ericsson.com");
        mailInfo.setToAddress("jacky.z.huang@ericsson.com");
        mailInfo.setSubject("mail title.");
        mailInfo.setContent("mail content");

        TextMailSender textMailSender = new TextMailSender();
        textMailSender.send(mailInfo);
    }

    //@Test
    public void sendSsl() {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.qq.com");
        mailInfo.setMailServerPort("465");
        mailInfo.setValidate(true);
        mailInfo.setUserName("316852753@qq.com");
        mailInfo.setPassword("fodtxasackplbiad");//您的邮箱密码
        mailInfo.setFromAddress("316852753@qq.com");
        mailInfo.setToAddress("4279824@qq.com");
        mailInfo.setSubject("mail title.");
        mailInfo.setContent("mail content");

        TextMailSender textMailSender = new TextMailSender();
        textMailSender.send(mailInfo);
        HtmlMailSender htmlMailSender = new HtmlMailSender();
        htmlMailSender.send(mailInfo);
    }

    //@Test
    public void asyncSend() throws InterruptedException {

        AsyncTaskPlugin plugin = new AsyncTaskPlugin();
        plugin.start();

        for (int i = 0; i < 5; i++) {
            TextMailTask task = new TextMailTask();
            task.setHost("se-smtp.ericsson.se");
            task.setPort("25");
            task.setFromAddress("jacky.z.huang@ericsson.com");
            task.setToAddress("jacky.z.huang@ericsson.com");
            task.setSubject("test email");
            task.setContent("test content " + i);
            AsyncTaskKit.submit(task);
            System.out.println("task[" + i + "] submitted.");
            TimeUnit.SECONDS.sleep(1);
        }

        TimeUnit.SECONDS.sleep(30);
    }
}
