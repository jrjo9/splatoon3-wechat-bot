package ${cfg.daoBasePackage}.infrastructure.mapper;

<#if cfg.clientInterfaceBasePackage??>
import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
<#else>
import ${package.Entity}.${entity};
</#if>
<#if cfg.dataSourceName??>
import com.baomidou.dynamic.datasource.annotation.DS;
</#if>
import ${superMapperClassPackage};
import org.apache.ibatis.annotations.Mapper;

/**
*
* ${table.comment!} Mapper 接口
*
* @author ${author}
* @since ${date}
*/
<#if cfg.dataSourceName??>
    @DS("${cfg.dataSourceName}")
</#if>
@Mapper
<#if kotlin>
    interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

}
</#if>
