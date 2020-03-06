package com.neoniou.com.mail;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * A java mail library based on socket connection
 *
 * @author Neo.Zzj
 */
public class NeoMail {

    private static Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter writer;

    private String username;

    private String text = null;
    private String html = null;

    private String attachment = null;
    private String fileName = null;

    private String from;
    private String[] tos;
    private String[] ccs = null;
    private String[] bccs = null;
    private String subject;

    /**
     * Constructor
     */
    public NeoMail() {
    }

    /**
     * Set mail's subject
     *
     * @param subject mail subject
     */
    public NeoMail subject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Set mail from
     *
     * @param from from mail
     */
    public NeoMail from(String from) {
        this.from = from;
        return this;
    }

    /**
     * Set mail to
     *
     * @param tos mail recipients
     */
    public NeoMail to(String... tos) {
        this.tos = tos;
        return this;
    }

    /**
     * Set cc
     *
     * @param ccs cc
     */
    public NeoMail cc(String... ccs) {
        this.ccs = ccs;
        return this;
    }

    /**
     * Set Bcc
     *
     * @param bccs Bcc
     */
    public NeoMail bcc(String... bccs) {
        this.bccs = bccs;
        return this;
    }

    /**
     * Set text mail
     *
     * @param text text content
     */
    public NeoMail text(String text) {
        this.text = text;
        this.html = null;
        return this;
    }

    /**
     * Set html mail
     *
     * @param html html content
     */
    public NeoMail html(String html) {
        this.html = html;
        this.text = null;
        return this;
    }

    /**
     * Set mail attachment
     *
     * @param filePath file path
     * @throws IOException IOException
     */
    public NeoMail attach(String filePath) throws IOException {
        File file = new File(filePath);
        fileName = file.getName();

        // Read file and encode to Base64
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        int read = fileInputStream.read(bytes);
        fileInputStream.close();
        attachment = Base64Util.encode(Arrays.toString(bytes));

        return this;
    }

    /**
     * Config
     *
     * @param mailSmtp mail server smtp address and port
     * @param username mail address
     * @param password mail password
     * @throws IOException IOException
     */
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
        bufferedReader.readLine();
        writer.println(Base64Util.encode(username));
        bufferedReader.readLine();
        writer.println(Base64Util.encode(password));
        bufferedReader.readLine();

        writer.println("MAIL FROM: <" + username + ">");
        bufferedReader.readLine();

        return this;
    }

    /**
     * Send mail
     *
     * @throws IOException IOException
     */
    public void send() throws IOException {
        if (tos == null) {
            throw new NullPointerException("Please set the recipients!");
        }

        String to = setRcpt(tos);

        String cc = null;
        if (ccs != null) {
            cc = setRcpt(ccs);
        }

        String bcc = null;
        if (bccs != null) {
            bcc = setRcpt(bccs);
        }

        writer.println("DATA");
        bufferedReader.readLine();

        String content;
        if (text != null && html == null) {
            writer.println("Content-Type: text/plain; charset=\"UTF-8\"");
            content = Base64Util.encode(text);
        } else if (text == null && html != null) {
            writer.println("Content-Type: text/html; charset=\"UTF-8\"");
            content = Base64Util.encode(html);
        } else {
            throw new NullPointerException("Please set the content of e-mail!");
        }

        writer.println("Content-Transfer-Encoding: base64");
        String fromMessage = "FROM: " + (from == null ? "neo-mail" : from) + "<" + username + ">";
        writer.println(fromMessage);
        System.out.println(fromMessage);
        writer.println("TO: " + to);
        if (ccs != null) {
            writer.println("Cc: " + cc);
        }
        if (bccs != null) {
            writer.println("Bcc: " + bcc);
        }
        if (subject != null) {
            writer.println("SUBJECT: " + subject);
        }
        writer.println();
        writer.println(content);
        writer.println(".");
        System.out.println(bufferedReader.readLine());
        writer.println("QUIT");
        bufferedReader.readLine();
        socket.close();
    }

    private String setRcpt(String[] rcpt) throws IOException {
        StringBuilder mails = new StringBuilder();
        for (int i = 0; i < rcpt.length; i++) {
            String rcptMessage = "RCPT TO: <" + rcpt[i] + ">";
            writer.println(rcptMessage);
            bufferedReader.readLine();
            System.out.println(rcptMessage);
            mails.append(i >= 1 ? ", " + rcpt[i] : rcpt[i]);
        }
        return mails.toString();
    }
}
