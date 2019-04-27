package cn.shaines.spider.module.job;

import cn.shaines.spider.module.SpiderInterface;
import cn.shaines.spider.util.EasyUtil;
import cn.shaines.spider.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智联 中山 爬虫
 *
 * @author houyu
 * @createTime 2019/4/21 20:59
 */
@SuppressWarnings({"AlibabaAvoidCommentBehindStatement", "Duplicates"})
public class ZhiLianZSSpider implements SpiderInterface {

    // 自己封装的一个网络请求工具类,
    HttpUtil httpUtil = HttpUtil.get();
    // 自己封装的一个公用工具类
    EasyUtil easyUtil = EasyUtil.get();

    /**
     * 处理薪资词云图
     *
     * @return
     */
    private Object handlerSalary() {
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) process(null);

        System.out.println("智联中山java招聘信息共: " + dataList.size() + " 条");

        List<String> list = new ArrayList<>(dataList.size());
        dataList.forEach((v) -> list.add((String) v.get("薪资待遇")));
        Map<String, Integer> salaryMap = easyUtil.getAloneCountInList(list);

        List<String> tempList = new ArrayList<>(dataList.size() * 4);
        salaryMap.forEach((k, v) -> {
            if (k.contains("-")) {
                String[] split = k.split("-");
                String startStr = split[0].replace("K", "");
                String endStr = split[1].replace("K", "");
                int startNum = Integer.parseInt(startStr);
                int endNum = Integer.parseInt(endStr);
                while (startNum < endNum + 1) {
                    int num = startNum++;
                    for (int i = 0; i < v; i++) {
                        tempList.add(num + "");
                    }
                }
            }else {
                for (int i = 0; i < v; i++) {
                    tempList.add(k + "");
                }
            }
        });
        System.out.println(tempList);
        salaryMap = easyUtil.getAloneCountInList(tempList);
        System.out.println("salaryMap = " + salaryMap);
        return null;
    }

    /**
     * 解析网页数据
     *
     * @param param
     * @return
     */
    @Override
    public Object process(Map<String, Object> param) {
        String html = getHtml(1);
        JSONObject jsonObject = JSON.parseObject(html);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("results");

        Map<String, Object> dataMap = null;
        List<Map<String, Object>> dataList = new ArrayList<>(jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            dataMap = new HashMap<>(16);

            String 更新时间 = data.getString("updateDate");
            String 结束时间 = data.getString("endDate");
            String 所属城市 = data.getJSONObject("city").getString("display");
            String 详情网址 = data.getString("positionURL");
            String 福利多多 = easyUtil.join(data.getJSONArray("welfare").toArray(), ",");
            String 薪资待遇 = data.getString("salary");
            String 工作年限 = data.getJSONObject("workingExp").getString("name");
            dataMap.put("更新时间", 更新时间);
            dataMap.put("结束时间", 结束时间);
            dataMap.put("所属城市", 所属城市);
            dataMap.put("详情网址", 详情网址);
            dataMap.put("福利多多", 福利多多);
            dataMap.put("薪资待遇", 薪资待遇);
            dataMap.put("工作年限", 工作年限);

            String 公司代码 = data.getJSONObject("company").getString("number");
            String 公司规模 = data.getJSONObject("company").getJSONObject("size").getString("name");
            String 公司名称 = data.getJSONObject("company").getString("name");
            String 公司类型 = data.getJSONObject("company").getJSONObject("type").getString("name");
            String 招聘网址 = data.getJSONObject("company").getString("url");
            dataMap.put("公司代码", 公司代码);
            dataMap.put("公司规模", 公司规模);
            dataMap.put("公司名称", 公司名称);
            dataMap.put("公司类型", 公司类型);
            dataMap.put("招聘网址", 招聘网址);

            String 岗位简介 = data.getJSONObject("jobType").getString("display");
            String 招聘人数 = data.getString("recruitCount");
            String 创建时间 = data.getString("createDate");
            String 岗位名称 = data.getString("jobName");
            String 学历底线 = data.getJSONObject("eduLevel").getString("name");
            String 工作制度 = data.getString("emplType");
            String 区域名称 = data.getString("businessArea");
            dataMap.put("岗位简介", 岗位简介);
            dataMap.put("招聘人数", 招聘人数);
            dataMap.put("创建时间", 创建时间);
            dataMap.put("岗位名称", 岗位名称);
            dataMap.put("学历底线", 学历底线);
            dataMap.put("工作制度", 工作制度);
            dataMap.put("区域名称", 区域名称);

            JSONObject positionLabel = JSON.parseObject(data.getString("positionLabel"));
            JSONArray jobLight = positionLabel.getJSONArray("jobLight");
            String 工作福利 = easyUtil.join(jobLight == null ? new String[]{} : jobLight.toArray(), ",");
            dataMap.put("工作福利", 工作福利);

            JSONArray skill = positionLabel.getJSONArray("skill");
            String 掌握技能 = easyUtil.join(skill == null ? new String[]{} : skill.toArray(), ",");
            dataMap.put("掌握技能", 掌握技能);

            System.out.println("dataMap = " + dataMap);
            dataList.add(dataMap);
        }
        return dataList;
    }

    /**
     * 网络请求数据
     *
     * @param page
     * @return
     */
    private String getHtml(int page) {
        String url = "https://fe-api.zhaopin.com/c/i/sou?pageSize=1000&cityId=780&workExperience=-1&education=-1&companyType=-1&employmentType=-1&jobWelfareTag=-1&kw=java&kt=3";
        String html = httpUtil.getHtml(url);
        // System.out.println(html);
        return html;
    }

    /**
     * main
     */
    public static void main(String[] args) {
        ZhiLianZSSpider zhiLianZSSpider = new ZhiLianZSSpider();
        //zhiLianZSSpider.process(null);
        zhiLianZSSpider.handlerSalary();
    }

}
