package cn.shaines.spider.module;

import cn.shaines.spider.util.ProxyPoolUtil;
import cn.shaines.spider.util.ProxyPoolUtil.CaptureProxyImpl1;

@SuppressWarnings("Duplicates")
public class Test {

    public static void main(String[] args) {
        ProxyPoolUtil proxyPoolUtil = ProxyPoolUtil.get();
        proxyPoolUtil.addCaptureProxy(0, new CaptureProxyImpl1());
        proxyPoolUtil.run();
        //
        String proxy = proxyPoolUtil.getProxy();
        System.out.println("proxy = " + proxy);

    }

}
