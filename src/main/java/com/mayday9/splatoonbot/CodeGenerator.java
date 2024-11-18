package com.mayday9.splatoonbot;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.mayday9.splatoonbot.common.BaseCodeGenerator;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {
    public static void main(String[] args) {
        //字段名和枚举名的映射，不区分表名
        Map<String, String> enumColumnMap = new HashMap<>();
        enumColumnMap.put("is_delete", "DeleteEnum");
        enumColumnMap.put("active_flag", "FlagEnum");

        BaseCodeGenerator.setEnumColumnMap(enumColumnMap);
//        BaseCodeGenerator.setModuleName("/");//多个module，需要指定modulename
        //指定entity生成到独立module里，并生成 api interface
        BaseCodeGenerator.setClientInterfaceModuleName("/");
//        BaseCodeGenerator.setClientInterfaceSubFolder("business");//文件夹
        BaseCodeGenerator.setClientInterfaceBasePackage("com.mayday9.splatoonbot.business");
        // 指定生成的文件
        BaseCodeGenerator.setLimitedGenerateModules(BaseCodeGenerator.GenerateModule.Dao,
            BaseCodeGenerator.GenerateModule.DaoImpl,
            BaseCodeGenerator.GenerateModule.NewDao,
            BaseCodeGenerator.GenerateModule.Entity,
            BaseCodeGenerator.GenerateModule.MaрXml,
            BaseCodeGenerator.GenerateModule.Mapper
        );
        AutoGenerator defaultConfig = BaseCodeGenerator.getDefaultConfig();
        defaultConfig.getGlobalConfig().setSwagger2(true);
        defaultConfig.getGlobalConfig().setDateType(DateType.ONLY_DATE);
        defaultConfig.getStrategy().setEntityLombokModel(true);
        //需要生成的表，可指定多个，留空为全部生成
        defaultConfig.getStrategy().setInclude(
            "t_basic_wx_user_nso"
        );
        BaseCodeGenerator.generate(defaultConfig);
    }
}


