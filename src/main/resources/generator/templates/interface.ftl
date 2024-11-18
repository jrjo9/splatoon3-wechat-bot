package ${cfg.clientInterfaceBasePackage}.api<#if cfg.clientInterfaceSubFolder??>.${cfg.clientInterfaceSubFolder}</#if>;

import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Add${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Edit${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Delete${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.vo.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.${entity}VO;
import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
import org.springframework.cloud.openfeign.FeignClient;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.request.SearchTemplateDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
<#if superControllerClassPackage??>
    import ${superControllerClassPackage};
</#if>


/**
*
* ${table.comment!} API
*
* @author ${author}
* @since ${date}
*/
@FeignClient(value = "${cfg.moduleName}", contextId = "${cfg.moduleName}.${entity}Api")
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if><#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
public interface ${entity}Api {

    /**
    * 按主键查询
    * @param id 主键id
    * @return 实体
    */
    @GetMapping("/find/{id}")
    ${entity}VO findById(@PathVariable(value = "id") Serializable id);

    /**
    * 按主键集合查询
    * @param ids 主键集合ids
    * @return 实体
    */
    @PostMapping("/findByIds")
    List<${entity}VO> findByIds(@RequestBody List<Serializable> ids);

    /**
    *   按属性查询多实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
    @GetMapping("/findByProp")
    List<${entity}VO> findBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Object propertyValue);

    /**
    *   按属性值集合查询多实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
    @GetMapping("/findByProps")
    List<${entity}VO> findBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue);

    /**
    *   按属性集合搜索模板查询多实体
    *   @param searchTemplateDTO 搜索模板参数
    *   @return 实体列表
    */
    @PostMapping("/findByMaps")
    List<${entity}VO> findBy(@Validated @RequestBody SearchTemplateDTO searchTemplateDTO);

    /**
    *   按属性查询单实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体
    */
    @GetMapping("/findOneByProp")
    ${entity}VO findOneBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue")  Object propertyValue);

    /**
    *   按属性值集合查询单实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体
    */
    @GetMapping("/findOneByProps")
    ${entity}VO findOneBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue);

    /**
    *   按属性集合搜索模板查询单实体
    *   @param searchTemplateDTO 搜索模板参数
    *   @return 实体
    */
    @PostMapping("/findOneByMaps")
    ${entity}VO findOneBy(@Validated @RequestBody SearchTemplateDTO searchTemplateDTO);

    /**
    * 查询所有
    * @return 实体
    */
    @GetMapping("/findAll")
    List<${entity}VO> findAll();

    /**
    * 关键字搜索
    * @param searchDTO 搜索参数
    * @return 分页列表
    */
    @PostMapping("/search")
    PageResult<${entity}VO> search(@Validated @RequestBody SearchDTO searchDTO);

    /**
    * 新增
    * @param add${entity}DTO ${package.ModuleName} 新增参数
    */
    @PostMapping("/add")
    void add(@Validated @RequestBody Add${entity}DTO add${entity}DTO);

    /**
    * 批量新增
    * @param add${entity}DTOList ${package.ModuleName} 新增参数
    */
    @PostMapping("/addBatch")
    void addBatch(@Validated @RequestBody List<Add${entity}DTO> add${entity}DTOList);

    /**
    * 新增或编辑
    * @param edit${entity}DTO ${package.ModuleName} 新增或编辑参数
    */
    @PostMapping("/addOrEdit")
    void addOrEdit(@Validated @RequestBody Edit${entity}DTO edit${entity}DTO);

    /**
    * 批量新增或编辑
    * @param edit${entity}DTOList ${package.ModuleName} 新增或编辑参数
    */
    @PostMapping("/addOrEditBatch")
    void addOrEditBatch(@Validated @RequestBody List<Edit${entity}DTO> edit${entity}DTOList);

    /**
    * 编辑
    * @param edit${entity}DTO ${package.ModuleName} 编辑参数
    */
    @PutMapping("/edit")
    void edit(@Validated @RequestBody Edit${entity}DTO edit${entity}DTO);

    /**
    * 批量编辑
    * @param edit${entity}DTO ${package.ModuleName} 编辑参数
    */
    @PutMapping("/editBatchById")
    void editBatchById(@Validated @RequestBody List<Edit${entity}DTO> edit${entity}DTO);

    /**
    * 批量删除
    * @param delete${entity}DTO ${package.ModuleName} 删除参数
    */
    @DeleteMapping("/delete")
    void delete(@Validated @RequestBody Delete${entity}DTO delete${entity}DTO);

    /**
    * 按主键删除
    * @param id 删除参数
    */
    @DeleteMapping("/deleteById/{id}")
    void deleteById(@PathVariable(value = "id") Serializable id);

    /**
    * 按属性删除
    * @param propertyName 删除属性名
    * @param propertyValue 删除属性值
    */
    @DeleteMapping("/deleteByProp")
    void deleteBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Object propertyValue);

    /**
    * 按属性值集合删除
    * @param propertyName 删除属性名
    * @param propertyValue 删除属性值集合
    */
    @DeleteMapping("/deleteByProps")
    void deleteBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue);

    /**
    * 按主键集合批量删除
    * @param ids 删除参数
    */
    @DeleteMapping("/deleteByIds")
    void deleteByIds(@Validated @RequestBody Collection<Serializable> ids);

    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    @GetMapping("/findByIdAndName")
    boolean findByIdAndName(@RequestParam("id") Serializable id,@RequestParam("name") String name);
}
