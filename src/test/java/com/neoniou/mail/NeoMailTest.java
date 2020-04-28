package com.neoniou.mail;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.neoniou.com.mail.MailSmtp;
import com.neoniou.com.mail.NeoMail;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NeoMailTest {

    private String username;
    private String password;

    /**
     * 可以通过 properties文件读取配置
     */
    @Before
    public void readConfig() {
        Properties props = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties");
        try {
            props.load(is);
            username = props.getProperty("username");
            password = props.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送简单文本邮件
     */
    @Test
    public void sendTextTest() {
        NeoMail neoMail = new NeoMail();
        try {
            neoMail.config("smtp.qq.com", 25, username, password)
                    .subject("这是一封测试简单文本邮件")
                    .from("Neo")
                    .to("me@neow.cc")
                    .text("这是内容")
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送简单 Html邮件
     */
    @Test
    public void sendHtmlTest() {
        NeoMail neoMail = new NeoMail();
        try {
            neoMail.config(MailSmtp.QQ, username, password)
                    .subject("这是一封测试简单Html邮件")
                    .from("Neo")
                    .to("me@neow.cc")
                    .html("<h1>标题</h1><div style=\"color: blue\">内容</div>")
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送 Pebble模板的 Html邮件
     */
    @Test
    public void sendPebbleTest() {
        NeoMail neoMail = new NeoMail();
        try {
            //使用 Pebble
            PebbleEngine engine = new PebbleEngine.Builder().build();
            PebbleTemplate compiledTemplate = engine.getTemplate("test.html");

            Map<String, Object> context = new HashMap<String, Object>();
            context.put("number", "1234");

            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);

            String html = writer.toString();

            //发送邮件
            neoMail.config(MailSmtp.QQ, username, password)
                    .subject("这是一封测试Pebble邮件")
                    .from("Neo")
                    .to("me@neow.cc")
                    .html(html)
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发邮件，包括抄送和密送
     */
    @Test
    public void sendCcAndBccTest() {
        NeoMail neoMail = new NeoMail();
        try {
            neoMail.config(MailSmtp.QQ, username, password)
                    .subject("这是一封测试抄送密送的邮件")
                    .from("Neo")
                    .to("mail1", "mail2")
                    .cc("mail3", "mail4")
                    .bcc("mail5", "mail6")
                    .text("这是内容")
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送带附件的邮件
     */
    @Test
    public void sendAttachMail() {
        NeoMail neoMail = new NeoMail();
        try {
            neoMail.config(MailSmtp.QQ, username, password)
                    .subject("这是一封测试附件的邮件")
                    .from("Neo")
                    .to("me@neow.cc")
                    .text("这是内容")
                    .attach("E:\\3.zip")
                    .send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
