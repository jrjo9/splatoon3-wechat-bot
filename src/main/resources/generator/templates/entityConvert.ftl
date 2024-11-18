package ${cfg.daoBasePackage}.convert;

import com.mayday9.splatoonbot.common.web.response.PageResult;
<#if cfg.clientInterfaceBasePackage??>
    import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
    import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Add${entity}DTO;
    import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Edit${entity}DTO;
    import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Delete${entity}DTO;
    import ${cfg.clientInterfaceBasePackage}.vo.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.${entity}VO;
<#else>
    import ${package.Entity}.${entity};
</#if>

import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
*
* ${table.comment!} 服务转换器
*
* @author ${author}
* @since ${date}
*/
@Mapper
public interface ${entity}Convert {

${entity}Convert INSTANCE = Mappers.getMapper(${entity}Convert.class);

}

