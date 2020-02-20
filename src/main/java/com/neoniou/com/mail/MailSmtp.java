package com.neoniou.com.mail;

/**
 * @author Neo.Zzj
 */
public enum MailSmtp {

    /**
     * smtp and port
     */
    QQ("smtp.qq.com", 25),
    MAIL_126("smtp.126.com", 25),
    MAIL_139("smtp.139.com", 25),
    MAIL_163("smtp.163.com", 25),
    HOTMAIL("smtp.live.com", 587),
    FOXMAIL("smtp.foxmail.com", 25),
    SINA("smtp.sina.com.cn", 25),
    GMAIL("smtp.gmail.com", 587),
    ;

    private String smtp;
    private int port;

    MailSmtp() {
    }

    MailSmtp(String smtp, int port) {
        this.smtp = smtp;
        this.port = port;
    }

    public String getSmtp() {
        return smtp;
    }

    public int getPort() {
        return port;
    }
}
