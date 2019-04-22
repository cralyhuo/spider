package cn.shaines.spider.module;

import java.util.Map;

/**
 * @author houyu
 * @createTime 2019/4/19 23:02
 */
public interface SpiderInterface {

    /**
     * 爬虫返回的数据
     * @return
     */
    Object process(Map<String, Object> param);

}
