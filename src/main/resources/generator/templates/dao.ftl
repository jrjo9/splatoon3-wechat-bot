package ${cfg.daoBasePackage}.infrastructure.dao;
<#if cfg.clientInterfaceBasePackage??>
import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
<#else>
import ${package.Entity}.${entity};
</#if>
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * ${table.comment!} 数据访问接口
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${entity}Dao extends IBaseDao<${entity}> {

    /**
    * 新增
    * @param ${entity?uncap_first} ${package.ModuleName} 新增参数
    * @return 成功或失败
    */
    boolean add(${entity} ${entity?uncap_first});
    /**
    * 编辑
    * @param ${entity?uncap_first} ${package.ModuleName} 编辑参数
    * @return 成功或失败
    */
    boolean edit(${entity} ${entity?uncap_first});
    /**
    * 批量删除
    * @param ids 主键ids
    * @return 成功或失败
    */
    int delete(Collection<? extends Serializable> ids);
    /**
    * 关键字搜索
    * @param searchDTO 搜索参数
    * @return 分页列表
    */
    PageResult<${entity}> search(SearchDTO searchDTO);
    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    boolean findByIdAndName(Serializable id, String name);
}
