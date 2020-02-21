package com.neoniou.com.mail;

import java.io.*;
import java.net.Socket;

/**
 * @author Neo.Zzj
 */
public class NeoMail {

    private static Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter writer;

    private String username;

    private String text = null;
    private String html = null;

    private String from;
    private String[] tos;
    private String[] ccs = null;
    private String[] bccs = null;
    private String subject;

    public NeoMail() {
    }

    public NeoMail subject(String subject) {
        this.subject = subject;
        return this;
    }

    public NeoMail from(String from) {
        this.from = from;
        return this;
    }

    public NeoMail to(String... tos) {
        this.tos = tos;
        return this;
    }

    public NeoMail cc(String... ccs) {
        this.ccs = ccs;
        return this;
    }

    public NeoMail bcc(String... bccs) {
        this.bccs = bccs;
        return this;
    }

    public NeoMail config(MailSmtp mailSmtp, String username, String password) throws IOException {
        this.username = username;

        // Create socket connection
        socket = new Socket(mailSmtp.getSmtp(), mailSmtp.getPort());
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);

        OutputStream outputStream = socket.getOutputStream();
        writer = new PrintWriter(outputStream, true);

        System.out.println(bufferedReader.readLine());

        // Message
        writer.println("HELO mail");
        System.out.println(bufferedReader.readLine());

        writer.println("AUTH LOGIN");
        System.out.println(bufferedReader.readLine());
        writer.println(Base64Util.encode(username));
        System.out.println(bufferedReader.readLine());
        writer.println(Base64Util.encode(password));
        System.out.println(bufferedReader.readLine());

        writer.println("MAIL FROM: <" + username + ">");
        System.out.println(bufferedReader.readLine());

        return this;
    }

    public NeoMail text(String text) {
        this.text = text;
        this.html = null;
        return this;
    }

    public NeoMail html(String html) {
        this.html = html;
        this.text = null;
        return this;
    }

    public void send() throws IOException {
        // to
        String to = setRcpt(tos);
        // cc
        String cc = null;
        if (ccs != null) {
            cc = setRcpt(ccs);
        }
        // bcc
        String bcc = null;
        if (bccs != null) {
            bcc = setRcpt(bccs);
        }
        // main
        writer.println("DATA");
        System.out.println(bufferedReader.readLine());

        String content;
        if (text != null && html == null) {
            writer.println("Content-Type: text/plain; charset=\"UTF-8\"");
            content = Base64Util.encode(text);
        } else if (text == null && html != null) {
            writer.println("Content-Type: text/html; charset=\"UTF-8\"");
            content = Base64Util.encode(html);
        } else {
            throw new IOException();
        }

        writer.println("Content-Transfer-Encoding: base64");
        writer.println("FROM: " + from + "<" + username + ">");
        writer.println("TO: " + to);
        if (ccs != null) {
            writer.println("Cc: " + cc);
        }
        if (bccs != null) {
            writer.println("Bcc: " + bcc);
        }
        writer.println("SUBJECT: " + subject);
        writer.println();
        writer.println(content);
        writer.println(".");

        writer.println("QUIT");
        System.out.println(bufferedReader.readLine());

        socket.close();
    }

    /**
     * set RCPT
     * @param rcpt
     * @return
     * @throws IOException
     */
    private String setRcpt(String[] rcpt) throws IOException {
        StringBuilder mails = new StringBuilder();
        for (int i = 0; i < rcpt.length; i++) {
            writer.println("RCPT TO: <" + rcpt[i] + ">");
            System.out.println(bufferedReader.readLine());
            mails.append(i >= 1 ? ", " + rcpt[i] : rcpt[i]);
        }
        return mails.toString();
    }
}
