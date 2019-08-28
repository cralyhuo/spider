package cn.shaines.spider.test;

import cn.shaines.spider.util.ThreadPoolUtil;

/**
 * @author houyu
 * @createTime 2019/4/16 16:09
 */
public class Test1 {

    public static void main(String[] args) throws Exception {
        /*Map<String, Object> map = new HashMap<>(2);
        map.put("params", "Vj3R3O0/bYDtbld4ukYuMD6NKlgl/hnDJ/kQX8U6uOohtB06pLZEq7iIFKWPIXMjMIJuoqiRKAh+Qq8hjRZjzkBNG6+KTBA6maf3f3e53lq0ocfA147HDPkPzdDymQBUXxsczgnCEz7SOdc5nnn1Evaa4iLX1/HNk/IByCgcYi+BVpNI37C6NX7Lymj5Fyfy");
        map.put("encSecKey", "de26aaf3212a7fb6c25cd6c67347baa8673d9a919b06fc7fab4011c738cb227c3fa5387db7d74676aa113278f02ba08f25292ebeeb0073a05f752cb15b8f967b2f3a606f77268e4f8d1428c3f5c25de71e9252e705d9850daf32bfd34cde18e1b7dc07717a6d8fa2e682be061b094c8937f4d31ebf5ae7f468a0fa034c13f941");
        String bodyString = HttpURLConnectionUtil.builderPost("https://music.163.com/weapi/v1/resource/comments/R_SO_4_1379057027?csrf_token=")
                .setParam(map)//
                .execute()//
                .getBodyString();
        System.out.println("bodyString = " + bodyString);*/
        //
        /*Map<String, Object> aesEncrypt = AESEncrypt.getParamMap("R_SO_4_254574", 1, 100);
        String bodyString = HttpURLConnectionUtil.builderPost("https://music.163.com/weapi/v1/resource/comments/R_SO_4_254574?csrf_token=")
                .setParam(aesEncrypt)//
                .execute()//
                .getBodyString();
        System.out.println("bodyString = " + bodyString);*/

        // WordSegmenter.segWithStopWords(new File("C:\\Users\\houyu\\Desktop\\a.txt"), new File("C:\\Users\\houyu\\Desktop\\b.txt"));
        System.out.println("====================");
        ThreadPoolUtil.get().submit(() -> {
            try {
                System.out.println("开始1");
                Thread.sleep(6000);
                System.out.println("结束1");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("====================");
        ThreadPoolUtil.get().submit(() -> {
            try {
                System.out.println("开始2");
                Thread.sleep(6000);
                System.out.println("结束2");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("====================");
        ThreadPoolUtil.get().submit(() -> {
            try {
                System.out.println("开始1");
                Thread.sleep(6000);
                System.out.println("结束1");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("====================");
        ThreadPoolUtil.get().submit(() -> {
            try {
                System.out.println("开始2");
                System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
                Thread.sleep(6000);
                System.out.println("结束2");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("====================");
        ThreadPoolUtil.get().submit(() -> {
            try {
                System.out.println("开始1");
                Thread.sleep(6000);
                System.out.println("结束1");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("====================");
        ThreadPoolUtil.get().submit(() -> {
            try {
                System.out.println("开始2");
                Thread.sleep(6000);
                System.out.println("结束2");
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    /*
    params: oD0QSQlCxxRCPinJnla/yyq3xwEo7H1ro9mWAWsHZTPi+pIoYtSab25wvybe04weHM2qnwnfAZqc8eeHqVwYd95yp3928e9puVjzHSSJKDtd/+OywUyaW6rTx4yLstEmv+He3raGGC5SAVScV27f+sNV3gZPccqIFrGU4kYzTyVGOTN8iN5DAXTazr6mugzT
encSecKey: a6c93df163a2a407f6300e615bf108a666b23bd41eac8d32ac5394d623da52232c304999eb0c246dc699e3bfaa41fe4ab968868e39f64d54f6ee82ac9af3427b89c46b8baea095071ecd51c43f7b00fed07dc9c9378c39d2850fad0ec6c7f35b8aa7fd17dfa8e6baf46854db0bc55307e99b4f888682cf76c10323ca9b64f9f7
     */

}
