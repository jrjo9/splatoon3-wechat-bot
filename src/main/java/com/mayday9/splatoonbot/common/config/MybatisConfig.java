package com.mayday9.splatoonbot.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lianjiannan
 * @since 2024/9/19 18:48
 **/
@Configuration
public class MybatisConfig {

//    @Primary
//    @Bean(name = "sysSqlSessionFactory")
//    public SqlSessionFactory sysSqlSessionFactory(@Qualifier("sysDataSource") DataSource dataSource)
//        throws Exception {
//        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        return bean.getObject();
//    }
}
