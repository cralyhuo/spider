package cn.shaines.spider.test;

import cn.shaines.spider.util.HttpUtil;

/**
 * @author houyu
 * @createTime 2019/4/16 16:09
 */
public class Test1 {

    public static void main(String[] args) {

        System.out.println(HttpUtil.get().getHtml("https://fe-api.zhaopin.com/c/i/sou?pageSize=1190&cityId=780&workExperience=-1&education=-1&companyType=-1&employmentType=-1&jobWelfareTag=-1&kw=java&kt=3&at=ab713a70403e41acac827b11d52a89cb&rt=134542fe834e4a019404bf10c52124b3&_v=0.62558410&userCode=1032416925&x-zp-page-request-id=edbbdee3fc2c44a1a1df219e83179ce3-1555380324407-469085"));


    }
}
