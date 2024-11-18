package com.mayday9.splatoonbot.common.config;

import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {

//    @ConfigurationProperties(prefix = "spring.datasource")
//    @Bean
//    public DataSource druid() {
//        return new DruidDataSource();
//    }

    /**
     * 配置一个druid的监控
     * 1、配置一个druid的后台  管理servlet
     * 2、配置一个druid的filter
     */
    //1、配置一个druid的后台  管理servlet
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        //注意，请求时 /druid/*
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> initParm = new HashMap<>();
        //登陆页面账户与密码
        initParm.put(ResourceServlet.PARAM_NAME_USERNAME, "admin");
        initParm.put(ResourceServlet.PARAM_NAME_PASSWORD, "admin123456");
//        //监控后台 允许ip
//        initParm.put(ResourceServlet.PARAM_NAME_ALLOW, "");
//        //黑名单
//        initParm.put(ResourceServlet.PARAM_NAME_DENY, "");

        bean.setInitParameters(initParm);
        return bean;
    }

    @Bean
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean
    public WallConfig wallConfig() {
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);//允许一次执行多条语句
        wallConfig.setNoneBaseStatementAllow(true);//允许一次执行多条语句
        return wallConfig;
    }

    //    2、配置一个druid的filter
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());

        Map<String, String> initPrams = new HashMap<>();
        initPrams.put(WebStatFilter.PARAM_NAME_EXCLUSIONS, "*.js,*.css,/druid/*");
        bean.setInitParameters(initPrams);

        //设置拦截器请求
        bean.addUrlPatterns("/*");
        return bean;
    }

    @PostConstruct
    public void setProperties() {
        System.setProperty("druid.mysql.usePingMethod", "false");
    }

}
