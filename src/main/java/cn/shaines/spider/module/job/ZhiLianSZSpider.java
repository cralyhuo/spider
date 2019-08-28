package cn.shaines.spider.module.job;

import cn.shaines.spider.module.SpiderInterface;
import cn.shaines.spider.util.DbUtil;
import cn.shaines.spider.util.HttpURLConnectionUtil;
import cn.shaines.spider.util.PublicUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智联 深圳 爬虫
 * @author houyu
 * @createTime 2019/4/21 20:59
 */
@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class ZhiLianSZSpider implements SpiderInterface {

    // 定义一个标识创建一次表
    private int iscreatetableflag = 0;
    // 定义开始的位置
    private int start = 0;
    // 定义表名
    private String tableName = "智联深圳JAVA招聘信息";


    /**
     * 处理薪资
     * @return
     */
    private Object handlerSalary(){
        List<Map<String, Object>> dataList = null;
        try {
            dataList = DbUtil.executeQuery("SELECT 薪资待遇 FROM " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("智联深圳java招聘信息共: " + dataList.size() + " 条");

        List<String> list = new ArrayList<>(dataList.size());
        dataList.forEach((v) -> list.add((String)v.get("薪资待遇")));

        System.out.println(list);

        List<String> tempList = new ArrayList<>(dataList.size() * 10);

        list.forEach(v -> {
            if (v.contains("-")){
                String[] split = v.split("-");
                double startNum = Double.parseDouble(split[0].replace("K", ""));
                double endNum = Double.parseDouble(split[1].replace("K", ""));
                while (startNum < endNum + 1) {
                    double num = startNum++;
                    String numStr = num + "";
                    if (!numStr.contains(".0")){
                        tempList.add(numStr);
                    }else {
                        tempList.add(numStr.replace(".0", ""));
                    }
                }
            }else {
                tempList.add(v);
            }
        });

        System.out.println(tempList);

        Map<String, Integer> salaryMap = PublicUtil.getAloneCountInList(tempList);

        System.out.println("\r\nsalaryMap = " + salaryMap);

        return null;
    }

    /**
     * 解析网页数据
     * @param param
     * @return
     */
    @Override
    public Object process(Map<String, Object> param) {
        List<Map<String, Object>> dataList = null;

        while (true){
            String html = getHtml();
            JSONObject jsonObject = JSON.parseObject(html);
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("results");

            // 如果数据为空,跳出循环
            if (jsonArray.isEmpty()){
                break;
            }

            Map<String, Object> dataMap;
            dataList = new ArrayList<>(jsonArray.size());

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject data = jsonArray.getJSONObject(i);
                dataMap = new HashMap<>(16);

                String 更新时间 = data.getString("updateDate");
                String 结束时间 = data.getString("endDate");
                String 所属城市 = data.getJSONObject("city").getString("display");
                String 详情网址 = data.getString("positionURL");
                String 福利多多 = PublicUtil.join(data.getJSONArray("welfare").toArray(), ",");
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
                String 工作福利 = PublicUtil.join(jobLight == null ? new String[]{} : jobLight.toArray(), ",");
                dataMap.put("工作福利", 工作福利);

                JSONArray skill = positionLabel.getJSONArray("skill");
                String 掌握技能 = PublicUtil.join(skill == null ? new String[]{} : skill.toArray(), ",");
                dataMap.put("掌握技能", 掌握技能);

                System.out.println("dataMap = " + dataMap);

                if (iscreatetableflag == 0){
                    DbUtil.createTable(tableName, dataMap.keySet().toArray(new String[]{}));
                    iscreatetableflag = 1;
                }
                DbUtil.insertData(tableName, dataMap);

                dataList.add(dataMap);
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return dataList;
    }

    /**
     * 网络请求数据
     * @return
     */
    private String getHtml(){
//      String url = String.format("https://fe-api.zhaopin.com/c/i/sou?start=%d&pageSize=90&cityId=765&kw=JAVA&kt=3", start);
        String url = String.format("https://fe-api.zhaopin.com/c/i/sou?start=%d&pageSize=90&cityId=765&kw=JAVA&kt=3&rt=134542fe834e4a019404bf10c52124b3&_v=0.04244049&x-zp-page-request-id=6d260c9fff0d4668a158babf5ae396e0-1556345376278-699243", start);

        String html = HttpURLConnectionUtil.builder(url).execute().getBodyString();
        // System.out.println(html);
        System.out.println("\r\nstart = " + start + "\r\n");
        start += 90;

        return html;
    }

    /**
     * main
     */
    public static void main(String[] args) {
        ZhiLianSZSpider zhiLianZSSpider = new ZhiLianSZSpider();
        //zhiLianZSSpider.process(null);
        zhiLianZSSpider.handlerSalary();

    }

}
