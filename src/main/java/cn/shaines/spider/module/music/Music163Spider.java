package cn.shaines.spider.module.music;

import cn.shaines.spider.util.DbUtil;
import cn.shaines.spider.util.HttpURLConnectionUtil;
import cn.shaines.spider.util.ProxyPoolUtil;
import cn.shaines.spider.util.ProxyPoolUtil.CaptureProxyImpl1;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
        Map<String, Object> aesEncrypt = AESEncrypt.getParamMap(rid, pageIndex, 100);
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
    public static void main(String[] args) throws Exception {
        ProxyPoolUtil proxyPoolUtil = ProxyPoolUtil.get();
        proxyPoolUtil.addCaptureProxy(0, new CaptureProxyImpl1());
        proxyPoolUtil.run();
        Thread.sleep(10000);
        // 阻塞队列固定大小
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(213);
        // 线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 213, 1, TimeUnit.MINUTES, queue);
        for (int i = 1; i < 213; i++) {
            executor.submit(new MyRun(i));
        }
        executor.shutdown();
        proxyPoolUtil.close();
        System.out.println("music163Spider.errorPage = " + Music163Spider.errorPage);

        //
        // Music163Spider music163Spider = new Music163Spider();
        // music163Spider.process(550);
        //
        // handleWord();

    }

    public static void handleWord() throws Exception {
        // File sourceFile = new File("C:\\Users\\houyu\\Desktop\\source.txt");
        // List<Map<String, Object>> list = DbUtil.executeQuery("SELECT `评论内容` FROM `网易云音乐歌曲评论表`");
        // StringBuilder builder = new StringBuilder(2048 * 1024);
        // for(Map<String, Object> map : list) {
        //     builder.append(map.get("评论内容")).append("\r\n");
        // }
        // InputStream inputStream = IOUtil.toInputStream(builder.toString().getBytes("UTF-8"));
        // IOUtil.toFile(inputStream, sourceFile);
        // 分词
        // WordSegmenter.segWithStopWords(new File("C:\\Users\\houyu\\Desktop\\source.txt"),
        //                                new File("C:\\Users\\houyu\\Desktop\\target.txt"));
        // 插入数据库
        // String content = IOUtil.toString(new FileInputStream(new File("C:\\Users\\houyu\\Desktop\\target.txt")), "UTF-8");
        // String[] split = content.split(" ");
        // List<Map<String, Object>> list = new ArrayList<>(2048);
        // for(String s : split) {
        //     s = s.trim();
        //     if(s.isEmpty()) {
        //         continue;
        //     }
        //     Map<String, Object> map = new HashMap<>(1);
        //     map.put("text", s);
        //     list.add(map);
        //     if(list.size() >= 2048) {
        //         DbUtil.insertData("网易云音乐评论词", list);
        //         list.clear();
        //     }
        // }
        // DbUtil.insertData("网易云音乐评论词", list);

        // SELECT text, count(text) AS textCount FROM 网易云音乐评论词 GROUP BY text ORDER BY textCount DESC LIMIT 150

    }

    static class MyRun implements Runnable {

        private int pageIndex;

        public MyRun(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        @Override
        public void run() {
            logger.info("处理页数:" + pageIndex);
            Music163Spider music163Spider = new Music163Spider();
            music163Spider.process(pageIndex);
            logger.info(">> 完成页数:" + pageIndex);
        }
    }

}
