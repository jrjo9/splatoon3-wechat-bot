package ${package.Service}.impl;

import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Add${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Edit${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.dto.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.Delete${entity}DTO;
import ${cfg.clientInterfaceBasePackage}.vo.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity?lower_case}.${entity}VO;
<#if cfg.clientInterfaceBasePackage??>
    import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
<#else>
    import ${package.Entity}.${entity};
</#if>
import com.mayday9.splatoonbot.common.util.core.Func;
import ${cfg.daoBasePackage}.convert.${entity}Convert;
import ${cfg.daoBasePackage}.infrastructure.dao.${entity}Dao;
import ${package.Service}.${table.serviceName};
import org.springframework.stereotype.Service;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.request.SearchTemplateDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import com.mayday9.splatoonbot.common.web.response.ApiException;
import com.mayday9.splatoonbot.common.web.response.ExceptionCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.List;
import java.io.Serializable;
import java.util.Collection;
import java.time.LocalDateTime;

/**
*
* ${table.comment!} 服务实现类
*
* @author ${author}
* @since ${date}
*/
@Service
public class ${entity}ServiceImpl implements ${entity}Service {
@Resource
private ${entity}Dao ${entity?substring(1)?uncap_first}Dao;

/**
*按主键查询
* @param id 主键Id
* @return 实体
*/
@Override
public ${entity}VO findById(Serializable id) {
return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findById(id));
}

/**
*   按主键集合查询
*   @param ids 主键ids
*   @return 实体列表
*/
@Override
public List
<${entity}VO> findByIds(List<? extends Serializable> ids) {
    return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findByIds(ids));
    }

    /**
    * 按属性查询
    * @param propertyName 属性名
    * @param propertyValue 属性值
    * @return 实体列表
    */
    @Override
    public List
    <${entity}VO> findBy(String propertyName, Object propertyValue) {
        return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findBy(propertyName, propertyValue));
        }

        /**
        * 按属性集合搜索模板查询多实体
        * @param searchTemplateDTO 搜索模板参数
        * @return 实体列表
        */
        @Override
        public List
        <${entity}VO> findBy(SearchTemplateDTO searchTemplateDTO) {
            return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findBy(searchTemplateDTO.getEqParams(), searchTemplateDTO.getNeParams(), searchTemplateDTO.getInParams()));
            }

            /**
            * 按属性查询
            * @param propertyName 属性名
            * @param propertyValue 属性值
            * @return 实体列表
            */
            @Override
            public List
            <${entity}VO> findBy(String propertyName, Collection<?> propertyValue) {
                return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findBy(propertyName, propertyValue));
                }

                /**
                * 按属性查询
                * @param propertyName 属性名
                * @param propertyValue 属性值
                * @return 实体
                */
                @Override
                public ${entity}VO findOneBy(String propertyName, Object propertyValue) {
                return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findOneBy(propertyName, propertyValue));
                }

                /**
                * 按属性查询
                * @param propertyName 属性名
                * @param propertyValue 属性值
                * @return 实体
                */
                @Override
                public ${entity}VO findOneBy(String propertyName, Collection<?> propertyValue) {
                return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findOneBy(propertyName, propertyValue));
                }

                /**
                * 按属性集合搜索模板查询单实体
                * @param searchTemplateDTO 搜索模板参数
                * @return 实体
                */
                @Override
                public ${entity}VO findOneBy(SearchTemplateDTO searchTemplateDTO) {
                return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findOneBy(searchTemplateDTO.getEqParams(), searchTemplateDTO.getNeParams(),
                searchTemplateDTO.getInParams()));
                }

                /**
                *查询所有
                * @return 实体列表
                */
                @Override
                public List
                <${entity}VO> findAll() {
                    return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.findAll());
                    }

                    /**
                    * 关键字搜索
                    * @param searchDTO 搜索参数
                    * @return 分页列表
                    */
                    @Override
                    public PageResult
                    <${entity}VO> search(SearchDTO searchDTO) {
                        return ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.search(searchDTO));

                        //PageResult
                        <${entity}VO> result = ${entity}Convert.INSTANCE.convertVO(${entity?substring(1)?uncap_first}Dao.search(searchDTO));

                            //if (CollectionUtils.isEmpty(result.getList())) {
                            //return new PageResult<>();
                            //}

                            //Map
                            <String
                            , SchSystemParams> map = iSchSystemParamsService.find(ListUtils.distinctSelect(result.getList(), SchCollectorVO::getCollectorId));

                            //result.getList().forEach(p -> {
                            // String collectorId = String.valueOf(p.getCollectorId());
                            // if (map.containsKey(collectorId)) {
                            // p.setCollectorName(map.get(collectorId).getParamName());
                            // }
                            //});

                            //return result;
                            }

                            /**
                            * 新增
                            * @param add${entity}DTO 编辑参数
                            * @return 成功或失败
                            */
                            @Override
                            @Transactional(rollbackFor = Exception.class)
                            public void add(Add${entity}DTO add${entity}DTO) {
                            ${entity} ${entity?uncap_first} =${entity}Convert.INSTANCE.convertDO(add${entity}DTO);
                            ${entity?substring(1)?uncap_first}Dao.add(${entity?uncap_first});

                            // String name = add${entity}DTO.getName();

                            //${entity} ${entity?uncap_first} = ${entity?substring(1)?uncap_first}Dao.findOneBy("name", name);

                            //if (Func.isNotEmpty(${entity?uncap_first})) {
                            // throw new ApiException(ExceptionCode.ParamIllegal.getCode(), name+"在${table.comment!}已经存在");
                            // }

                            // ${entity} ${entity?uncap_first}DO =${entity}Convert.INSTANCE.convertDO(add${entity}DTO);

                            // ${entity?substring(1)?uncap_first}Dao.add(${entity?uncap_first}DO);

                            }

                            /**
                            * 批量新增
                            * @param add${entity}DTOList 编辑参数
                            * @return 成功或失败
                            */
                            @Override
                            @Transactional(rollbackFor = Exception.class)
                            public void addBatch(List
                            <Add${entity}DTO> add${entity}DTOList) {

                                List<${entity}> ${entity?uncap_first}List =${entity}Convert.INSTANCE.convertAddDOList(add${entity}DTOList);

                                ${entity?substring(1)?uncap_first}Dao.saveBatch(${entity?uncap_first}List, IBaseDao.DEFAULT_BATCH_SIZE);
                                }
                                /**
                                * 新增或编辑
                                * @param edit${entity}DTO 新增或编辑参数
                                * @return 成功或失败
                                */
                                @Override
                                @Transactional(rollbackFor = Exception.class)
                                public void addOrEdit(Edit${entity}DTO edit${entity}DTO) {
                                ${entity} ${entity?uncap_first} =${entity}Convert.INSTANCE.convertDO(edit${entity}DTO);
                                ${entity?substring(1)?uncap_first}Dao.saveOrUpdate(${entity?uncap_first});
                                }

                                /**
                                * 批量新增或编辑
                                * @param edit${entity}DTOList 新增或编辑参数
                                * @return 成功或失败
                                */
                                @Override
                                @Transactional(rollbackFor = Exception.class)
                                public void addOrEditBatch(List
                                <Edit${entity}DTO> edit${entity}DTOList) {
                                    List<${entity}> ${entity?uncap_first}List =${entity}Convert.INSTANCE.convertEditDOList(edit${entity}DTOList);
                                    ${entity?substring(1)?uncap_first}Dao.saveOrUpdateBatch(${entity?uncap_first}List, IBaseDao.DEFAULT_BATCH_SIZE);
                                    }
                                    /**
                                    * 编辑
                                    * @param edit${entity}DTO 编辑参数
                                    * @return 成功或失败
                                    */
                                    @Override
                                    @Transactional(rollbackFor = Exception.class)
                                    public void edit(Edit${entity}DTO edit${entity}DTO) {
                                    //${entity} ${entity?uncap_first} =${entity}Convert.INSTANCE.convertDO(edit${entity}DTO);
                                    // ${entity?substring(1)?uncap_first}Dao.edit(${entity?uncap_first});

                                    Long id = edit${entity}DTO.getId();

                                    ${entity} ${entity?uncap_first}DO = ${entity?substring(1)?uncap_first}Dao.findById(id);

                                    if (Func.isEmpty(${entity?uncap_first}DO)) {
                                    throw new ApiException(ExceptionCode.ParamIllegal.getCode(), "未找到${table.comment!}数据，无法更新！");
                                    }

                                    ${entity} ${entity?uncap_first} =${entity}Convert.INSTANCE.convertDO(edit${entity}DTO,${entity?uncap_first}DO);

                                    ${entity?substring(1)?uncap_first}Dao.edit(${entity?uncap_first});
                                    }

                                    /**
                                    * 批量编辑
                                    * @param edit${entity}DTOList 编辑参数
                                    * @return 成功或失败
                                    */
                                    @Override
                                    @Transactional(rollbackFor = Exception.class)
                                    public void editBatchById(List
                                    <Edit${entity}DTO> edit${entity}DTOList) {
                                        List<${entity}> ${entity?uncap_first}List =${entity}Convert.INSTANCE.convertEditDOList(edit${entity}DTOList);
                                        ${entity?substring(1)?uncap_first}Dao.updateBatchById(${entity?uncap_first}List, IBaseDao.DEFAULT_BATCH_SIZE);
                                        }

                                        /**
                                        * 批量删除
                                        * @param delete${entity}DTO ${package.ModuleName} 删除参数
                                        * @return 成功或失败
                                        */
                                        @Override
                                        @Transactional(rollbackFor = Exception.class)
                                        public void delete(Delete${entity}DTO delete${entity}DTO) {
                                        ${entity?substring(1)?uncap_first}Dao.delete(delete${entity}DTO.getIds());
                                        }

                                        /**
                                        * 按主键删除
                                        * @param id 删除参数
                                        * @return 成功或失败
                                        */
                                        @Override
                                        @Transactional(rollbackFor = Exception.class)
                                        public void deleteById(Serializable id) {
                                        ${entity?substring(1)?uncap_first}Dao.deleteById(id);
                                        }

                                        /**
                                        * 按属性删除
                                        * @param propertyName 删除属性名
                                        * @param propertyValue 删除属性值
                                        * @return 成功或失败
                                        */
                                        @Override
                                        @Transactional(rollbackFor = Exception.class)
                                        public void deleteBy(String propertyName, Object propertyValue) {
                                        ${entity?substring(1)?uncap_first}Dao.deleteBy(propertyName, propertyValue);
                                        }

                                        /**
                                        * 按属性删除
                                        * @param propertyName 删除属性名
                                        * @param propertyValue 删除属性值集合
                                        * @return 成功或失败
                                        */
                                        @Override
                                        @Transactional(rollbackFor = Exception.class)
                                        public void deleteBy(String propertyName, Collection<?> propertyValue) {
                                        ${entity?substring(1)?uncap_first}Dao.deleteBy(propertyName, propertyValue);
                                        }

                                        /**
                                        * 按主键集合批量删除
                                        * @param ids 删除参数
                                        * @return 成功或失败
                                        */
                                        @Override
                                        @Transactional(rollbackFor = Exception.class)
                                        public void deleteByIds(Collection<? extends Serializable> ids) {
                                        ${entity?substring(1)?uncap_first}Dao.deleteByIds(ids);
                                        }

                                        /**
                                        * 名称不重复
                                        * @param id 主键
                                        * @param name 名称
                                        * @return 名称重复数量
                                        */
                                        @Override
                                        public boolean findByIdAndName(Serializable id, String name) {
                                        return ${entity?substring(1)?uncap_first}Dao.findByIdAndName(id, name);
                                        }

                                        }

