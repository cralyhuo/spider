package cn.shaines.spider.module.music;

import cn.shaines.spider.util.DbUtil;
import cn.shaines.spider.util.HttpURLConnectionUtil;
import cn.shaines.spider.util.ProxyPoolUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网易云 评论
 * @author houyu
 * @createTime 2019/4/21 20:59
 */
public class Music163Spider {

    private static Logger logger = LoggerFactory.getLogger(Music163Spider.class);

    /** 定义一个标识创建一次表 */
    private volatile int ifCreateTableFlag = 0;
    /** 定义表名 */
    private String tableName = "网易云音乐歌曲评论表";
    private String rid = "R_SO_4_224086";
    public static List<Integer> errorPage = new ArrayList<>(128);
    // 1054

    public Object process(int pageIndex) {
        String html = getHtml(pageIndex);
        if(html == null) {
            logger.debug("失败无法解析");
            return null;
        }
        // System.out.println("html = " + html);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONArray comments = JSON.parseObject(html).getJSONArray("comments");
        List<Map<String, Object>> insertList = new ArrayList<>(comments.size());
        logger.debug("解析数据, 有{}条数据处理", comments.size());
        for(int i = 0; i < comments.size(); i++) {
            JSONObject comment = (JSONObject) comments.get(i);
            Map<String, Object> dataMap = new HashMap<>(8);
            dataMap.put("歌曲RID",rid);
            JSONObject user = comment.getJSONObject("user");
            dataMap.put("用户ID", user.getString("userId"));
            dataMap.put("用户昵称", user.getString("nickname"));
            dataMap.put("用户头像", user.getString("avatarUrl"));
            dataMap.put("评论ID", comment.getString("commentId"));
            dataMap.put("评论内容", comment.getString("content"));
            dataMap.put("评论时间", simpleDateFormat.format(comment.getDate("time")));
            dataMap.put("是否收藏歌曲", comment.getBoolean("liked"));
            insertList.add(dataMap);
            // System.out.println("dataMap = " + dataMap);
        }

        /*if(ifCreateTableFlag == 0) {
            synchronized(Music163Spider.class){
                if(ifCreateTableFlag == 0) {
                    DbUtil.createTable(tableName, insertList.get(0).keySet().toArray(new String[]{}));
                }
                ifCreateTableFlag = 1;
            }
        }*/

        DbUtil.insertData(tableName, insertList);
        logger.debug("有{}条数据处理完成入库", insertList.size());
        //
        return null;
    }

    /**
     * 网络请求数据
     * @return
     */
    private String getHtml(int pageIndex) {
        Map<String, Object> aesEncrypt = AESEncrypt.getParamMap(rid, pageIndex, 20);
        String proxy = ProxyPoolUtil.get().getProxy();
        String[] host_port = {null, null};
        if(proxy != null) {
            host_port = proxy.split(":");
        }
        logger.debug("{}线程使用的代理是:{}", Thread.currentThread().getName(), Arrays.toString(host_port));
        String bodyString = null;
        try {
            bodyString = HttpURLConnectionUtil.builderPost("https://music.163.com/weapi/v1/resource/comments/R_SO_4_224086?csrf_token=")
                    .setParam(aesEncrypt)//
                    .setProxy(host_port[0], Integer.valueOf(host_port[1] == null ? "1" : host_port[1]))
                    .addHead("referer", "https://music.163.com/")
                    .addHead("origin", "https://music.163.com")
                    // .setCookie("_iuqxldmzr_=32; _ntes_nnid=0628af491720529f9fa051080845203f,1564645585395; _ntes_nuid=0628af491720529f9fa051080845203f; WM_TID=3ljV3hLGiM9AFVAVRVJ4pDHwSSGt%2Bq0k; WM_NI=ew23iADZWLIc%2F%2BdKsUIYunuHYPv7%2BdmRhDhEFJQ7tiZ8yEv4ehdUQ9SGN8z1s53vjdqjLSyE6xuX5m%2BwHF3VAezSS1yZJvk%2B4ag6D%2FmUHd5mRHP2jP5KIMd5Ysaeae5LOWY%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eeb3c754b6aa8ad2c448b6ac8bb6d84f878f9faff3408d9fb8b1b37ea896f8d3bc2af0fea7c3b92aa7a9fed4ec4b888699adcc69edf5acacf25f8b87bb8ff8449b90bad8aa70af8ea8a5eb4f86969fd8f7639ae8b7a6f540a7b8acb6d433919982d9f93bb1ebfd97e970b3b684b4fc4ff6a8bbb9b867e9a988a7b25e83bbb6b4d23bbb868697ea21f8bfa9d7c725af8b8885b8679298a0d3e95cf79587aee73f9badaf86e46ff79481b8dc37e2a3; playerid=62165561; JSESSIONID-WYYY=Fn5BOWTgCPMbJKAgUlOSkV6gMJKz4%2BfZnM4zz5J23F5Rru3WrmGUCwB2m8Naoik9nw7%5Cz%2BY6OznRZerQoEp0%5CHUeq1GTJesZDJFNZ4vnaP%2BOYunTu6uGNpeBowdIjfdGKgGqg7fARi%5CMAixOQeQbnSSPtoWRVEehlDdgdeN6iPkdOQWt%3A1566732683758")
                    .execute()//
                    .getBodyString();
        } catch(Exception e) {
            // e.printStackTrace();
            logger.warn("失败分页:{}", pageIndex);
            errorPage.add(pageIndex);
            return null;
        }
        return bodyString;
    }

    /**
     * main
     */
    public static void main(String[] args) {
        // ProxyPoolUtil proxyPoolUtil = ProxyPoolUtil.get();
        // proxyPoolUtil.addCaptureProxy(0, new CaptureProxyImpl1());
        // proxyPoolUtil.run();
        // // 阻塞队列固定大小
        // BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(2422);
        // // 线程池
        // ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 2422, 1, TimeUnit.HOURS, queue);
        // for (int i = 1; i < 1054; i++) {
        //     executor.submit(new MyRun(i));
        // }
        // executor.shutdown();
        Music163Spider music163Spider = new Music163Spider();
        music163Spider.process(550);
        // System.out.println("music163Spider.errorPage = " + Music163Spider.errorPage);

    }

    static class MyRun implements Runnable {

        private int pageIndex;

        public MyRun(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        @Override
        public void run() {
            System.out.println("处理页数:" + pageIndex);
            Music163Spider music163Spider = new Music163Spider();
            music163Spider.process(pageIndex);
            System.err.println(">> 完成页数:" + pageIndex);
        }
    }

}
