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


    public NeoMail config(String smtp, String username, String password) throws IOException {
        this.username = username;

        // Create socket connection
        socket = new Socket(smtp, 25);
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
        StringBuilder to = new StringBuilder();
        for (int i = 0; i < tos.length; i++) {
            writer.println("RCPT TO: <" + tos[i] + ">");
            System.out.println(bufferedReader.readLine());
            to.append(i >= 1 ? ", " + tos[i] : tos[i]);
        }
        // cc
        StringBuilder cc = new StringBuilder();
        for (int i = 0; i < ccs.length; i++) {
            writer.println("RCPT TO: <" + ccs[i] + ">");
            System.out.println(bufferedReader.readLine());
            cc.append(i >= 1 ? ", " + ccs[i] : ccs[i]);
        }
        // bcc
        StringBuilder bcc = new StringBuilder();
        for (int i = 0; i < bccs.length; i++) {
            writer.println("RCPT TO: <" + bccs[i] + ">");
            System.out.println(bufferedReader.readLine());
            bcc.append(i >= 1 ? ", " + bccs[i] : bccs[i]);
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
        if (!"".equals(cc.toString())) {
            writer.println("Cc: " + cc);
        }
        if (!"".equals(bcc.toString())) {
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
}
