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
import lombok.Builder;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
*
* ${table.comment!} DeleteDTO
*
* @author ${author}
* @since ${date}
*/
<#if entityLombokModel>
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
    <#if superEntityClass??>
    <#else>
    </#if>
    <#if chainModel>
@Accessors(chain = true)
    </#if>
</#if>
<#if table.convert>
</#if>
<#if swagger2>
@ApiModel(value="Delete${entity}DTO对象", description="${table.comment!}")
</#if>
public class Delete${entity}DTO implements Serializable {

    @NotNull(message = "主键不能为空")
    @ApiModelProperty(value = "ids", required = true)
    private List<Serializable> ids;

}
