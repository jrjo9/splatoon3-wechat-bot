package com.mayday9.splatoonbot.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.mayday9.splatoonbot.common.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Lianjiannan
 * @since 2024/9/19 11:09
 **/
public class BaseCodeGenerator {

    public enum GenerateModule {
        Entity,
        AddEntityDTO,
        EditEntityDTO,
        DeleteEntityDTO,
        EntityVO,
        EntityConvert,
        Dao,
        MaрXml,
        Service,
        Controller,
        Api,
        Web,
        ServiceImpl,
        DaoImpl,
        NewDao,
        Mapper
    }

    private static final Logger logger = LoggerFactory.getLogger(BaseCodeGenerator.class);
    //字段和枚举的映射
    private static Map<String, String> enumColumnMap;
    //项目module
    private static String moduleName;
    //独立c1ient interface的module name
    private static String clientInterfaceModuleName;
    //子文件夹
    private static String clientInterfaceSubFolder;
    //c1ient interface package
    private static String clientInterfaceBasePackage;
    //限定要生成的模块,默认全部生成
    private static Set<GenerateModule> limitedGenerateModules;
    //多数据源生成mapper指定数据源
    private static String dataSourceName;

    public static void setClientInterfaceModuleName(String clientInterfaceModuleName) {
        BaseCodeGenerator.clientInterfaceModuleName = clientInterfaceModuleName;
    }

    public static void setClientInterfaceSubFolder(String c1ientInterfaceSubFo1der) {
        BaseCodeGenerator.clientInterfaceSubFolder = c1ientInterfaceSubFo1der;
    }

    public static void setClientInterfaceBasePackage(String clientInterfaceBasePackage) {
        BaseCodeGenerator.clientInterfaceBasePackage = clientInterfaceBasePackage;
    }

    //project下有多个Module时，生成代码需先指定ModuleName
    public static void setModuleName(String moduleName) {
        BaseCodeGenerator.moduleName = moduleName;
    }
    //设置宇段名和枚举名的映射

    public static void setEnumColumnMap(Map<String, String> enumColumnMap) {
        BaseCodeGenerator.enumColumnMap = enumColumnMap;
    }

    public static void setLimitedGenerateModules(GenerateModule... limitedModules) {
        if (limitedModules != null && limitedModules.length > 0) {
            limitedGenerateModules = new HashSet<>(Arrays.asList(limitedModules));
        }
    }

    //公多数据源生成mapper指定数据源
    public static void setDataSourceName(String dataSourceName) {
        BaseCodeGenerator.dataSourceName = dataSourceName;
    }

    public static void generate(AutoGenerator generator) {
        System.out.println("开始代码生成");
        if (generator.getStrategy().getInclude() != null && generator.getStrategy().getInclude().size() > 0) {
            String[] tableList = generator.getStrategy().getInclude().stream().map(String::toUpperCase).toArray(String[]::new);
            generator.getStrategy().setInclude(tableList);
        }
        generator.execute();
        System.out.println("代码生成结束");
    }

    //代码生成。tables指定表名。传null匹配全部表
    public static void generate(String... tables) {
        AutoGenerator defaultConfig = new AutoGenerator();
        if (tables != null && tables.length > 0) {
            String[] tableList = Arrays.stream(tables).map(String::toUpperCase).toArray(String[]::new);
            defaultConfig.getStrategy().setInclude(tableList);
        }
        generate(defaultConfig);
    }

    //默认配置
    public static AutoGenerator getDefaultConfig() {
        AutoGenerator autoGenerator = new AutoGenerator();
        //读取配置项
        Properties properties;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("classpath:application-shared.properties");
            Properties overwrittenProperties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
            for (Map.Entry<Object, Object> entry : overwrittenProperties.entrySet()) {
                properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(properties.getProperty("spring.datasource.url"));
        dataSourceConfig.setDriverName(properties.getProperty("spring.datasource.driver-class-name"));
        dataSourceConfig.setUsername(properties.getProperty("spring.datasource.username"));
        dataSourceConfig.setPassword(properties.getProperty("spring.datasource.password"));
        autoGenerator.setDataSource(dataSourceConfig);
        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setFileOverride(true); //文件存在则覆盖
        String userDir = System.getProperty("user.dir");
        final String modulePath = userDir + (!StringUtils.isEmpty(moduleName) ? "/" + moduleName : "");
        globalConfig.setOutputDir(modulePath + "/src/main/java"); //文件输出路径
        globalConfig.setAuthor("AutoGenerator"); //作者
        globalConfig.setBaseResultMap(true);//mapper XML 生成基本的resultMap
        globalConfig.setBaseColumnList(true);//mapper XML 生成通用的sql片段
        globalConfig.setOpen(false); //生成完自动打开文件夹
        //自定义文件命名，注意%s会自动填充表实体属性!
        globalConfig.setControllerName("%sController");
        globalConfig.setServiceName("%sService");
        globalConfig.setMapperName("%sMapper");
        globalConfig.setXmlName("%sMapper");
        globalConfig.setIdType(IdType.ASSIGN_UUID);
//        globalConfig.setSwagger2(true);
        autoGenerator.setGlobalConfig(globalConfig);
        //包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(properties.getProperty("api.base-package"));
        autoGenerator.setPackageInfo(pc);
        //pc. setModu 7eName("rts ");

        //使用freemarker模板引擎
        autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setService("generator/templates/service");
        templateConfig.setMapper(null);
        templateConfig.setController("generator/templates/controller");
        if (limitedGenerateModules != null && limitedGenerateModules.size() > 0) {
            if (!limitedGenerateModules.contains(GenerateModule.Service)) {
                templateConfig.setService(null);
            }
            if (!limitedGenerateModules.contains(GenerateModule.Controller)) {
                templateConfig.setController(null);
            }
            if (!limitedGenerateModules.contains(GenerateModule.Dao)) {
                templateConfig.setMapper(null);
            }
        }
        templateConfig.setServiceImpl("generator/templates/serviceImpl");
        if (!limitedGenerateModules.contains(GenerateModule.ServiceImpl)) {
            templateConfig.setServiceImpl(null);
        }
        autoGenerator.setTemplate(templateConfig);
        String enumPackages = properties.getProperty("mybatis-plus.type-enums-package");
        final String[] enumPackagesArray = StringUtils.isEmpty(enumPackages) ? null : StringUtils.tokenizeToStringArray(enumPackages, ",; \t\n");
        //. 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                //字段名和敌举类型映射
                if (enumColumnMap != null) {
                    map.put("typeMap", enumColumnMap);
                }
                if (!StringUtils.isEmpty(moduleName)) {
                    map.put("moduleName", moduleName);
                }
                map.put("enumPackages", enumPackagesArray);
                if (!StringUtils.isEmpty(clientInterfaceBasePackage)) {
                    map.put("clientInterfaceBasePackage", clientInterfaceBasePackage);
                    map.put("clientInterfaceSubFolder", clientInterfaceSubFolder);
                    map.put("daoBasePackage", pc.getParent() + ".business");
                }
                if (!StringUtils.isEmpty(dataSourceName)) {
                    map.put("dataSourceName", dataSourceName);
                }
                this.setMap(map);
            }
        };

        //.自定义输出配置，xm7输出到resources
        List<FileOutConfig> focList = new ArrayList<>();
        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.MaрXml)) {
            String templatePath = "generator/templates/mapperXml.ftl";
            focList.add(new FileOutConfig(templatePath) {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    //自定义输出文件名
                    // 如果伽Entity.没置了前后綴、此処注意xm7的名称会跟着爰生変化! !
                    return modulePath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
                }
            });
        }
        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.Entity)) {
            //entity输出路径
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/entity.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/%s/src/main/java/%s/entity/%s%s.java",
                            userDir, clientInterfaceModuleName, clientInterfaceBasePackage.replace(".", "/"),
                            StringUtils.isEmpty(clientInterfaceSubFolder) ? "" : clientInterfaceSubFolder + "/", tableInfo.getEntityName());
                    }
                });
                templateConfig.setEntity(null);
            } else {
                templateConfig.setEntity("generator/templates/entity");
            }
        }
        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.AddEntityDTO)) {
            //entityDTO输出路径
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/addEntityDTO.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/%s/src/main/java/%s/dto/%s%sAdd%sDTO.java",
                            userDir, clientInterfaceModuleName, clientInterfaceBasePackage.replace(".", "/"),
                            StringUtils.isEmpty(clientInterfaceSubFolder) ? "" : clientInterfaceSubFolder + "/", tableInfo.getEntityName().toLowerCase() + "/", tableInfo.getEntityName());
                    }
                });
                templateConfig.setEntity(null);
            } else {
                templateConfig.setEntity("generator/templates/addEntityDTO");
            }
        }

        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.EditEntityDTO)) {
            //entityDTO输出路径
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/editEntityDTO.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/%s/src/main/java/%s/dto/%s%sEdit%sDTO.java",
                            userDir, clientInterfaceModuleName, clientInterfaceBasePackage.replace(".", "/"),
                            StringUtils.isEmpty(clientInterfaceSubFolder) ? "" : clientInterfaceSubFolder + "/", tableInfo.getEntityName().toLowerCase() + "/", tableInfo.getEntityName());
                    }
                });
                templateConfig.setEntity(null);
            } else {
                templateConfig.setEntity("generator/templates/editEntityDTO");
            }
        }

        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.DeleteEntityDTO)) {
            //entityDTO输出路径
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/deleteEntityDTO.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/%s/src/main/java/%s/dto/%s%sDelete%sDTO.java",
                            userDir, clientInterfaceModuleName, clientInterfaceBasePackage.replace(".", "/"),
                            StringUtils.isEmpty(clientInterfaceSubFolder) ? "" : clientInterfaceSubFolder + "/", tableInfo.getEntityName().toLowerCase() + "/", tableInfo.getEntityName());
                    }
                });
                templateConfig.setEntity(null);
            } else {
                templateConfig.setEntity("generator/templates/deleteEntityDTO");
            }
        }

        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.EntityVO)) {
            //entityVO输出路径
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/entityVO.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/%s/src/main/java/%s/vo/%s%sVO.java",
                            userDir, clientInterfaceModuleName, clientInterfaceBasePackage.replace(".", "/"),
                            StringUtils.isEmpty(clientInterfaceSubFolder) ? "" : clientInterfaceSubFolder + "/" + tableInfo.getEntityName().toLowerCase() + "/", tableInfo.getEntityName());
                    }
                });
                templateConfig.setEntity(null);
            } else {
                templateConfig.setEntity("generator/templates/entityVO");
            }
        }


        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.Api)) {
            //api interface
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/interface.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/%s/src/main/java/%s/api/%s%sApi.java",
                            userDir, clientInterfaceModuleName, clientInterfaceBasePackage.replace(".", "/"),
                            StringUtils.isEmpty(clientInterfaceSubFolder) ? "" : clientInterfaceSubFolder + "/", tableInfo.getEntityName());
                    }
                });
            }
        }
        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.Mapper)) {
            //mapper
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/mapper.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/src/main/java/%s/infrastructure/mapper/%sMapper.java",
                            modulePath, pc.getParent().replace(".", "/") + "/business", tableInfo.getEntityName());
                    }
                });
            }
        }


        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.EntityConvert)) {
            //entityConvert输出路径
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/entityConvert.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/src/main/java/%s/convert/%sConvert.java",
                            modulePath, pc.getParent().replace(".", "/") + "/business", tableInfo.getEntityName());
                    }
                });
                templateConfig.setEntity(null);
            } else {
                templateConfig.setEntity("generator/templates/entityConvert");
            }
        }

        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.NewDao)) {
            //dao
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/dao.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/src/main/java/%s/infrastructure/dao/%sDao.java",
                            modulePath, pc.getParent().replace(".", "/") + "/business", tableInfo.getEntityName());
                    }
                });
            }
        }
        if (limitedGenerateModules == null || limitedGenerateModules.contains(GenerateModule.DaoImpl)) {
            //dao
            if (!StringUtils.isEmpty(clientInterfaceModuleName)) {
                focList.add(new FileOutConfig("generator/templates/daoImpl.ftl") {
                    @Override
                    public String outputFile(TableInfo tableInfo) {
                        return String.format("%s/src/main/java/%s/infrastructure/dao/impl/%sDaoImpl.java",
                            modulePath, pc.getParent().replace(".", "/") + "/business", tableInfo.getEntityName());
                    }
                });
            }
        }

        if (focList.size() > 0) {
            cfg.setFileOutConfigList(focList);
        }
        autoGenerator.setCfg(cfg);
        templateConfig.setXml(null);
        //策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityTableFieldAnnotationEnable(true);//实体类总是加上@Table、@TabelField注解
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略，下划线转驼峰命名
        strategy.setColumnNaming(NamingStrategy.underline_to_camel); //1/. 列名生成策略，下划线转驼峰命名
//        strategy.setSuperServiceClass(BaseService.class.getName());
        strategy.setRestControllerStyle(true);
//        strategy.setEntityLombokModel(true);
        //s trategy. setSuperContro7 7erC1ass(nu77);
        //s trategy. setSuperEnti tyC7ass (BaseDomain. c7ass);. /输出的Enti ty继承父类
        //'s trategy. setSuperEnti tyco7umns("ID", "CREATE_ TIME"); //.写于父类中的公共字段
        strategy.setSuperEntityClass(BaseEntity.class);
        strategy.setSuperEntityColumns("id", "is_delete", "create_by", "update_by", "create_time", "update_time");
        strategy.setInclude();
        //需要生成的表，可指定多个
        strategy.setControllerMappingHyphenStyle(false); //controller. mapping是否用-连接符
        //strategy. setTab7ePrefix();
        autoGenerator.setStrategy(strategy);
        //g7oba 7Config. setOpen(false);
        return autoGenerator;

    }

    public static void main(String[] args) {
        enumColumnMap = new HashMap<>();
        generate("A_API");
    }
}
