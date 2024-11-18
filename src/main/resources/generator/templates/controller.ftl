package ${package.Controller};

import com.mayday9.splatoonbot.common.web.aspect.log.annotation.Log;
import com.mayday9.splatoonbot.common.web.aspect.log.enums.LogLevel;
import com.mayday9.splatoonbot.common.web.aspect.log.enums.OperationTypeEnum;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Add${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Edit${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Delete${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.vo.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.${entity}VO;
<#if cfg.clientInterfaceBasePackage??>
import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
import ${cfg.clientInterfaceBasePackage}.api.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity}Api;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.request.SearchTemplateDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation. *;
<#else>
import ${package.Entity}.${entity};
import org.springframework.web.bind.annotation. *;
</#if>
import ${package.Service}.${table.serviceName};
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
/**
*
* ${table.comment!} 控制器类
*
* @author ${author}
* @since ${date}
*/
@Api(value = "${table.comment!}", tags = "${table.comment!}")
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
<#if kotlin>
    class ${table.controllerName} <#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
    <#if superControllerClass??>
        <#if cfg.clientInterfaceBasePackage??>
public class ${table.controllerName} extends ${superControllerClass} implements ${entity}Api {
@RequestMapping("/${table.entityPath}")
public class ${table.controllerName} extends ${superControllerClass} {
        </#if>
    <#else>
        <#if cfg.clientInterfaceBasePackage??>
public class ${table.controllerName} implements ${entity}Api {
        <#else>
@RequestMapping("/${table.entityPath}")
public class ${table.controllerName} {
        </#if>
    </#if>
    @Resource
    private ${table.serviceName} ${table.serviceName?substring(1)?uncap_first};
    /**
    *按主键查询
    * @param id 主键Id
    * @return 实体
    */
    @ApiOperation("按主键查询")
    <#if cfg.clientInterfaceBasePackage??>
    @Log(operationType = OperationTypeEnum.查询, operationDescription = "按主键查询单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
    @Override
    public ${entity}VO findById(@PathVariable(value = "id") Serializable id) {
    <#else>
    @GetMapping("/find/{id}")
    public ${entity}VO findById(@PathVariable(value = "id") Serializable id) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findById(id);
    }

    /**
    *按主键集合查询
    * @param ids 主键Ids
    * @return 实体列表
    */
    //@ApiOperation("按主键集合查询")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "按主键集合查询多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public List<${entity}VO> findByIds(@Validated @RequestBody List<Serializable> ids) {
    <#else>
        @GetMapping("/findByIds")
        public List<${entity}VO> findByIds(@Validated @RequestBody List<Serializable> ids) {
    </#if>
    return ${table.serviceName?substring(1)?uncap_first}.findByIds(ids);
    }


    /**
    *   按属性查询多实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
    //@ApiOperation("按属性查询多实体")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "按属性查询多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public List<${entity}VO> findBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue")  Object propertyValue) {
    <#else>
        @GetMapping("/findBy")
        public List<${entity}VO> findBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Object propertyValue) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findBy(propertyName,propertyValue);
    }

    /**
    *   按属性值集合查询多实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
   // @ApiOperation("按属性值集合查询多实体")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "按属性值集合查询多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public List<${entity}VO> findBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue) {
    <#else>
        @GetMapping("/findBy")
        public List<${entity}VO> findBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findBy(propertyName,propertyValue);
    }

    /**
    *   按属性集合搜索模板查询多实体
    *   @param searchTemplateDTO 搜索模板参数
    *   @return 实体列表
    */
    // @ApiOperation("按属性集合搜索模板查询多实体")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "按属性集合搜索模板查询多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public List<${entity}VO> findBy(@Validated @RequestBody SearchTemplateDTO searchTemplateDTO) {
    <#else>
        @GetMapping("/findByMaps")
        public List<${entity}VO> findBy(@Validated @RequestBody SearchTemplateDTO searchTemplateDTO) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findBy(searchTemplateDTO);
    }

    /**
    *   按属性查询单实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
   // @ApiOperation("按属性查询单实体")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "按属性查询单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public ${entity}VO findOneBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Object propertyValue) {
    <#else>
        @GetMapping("/findBy")
        public ${entity}VO findOneBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Object propertyValue) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findOneBy(propertyName, propertyValue);
    }

    /**
    *   按属性值集合查询单实体
    *   @param propertyName 属性名
    *   @param propertyValue 属性值
    *   @return 实体列表
    */
   // @ApiOperation("按属性值集合查询单实体")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "按属性值集合单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public ${entity}VO findOneBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue) {
    <#else>
        @GetMapping("/findBy")
        public ${entity}VO findOneBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findOneBy(propertyName, propertyValue);
    }

    /**
    *   按属性集合搜索模板查询单实体
    *   @param searchTemplateDTO 搜索模板参数
    *   @return 实体
    */
    // @ApiOperation("按属性集合搜索模板查询单实体")
    <#if cfg.clientInterfaceBasePackage??>
    @Log(operationType = OperationTypeEnum.查询, operationDescription = "按属性集合搜索模板查询单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
    @Override
    public ${entity}VO findOneBy(@Validated @RequestBody SearchTemplateDTO searchTemplateDTO) {
        <#else>
        @GetMapping("/findOneByMaps")
        public ${entity}VO findOneBy(@Validated @RequestBody SearchTemplateDTO searchTemplateDTO) {
        </#if>
        return ${table.serviceName?substring(1)?uncap_first}.findOneBy(searchTemplateDTO);
     }
    /**
    *查询所有
    * @return 实体
    */
    @ApiOperation("查询所有")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "查询所有${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public List<${entity}VO> findAll() {
    <#else>
        @GetMapping("/findAll")
        public ${entity}VO findAll() {
    </#if>
    return ${table.serviceName?substring(1)?uncap_first}.findAll();
    }

    /**
    * 关键字搜索
    * @param searchDTO 搜索参数
    * @return 分页列表
    */
    @ApiOperation("关键字搜索")
    <#if cfg.clientInterfaceBasePackage??>
    @Log(operationType = OperationTypeEnum.查询, operationDescription = "关键字分页搜索${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
    @Override
    public PageResult<${entity}VO> search(@Validated @RequestBody SearchDTO searchDTO) {
    <#else>
    @PostMapping("/search")
    public PageResult<${entity}VO> search(@Validated @RequestBody SearchDTO searchDTO) {
    </#if>
        return ${table.serviceName?substring(1)?uncap_first}.search(searchDTO);
    }

    /**
    * 新增
    * @param add${entity}DTO 新增参数
    */
    @ApiOperation("新增")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.增加, operationDescription = "新增单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void add(@Validated @RequestBody Add${entity}DTO add${entity}DTO) {
    <#else>
        @PostMapping("/add")
        public void add(@Validated @RequestBody Add${entity}DTO add${entity}DTO) {
    </#if>
     ${table.serviceName?substring(1)?uncap_first}.add(add${entity}DTO);
    }

    /**
    * 批量新增
    * @param add${entity}DTO 新增参数
    */
   // @ApiOperation("批量新增")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.增加, operationDescription = "批量新增多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void addBatch(@Validated @RequestBody List<Add${entity}DTO> add${entity}DTO) {
    <#else>
        @PostMapping("/addBatch")
        public void addBatch(@Validated @RequestBody List<Add${entity}DTO> add${entity}DTO) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.addBatch(add${entity}DTO);
    }

    /**
    * 新增或编辑
    * @param edit${entity}DTO 新增或编辑参数
    */
 //   @ApiOperation("新增或编辑")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.修改, operationDescription = "新增或编辑单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void addOrEdit(@Validated @RequestBody Edit${entity}DTO edit${entity}DTO) {
    <#else>
        @PutMapping("/addOrEdit")
        public void addOrEdit(@Validated @RequestBody Edit${entity}DTO edit${entity}DTO) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.addOrEdit(edit${entity}DTO);
    }

    /**
    * 批量新增或编辑
    * @param edit${entity}DTOList 编辑参数
    */
   // @ApiOperation("批量新增或编辑")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.修改, operationDescription = "新增或编辑多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void addOrEditBatch(@Validated @RequestBody List<Edit${entity}DTO> edit${entity}DTOList) {
    <#else>
        @PutMapping("/addOrEditBatch")
        public void addOrEditBatch(@Validated @RequestBody List<Edit${entity}DTO> edit${entity}DTOList) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.addOrEditBatch(edit${entity}DTOList);
    }

    /**
    * 编辑
    * @param edit${entity}DTO 编辑参数
    */
    @ApiOperation("编辑")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.修改, operationDescription = "编辑单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void edit(@Validated @RequestBody Edit${entity}DTO edit${entity}DTO) {
    <#else>
        @PutMapping("/edit")
        public void edit(@Validated @RequestBody Edit${entity}DTO edit${entity}DTO) {
    </#if>
     ${table.serviceName?substring(1)?uncap_first}.edit(edit${entity}DTO);
    }

    /**
    * 批量编辑
    * @param edit${entity}DTO 编辑参数
    */
   // @ApiOperation("批量编辑")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.修改, operationDescription = "批量编辑多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void editBatchById(@Validated @RequestBody List<Edit${entity}DTO> edit${entity}DTO) {
    <#else>
        @PutMapping("/editBatchById")
        public void editBatchById(@Validated @RequestBody List<Edit${entity}DTO> edit${entity}DTO) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.editBatchById(edit${entity}DTO);
    }

    /**
    * 批量删除
    * @param delete${entity}DTO ${package.ModuleName} 删除参数
    */
    //@ApiOperation("批量删除")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.删除, operationDescription = "批量删除多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void delete(@Validated @RequestBody Delete${entity}DTO delete${entity}DTO) {
    <#else>
        @DeleteMapping("/delete")
        public void delete(@Validated @RequestBody Delete${entity}DTO delete${entity}DTO) {
    </#if>
         ${table.serviceName?substring(1)?uncap_first}.delete(delete${entity}DTO);
    }

    /**
    * 按主键删除
    * @param id 删除参数
    */
    @ApiOperation("按主键删除")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.删除, operationDescription = "删除单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void deleteById(@PathVariable(value = "id") Serializable id) {
    <#else>
        @DeleteMapping("/delete/{id}")
        public void deleteById(@PathVariable(value = "id") Serializable id) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.deleteById(id);
    }

    /**
    * 按属性删除
    * @param propertyName 删除属性名
    * @param propertyValue 删除属性值
    */
   // @ApiOperation("按属性删除")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.删除, operationDescription = "按属性删除单条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void deleteBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue")  Object propertyValue) {
    <#else>
        @DeleteMapping("/deleteBy")
        public void deleteBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Object propertyValue) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.deleteBy(propertyName, propertyValue);
    }

    /**
    * 按属性值集合删除
    * @param propertyName 删除属性名
    * @param propertyValue 删除属性值集合
    */
   // @ApiOperation("按属性值集合删除")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.删除, operationDescription = "按属性值集合删除多条${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void deleteBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue) {
    <#else>
        @DeleteMapping("/deleteBy")
        public void deleteBy(@RequestParam("propertyName") String propertyName, @RequestParam("propertyValue") Collection<Serializable> propertyValue) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.deleteBy(propertyName, propertyValue);
    }

    /**
    * 按主键集合批量删除
    * @param ids 删除参数
    */
    @ApiOperation("按主键集合批量删除")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.删除, operationDescription = "批量删除${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public void deleteByIds(@Validated @RequestBody Collection<Serializable> ids) {
    <#else>
        @DeleteMapping("/deleteByIds")
        public void deleteByIds(@Validated @RequestBody Collection<Serializable> ids) {
    </#if>
    ${table.serviceName?substring(1)?uncap_first}.deleteByIds(ids);
    }

    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
  //  @ApiOperation("名称不重复")
    <#if cfg.clientInterfaceBasePackage??>
        @Log(operationType = OperationTypeEnum.查询, operationDescription = "查询名称不重复的${table.comment!}管理数据", module = "${table.comment!}", logLevel = LogLevel.Important)
        @Override
        public boolean findByIdAndName(@RequestParam("id") Serializable id, @RequestParam("name") String name) {
    <#else>
        @DeleteMapping("/findByIdAndName")
        public boolean findByIdAndName(@RequestParam("id") Serializable id, @RequestParam("name") String name) {
    </#if>
        return  ${table.serviceName?substring(1)?uncap_first}.findByIdAndName(id, name);
    }
}
</#if>
