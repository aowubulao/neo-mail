# Neo-Mail

一个学习性质的邮件库。

支持群发，抄送、密送。支持发送Html及Html模板的邮件。

基于Socket套接字编写，简洁、运行速度快。

## 快速开始

**· Maven坐标**

```
<dependency>
    <groupId>com.neoniou</groupId>
    <artifactId>neo-mail</artifactId>
    <version>1.0.0</version>
</dependency>
```

**· 使用实例**

```java
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
        neoMail.config(MailSmtp.QQ, username, password)
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
```



### Pebble中的test.html

```html
<!DOCTYPE html>
<html>
<head>
   <meta charset="UTF-8">
</head>
<body>
   <div>
        <h1>这是一封测试Html的邮件</h1>
        <div style="font-size: 16px; color: gray">
            如果你能顺利地看到数字：<span style="color: #409EFF">{{ number }}</span>
            <br>
            那么表示程序成功运行了
        </div>
   </div>
</body>
</html>
```