package ${package.Service};

import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Add${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Edit${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Delete${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.vo.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.${entity}VO;
<#if cfg.clientInterfaceBasePackage??>
import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
<#else>
import ${package.Entity}.${entity};
</#if>

import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.request.SearchTemplateDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
*
* ${table.comment!} 服务接口
*
* @author ${author}
* @since ${date}
*/
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${entity}Service {
    /**
    *   按主键查询
    *   @param id 主键id
    *   @return 实体
    */
    ${entity}VO findById(Serializable id);

    /**
    *   按主键集合查询
    *   @param ids 主键ids
    *   @return 实体列表
    */
    List<${entity}VO> findByIds(List<? extends Serializable> ids);

    /**
    *   按属性查询
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
    List<${entity}VO> findBy(String propertyName, Object propertyValue);

    /**
    *   按属性集合搜索模板查询多实体
    *   @param searchTemplateDTO 搜索模板参数
    *   @return 实体列表
    */
    List<${entity}VO> findBy(SearchTemplateDTO searchTemplateDTO);

    /**
    *   按属性查询
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
    List<${entity}VO> findBy(String propertyName, Collection<?> propertyValue);

    /**
    *   按属性查询
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体
    */
    ${entity}VO findOneBy(String propertyName, Object propertyValue);

    /**
    *   按属性查询
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体
    */
    ${entity}VO findOneBy(String propertyName, Collection<?> propertyValue);

    /**
    *   按属性集合搜索模板查询单实体
    *   @param searchTemplateDTO 搜索模板参数
    *   @return 实体
    */
    ${entity}VO findOneBy(SearchTemplateDTO searchTemplateDTO);

    /**
    *   查询所有
    *   @return 实体列表
    */
    List<${entity}VO> findAll();

    /**
    * 新增
    * @param add${entity}DTO ${package.ModuleName} 新增参数
    * @return 成功或失败
    */
    void add(Add${entity}DTO add${entity}DTO);

    /**
    * 批量新增
    * @param add${entity}DTO ${package.ModuleName} 新增参数
    * @return 成功或失败
    */
    void addBatch(List<Add${entity}DTO> add${entity}DTO);

    /**
    * 新增或编辑
    * @param edit${entity}DTO ${package.ModuleName} 新增或编辑参数
    * @return 成功或失败
    */
    void addOrEdit(Edit${entity}DTO edit${entity}DTO);

    /**
    * 批量新增或编辑
    * @param edit${entity}DTO ${package.ModuleName} 新增或编辑参数
    * @return 成功或失败
    */
    void addOrEditBatch(List<Edit${entity}DTO> edit${entity}DTO);
    /**
    * 编辑
    * @param edit${entity}DTO ${package.ModuleName} 编辑参数
    * @return 成功或失败
    */
    void edit(Edit${entity}DTO edit${entity}DTO);

    /**
    * 批量编辑
    * @param edit${entity}DTO ${package.ModuleName} 编辑参数
    * @return 成功或失败
    */
    void editBatchById(List<Edit${entity}DTO> edit${entity}DTO);

    /**
    * 批量删除
    * @param delete${entity}DTO ${package.ModuleName} 删除参数
    * @return 成功或失败
    */
    void delete(Delete${entity}DTO delete${entity}DTO);

    /**
    * 按主键删除
    * @param id 删除参数
    * @return 成功或失败
    */
    void deleteById(Serializable id);

    /**
    * 按属性删除
    * @param propertyName 删除属性名
    * @param propertyValue 删除属性值
    * @return 成功或失败
    */
    void deleteBy(String propertyName, Object propertyValue);

    /**
    * 按属性删除
    * @param propertyName 删除属性名
    * @param propertyValue 删除属性值集合
    * @return 成功或失败
    */
    void deleteBy(String propertyName, Collection<?> propertyValue);

    /**
    * 按主键集合批量删除
    * @param ids 删除参数
    * @return 成功或失败
    */
    void deleteByIds(Collection<? extends Serializable> ids);

    /**
    * 关键字搜索
    * @param searchDTO 搜索参数
    * @return 分页列表
    */
    PageResult<${entity}VO> search(SearchDTO searchDTO);

    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    boolean findByIdAndName(Serializable id, String name);
}
</#if>
