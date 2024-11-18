<#if cfg.clientInterfaceBasePackage??>
package ${cfg.clientInterfaceBasePackage}.dto<#if cfg.clientInterfaceSubFolder??>.${cfg.clientInterfaceSubFolder}</#if>.${entity?lower_case};
<#else>
package ${package.Entity};
</#if>

<#if swagger2>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
</#if>
<#if entityLombokModel>
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
</#if>

<#if cfg.enumPackages??>
    <#list cfg.enumPackages as package>
import ${package}.*;
    </#list>
</#if>
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
*
* ${table.comment!} AddDTO
*
* @author ${author}
* @since ${date}
*/
<#if entityLombokModel>
@Data
@NoArgsConstructor
@AllArgsConstructor
    <#if superEntityClass??>
        @EqualsAndHashCode(callSuper = true)
    <#else>
        @EqualsAndHashCode(callSuper = false)
    </#if>
    <#if chainModel>
@Accessors(chain = true)
    </#if>
</#if>
<#if table.convert>
</#if>
<#if swagger2>
@ApiModel(value="Add${entity}DTO对象", description="${table.comment!}")
</#if>
<#if superEntityClass??>
public class ${entity} extends ${superEntityClass}<#if activeRecord><${entity}></#if> {
<#elseif activeRecord>
public class ${entity} extends Model<${entity}> {
<#else>
public class Add${entity}DTO implements Serializable {
</#if>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>

    <#if swagger2>
    @ApiModelProperty(value = "${(field.comment)!}", required = true)
    <#else>
    /**
     * ${(field.comment)!} - ${field.type}
     */
    </#if>
    <#if field.keyFlag>
    <#-- 主键 -->
        <#if field.keyIdentityFlag>
        <#elseif idType??>
        <#elseif field.convert>
        </#if>
    <#-- 普通字段 -->
    <#elseif field.fill??>
    <#-- -----   存在字段填充设置   ----->
        <#if field.convert>
        <#else>
        </#if>
    <#elseif field.convert>
    </#if>
<#-- 乐观锁注解 -->
    <#if (versionFieldName!"") == field.name>
    </#if>
<#-- 逻辑删除注解 -->
    <#if (logicDeleteFieldName!"") == field.name>
    </#if>
<#-- property类型 -->
    <#if cfg.typeMap?? && cfg.typeMap[field.name]??>
        <#assign javaType=cfg.typeMap[field.name]>
    <#else>
        <#assign javaType=field.propertyType>
    </#if>
    <#if javaType == "String">
    @NotBlank(message = "${(field.comment)!}不能为空")
    <#else>
    @NotNull(message = "${(field.comment)!}不能为空")
    </#if>
    private ${javaType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->

<#if !entityLombokModel>
    <#list table.fields as field>
        <#if field.propertyType == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>

    <#-- property类型 -->
        <#if cfg.typeMap?? && cfg.typeMap[field.name]??>
            <#assign javaType=cfg.typeMap[field.name]>
        <#else>
            <#assign javaType=field.propertyType>
        </#if>

    public ${javaType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

        <#if entityBuilderModel>
    public ${entity} set${field.capitalName}(${javaType} ${field.propertyName}) {
        <#else>
    public void set${field.capitalName}(${javaType} ${field.propertyName}) {
        </#if>
        this.${field.propertyName} = ${field.propertyName};
        <#if entityBuilderModel>
            return this;
        </#if>
    }
    </#list>
</#if>

<#if entityColumnConstant>
    <#list table.fields as field>
        public static final String ${field.name?upper_case} = "${field.name}";

    </#list>
</#if>
<#if activeRecord>
    @Override
    protected Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }

</#if>
<#if !entityLombokModel>
    @Override
    public String toString() {
    return "${entity}{" +
    <#list table.fields as field>
        <#if field_index==0>
            "${field.propertyName}=" + ${field.propertyName} +
        <#else>
            ", ${field.propertyName}=" + ${field.propertyName} +
        </#if>
    </#list>
    "}";
    }
</#if>
}
