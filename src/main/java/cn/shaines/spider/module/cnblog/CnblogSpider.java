package cn.shaines.spider.module.cnblog;

import cn.shaines.spider.util.DbUtil;
import cn.shaines.spider.util.HttpURLConnectionUtil;
import cn.shaines.spider.util.PublicUtil;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("Duplicates")
public class CnblogSpider {

    /**
     * POST请求的链接
     */
    private static final String BASE_URL = "https://www.cnblogs.com/mvc/AggSite/PostList.aspx";
    /**
     * 请求头参数
     */
    private static final Map<String, Object> DEFAULT_HEADER = new HashMap<>(8);
    /**
     * 定义一个标识创建一次表
     */
    private static volatile int isCreateTableFlag = 0;
    /**
     * 定义表名
     */
    private String tableName = "cnblog博客数据";

    /**
     * 解析HTML
     */
    public Object process(int pageIndex) {

        String html = getHtml(pageIndex);
        Document document = Jsoup.parse(html);
        Elements post_items = document.getElementsByClass("post_item");
        post_items.forEach( v -> {
            Element titlelnk = v.selectFirst(".titlelnk");
            // 文章标题
            String title = titlelnk.text();
            // 详情连接
            String href = titlelnk.attr("href");
            // 文章摘要
            String summary = v.selectFirst(".post_item_summary").text();
            // 所属作者
            String lightblue = v.selectFirst(".lightblue").text();
            // 发布时间
            String time = v.selectFirst(".post_item_foot").text();
            time = PublicUtil.subStringBetween(time, "发布于 ", " 评论");
            // 评论人数
            String comment = v.selectFirst(".article_comment > .gray").text();
            comment = comment.replace("评论(", "").replace(")", "");
            // 阅读人数
            String view = v.selectFirst(".article_view > .gray").text();
            view = view.replace("阅读(", "").replace(")", "");

            Map<String, Object> dataMap = new LinkedHashMap<>(8);
            dataMap.put("文章标题", title);
            dataMap.put("详情连接", href);
            dataMap.put("文章摘要", summary);
            dataMap.put("所属作者", lightblue);
            dataMap.put("发布时间", time);
            dataMap.put("评论人数", comment);
            dataMap.put("阅读人数", view);

            //System.out.println(dataMap);

            if (isCreateTableFlag == 0){
                synchronized (CnblogSpider.class){
                    // 确保之创建一次表
                    if (isCreateTableFlag == 0){
                        DbUtil.createTable(tableName, dataMap.keySet().toArray(new String[]{}));
                        isCreateTableFlag = 1;
                    }
                }
            }

            DbUtil.insertData(tableName, dataMap);

        });

        System.out.println("解析完成页数:" + pageIndex);
        return null;
    }

    /**
     * 请求网页源码
     */
    public String getHtml(int pageIndex){
        System.err.println("正在请求页数:" + pageIndex);

        Map<String, Object> body = new HashMap<>(8);
        String JSONStr = "{\"CategoryId\": 808, \"CategoryType\": \"SiteHome\", \"ItemListActionName\": \"PostList\", \"PageIndex\": %d, \"ParentCategoryId\": 0, \"TotalPostCount\": 4000}";
        String html = HttpURLConnectionUtil.builderPost(BASE_URL).setHeader(DEFAULT_HEADER).setJson(JSONStr).execute().getBodyString();
        return html;
    }

    public static void main(String[] args) {
        DEFAULT_HEADER.put("referer", "https://www.cnblogs.com/");
        DEFAULT_HEADER.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        DEFAULT_HEADER.put("origin", "https://www.cnblogs.com");
        DEFAULT_HEADER.put("content-type", "application/json; charset=UTF-8");

        // 阻塞队列固定大小
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(200);
        // 线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.HOURS, queue);
        for (int i = 1; i < 201; i++) {
            executor.execute(new MyRun(i));
        }
        executor.shutdown();

    }
}

class MyRun implements Runnable{

    private int pageIndex;

    public MyRun(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public void run() {
        CnblogSpider cnblogSpider = new CnblogSpider();
        cnblogSpider.process(pageIndex);
    }
}
