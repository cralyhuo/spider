package cn.shaines.spider.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @program: loading-blog
 * @description: 发送邮件工具类
 * @author: houyu
 * @create: 2018-12-10 22:51
 */
public class MailUtil {

    private static Properties PROPERTIES;                                              // 配置参数
    private static InternetAddress FROM_INTERNETADDRESS;                               // 发送者地址
    private static String USERNAME = "for.houyu@foxmail.com";                               // 用户名
    private static String PASSWORD = "tdqhfshyueftbjgh";                               // 密码
    private static Session SESSION;                                                    // 发送者生成的session

    static {
        PROPERTIES = new Properties();
        PROPERTIES.put("mail.transport.protocol", "smtp");                              // 连接协议
        PROPERTIES.put("mail.smtp.host", "smtp.qq.com");                                // 主机名
        PROPERTIES.put("mail.smtp.port", 465);                                          // 端口号
        PROPERTIES.put("mail.smtp.auth", "true");
        PROPERTIES.put("mail.smtp.ssl.enable", "true");                                 // 设置是否使用ssl安全连接 ---  一般都使用
        // PROPERTIES.put("mail.debug", "true");                                        // 设置是否显示debug信息 true 会在控制台显示相关信息

        try {
            String nick =javax.mail.internet.MimeUtility.encodeText("Shy Site");
            FROM_INTERNETADDRESS = new InternetAddress(nick + " <" + "for.houyu@foxmail.com" + ">");
        } catch (AddressException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 得到回话对象
        SESSION = Session.getInstance(PROPERTIES);
    }

    public static boolean sendMail(Collection<String> toAddress, String title, String data) {
        // 获取邮件对象
        MimeMessage message = new MimeMessage(SESSION);
        // 邮差对象
        Transport transport = null;
        //
        InternetAddress[] addressesArray = new InternetAddress[toAddress.size()];
        Iterator<String> iterator = toAddress.iterator();
        int i = 0;
        try {
            while(iterator.hasNext()) {
                String oneAddress = iterator.next();
                if(oneAddress != null && !oneAddress.isEmpty()) {
                    addressesArray[i++] = new InternetAddress(oneAddress);
                }
            }
            // 设置发件人邮箱地址
            message.setFrom(FROM_INTERNETADDRESS);
            // 设置收件人地址
            message.setRecipients(MimeMessage.RecipientType.TO, addressesArray);
            // 设置邮件标题
            message.setSubject(title);
            // 设置邮件内容
            message.setText(data, null, "html");
            // 得到邮差对象
            transport = SESSION.getTransport();
            // 连接自己的邮箱账户
            transport.connect(USERNAME, PASSWORD);      // 密码为刚才得到的授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            // System.out.println("发送成功");
        } catch(MessagingException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                transport.close();
            } catch(MessagingException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * // 导入javax.mail依赖
     * compile group: 'javax.mail', name: 'mail', version: '1.4.7'
     * 详情浏览博客:https://www.cnblogs.com/xdp-gacl/p/4216311.html
     *
     * @param toAddress : 邮箱
     * @param title     : 标题
     * @param data      : 内容
     */
    public static boolean sendMail(String toAddress, String title, String data) {
        return sendMail(Arrays.asList(toAddress), title, data);
    }

    /**
     * 使用线程异步发送邮件
     * @param toInternetAddress
     * @param title
     * @param data
     */
    public static void asySendMail(String toInternetAddress, String title, String data) {
        ThreadPoolUtil.get().submit(() -> sendMail(toInternetAddress, title, data));
    }

    /**
     * --------------------------------------------------------------------------------------
     */
    private interface SingletonHolder {
        MailUtil INSTANCE = new MailUtil();
    }

    public static MailUtil get() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * --------------------------------------------------------------------------------------
     */

    public static void main(String[] args) throws Exception {
        MailUtil.sendMail(Arrays.asList("272694308@qq.com", "179714467@qq.com"),"来自SHY BLOG的邮件", "<p style='color: red'>你好呀2222!!</p>");
    }
}