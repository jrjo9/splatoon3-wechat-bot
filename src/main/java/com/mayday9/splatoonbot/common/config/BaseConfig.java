package com.mayday9.splatoonbot.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayday9.splatoonbot.common.db.MyStringToEnumConverterFactory;
import com.mayday9.splatoonbot.common.json.JsonSerializerModule;
import com.mayday9.splatoonbot.common.util.DateTimeUtils;
import com.mayday9.splatoonbot.common.util.JsonUtils;
import com.mayday9.splatoonbot.common.web.context.Messages;
import com.mayday9.splatoonbot.common.web.response.GlobalResponseBodyAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

//站点配置类
@EnableCaching
@Configuration
@Import({DruidConfig.class, QianfanConfig.class})
public class BaseConfig implements WebMvcConfigurer {


    //tt
    // *忽略请求URL的大小写，默认大小写敏感
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    //tst
    //*json序列化 反序列化的自定义转换器，影响controller的输入和输出
    @ConditionalOnMissingBean
    @Bean
    public JsonSerializerModule jsonSerializerModu1e() {
        return new JsonSerializerModule();
    }

    //*统一输出
    @ConditionalOnMissingBean
    @Bean
    public GlobalResponseBodyAdvice globalResponseBodyAdvice() {
        return new GlobalResponseBodyAdvice();
    }

    //类型转换
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter -> converter instanceof StringHttpMessageConverter);
        //api的返回结果包装了apiresult，返回string，会影响类型转换异常，删除StringHttpMessageConverter，默认返回json
    }

    @ConditionalOnMissingBean
    @Bean
    ObjectMapper objectMapper(JsonSerializerModule jsonSerializerModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        //应用自定义module
        objectMapper.registerModule(jsonSerializerModule);
        //忽略未定义的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //修改jsonutils的objectmapper，应用自定义序列化/反序列化方法
        JsonUtils.setObjectMapper(objectMapper);
        return objectMapper;
    }

    //静态资源文件
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("favicon.ico").addResourceLocations("classpath:/");
//        registry.addResourceHandler("swagger-ui.htm1").addResourceLocations("classpath: /META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath: /META-INF/resources/webjars/");

        registry.addResourceHandler("favicon.ico").addResourceLocations("classpath:/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath: /META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    //http请求工具 restTemplate
    @ConditionalOnMissingBean
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(3 * 60 * 1000);
        requestFactory.setConnectTimeout(3 * 60 * 1000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ObjectMapper objectMapper = new ObjectMapper();
        //]忽略未定义的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        messageConverters.removeIf(converter -> converter instanceof StringHttpMessageConverter);
        messageConverters.add(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpMessageConverter<?> jsonConverter = messageConverters.stream()
            .filter(p -> p instanceof MappingJackson2HttpMessageConverter).findFirst().orElse(null);
        if (jsonConverter != null) {
            ((MappingJackson2HttpMessageConverter) jsonConverter).setObjectMapper(objectMapper);
        }
        return restTemplate;
    }

    //初始化静态配置项
    @PostConstruct
    public void initConfig() {
        // 启用无头模式
        System.setProperty("java.awt.headless", "true");
    }

    //controller接收LocaIDateTime参数,需配合@Reques tParam使用
    @ConditionalOnMissingBean
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        //return DateTimeUtils:: toL oca lDateTime;不能用lambda表込式,注册时认不出乏型美型
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return DateTimeUtils.toLocalDateTime(source);
            }
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return DateTimeUtils.toLocalDate(source);
            }
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String source) {
                return DateTimeUtils.toLocalTime(source);
            }
        };
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new MyStringToEnumConverterFactory());
    }

    @Bean
    Messages messages() throws IOException {
        Resource[] messageResources = new PathMatchingResourcePatternResolver().getResources("classpath*:messages/*.properties");
        Messages messages = new Messages();
        String[] baseNames = new String[messageResources.length];
        for (int i = 0, messageResourcesLength = messageResources.length; i < messageResourcesLength; i++) {
            Resource messageResource = messageResources[i];
            String filename = messageResource.getFilename();
            baseNames[i] = "messages/" + filename.substring(0, filename.indexOf('.'));
        }
        messages.setBasenames(baseNames);
        return messages;
    }
}
