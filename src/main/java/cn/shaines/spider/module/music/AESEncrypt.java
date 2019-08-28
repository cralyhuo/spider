package cn.shaines.spider.module.music;

import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.util.Base64Utils;

/**
 * @description AESEncrypt加密工具
 * @date 2019-08-24 23:51:19
 * @author houyu for.houyu@foxmail.com
 */
public class AESEncrypt {

    /**
     * @description 对应网易云音乐的方法b
     * @date 2019-08-25 00:05:05
     * @author houyu for.houyu@foxmail.com
     */
    public static String encrypt(String content, String sKey) {
        /*
         function b(a, b) {
             var c = CryptoJS.enc.Utf8.parse(b)
               , d = CryptoJS.enc.Utf8.parse("0102030405060708")
               , e = CryptoJS.enc.Utf8.parse(a)
               , f = CryptoJS.AES.encrypt(e, c, {
                         iv: d,
                         mode: CryptoJS.mode.CBC
                     });
             return f.toString()
         }
        // 说明: 前段js没有使用填充模式, 默认使用了PKCS7Padding
        //
        // https://zhidao.baidu.com/question/1819427615658816228.html
        // CryptoJS.enc.Utf8.parse方法才可以将key转为128bit的。好吧，既然说了是多次尝试，那么就不知道原因了，后期再对其进行更深入的研究。
        // 字符串类型的key用之前需要用uft8先parse一下才能用
        //
        // 后端使用的是PKCS5Padding，但是在使用CryptoJS的时候发现根本没有这个偏移，查询后发现PKCS5Padding和PKCS7Padding是一样的东东，使用时默认就是按照PKCS7Padding进行偏移的
        //
        // CryptoJS.AES         >> 算法
        // CBC                  >> 模式
        // 0102030405060708     >> 偏移量
        //
        // 由于CryptoJS生成的密文是一个对象，如果直接将其转为字符串是一个Base64编码过的，在encryptedData.ciphertext上的属性转为字符串才是后端需要的格式。
         */
        try {
            byte[] encryptedBytes;
            byte[] byteContent = content.getBytes("UTF-8");
            // 获取cipher对象，getInstance("算法/工作模式/填充模式")
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // 采用AES方式将密码转化成密钥
            SecretKeySpec secretKeySpec = new SecretKeySpec(sKey.getBytes(), "AES");
            // 初始化偏移量
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes("UTF-8"));
            //cipher对象初始化 init（“加密/解密,密钥，偏移量”）
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            //按照上面定义的方式对数据进行处理。
            encryptedBytes = cipher.doFinal(byteContent);
            return new String(Base64Utils.encode(encryptedBytes), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 以下参数是js debug获取到的 */
    public static final String i = "C3Ba8sQUIHbPHC1Z";
    public static final String e = "";
    public static final String f = "";
    public static final String g = "0CoJUm6Qyw8W8jud";
    public static final String encSecKey = "7c23b7a80684cee814ecdc6252cc66e53a4df5890b1299783e5c575c709e7c8c22d98edc4074fc31b9bf7458e1ab6452c42fde55fcbd9b765f049da3809703686fc86b43ff757a2fa9eb77c0b04a51f02efb3e0ade116454561a6f2aefe89f6d611343383eaf643dce13b4ad1709ea2f8215f922c1a014d7fd79adcd0fa107a4";

    /**
     * @description 对应网易云音乐方法d, 其中参数d f g在外边定义好
     * @date 2019-08-25 00:06:51
     * @author houyu for.houyu@foxmail.com
     */
    public static Map<String, Object> methodD(String pageObject) {
        String encText = encrypt(pageObject, g);
        encText = encrypt(encText, i);// C3Ba8sQUIHbPHC1Z

        // 这里就不必要调用方法c了,因为这里是一个固定值, 直接前段js debug出来即可
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("params", encText);
        paramMap.put("encSecKey", encSecKey);
        return paramMap;
    }

    public static Map<String, Object> getParamMap(String rid, int pageIndex, int pageSize){
        String s = "{\"rid\":\"%s\",\"offset\":\"%s\",\"total\":\"false\",\"limit\":\"%s\",\"csrf_token\":\"\"}";
        s = String.format(s, rid, (pageIndex - 1) * pageSize, pageSize);
        return methodD(s);
    }

}
