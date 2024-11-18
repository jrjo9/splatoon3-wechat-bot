package ${cfg.daoBasePackage}.infrastructure.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.mayday9.splatoonbot.common.db.dao.impl.BaseDaoImpl;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import ${cfg.clientInterfaceBasePackage}.entity.<#if cfg.clientInterfaceSubFolder??>${cfg.clientInterfaceSubFolder}.</#if>${entity};
import ${cfg.daoBasePackage}.infrastructure.mapper.${table.mapperName};
import ${cfg.daoBasePackage}.infrastructure.dao.${entity}Dao;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * ${table.comment!} 数据访问实现类
 *
 * @author ${author}
 * @since ${date}
 */
@Repository
public class ${entity}DaoImpl extends BaseDaoImpl<${table.mapperName}, ${entity}> implements ${entity}Dao {


    /**
    * 新增
    * @param ${entity?uncap_first} 编辑参数
    * @return 成功或失败
    */
    @Override
    public boolean add(${entity}  ${entity?uncap_first}) {
        return super.save(${entity?uncap_first});
    }

    /**
    * 编辑
    * @param ${entity?uncap_first} 编辑参数
    * @return 成功或失败
    */
    @Override
    public boolean edit(${entity}  ${entity?uncap_first}) {
        return super.updateById(${entity?uncap_first});
    }

    /**
    * 批量删除
    * @param ids 主键ids
    * @return 成功或失败
    */
    @Override
    public int delete(Collection<? extends Serializable> ids) {
        return super.deleteByIds(ids);
    }

    /**
    * 关键字搜索
    * @param searchDTO 搜索参数
    * @return 分页列表
    */
    @Override
    public PageResult<${entity}> search(SearchDTO searchDTO) {
        //分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        LambdaQueryWrapper<${entity}> query = Wrappers.lambdaQuery() ;
        //时间范围，默认按createTime
        if (searchDTO.getBeginTime() != null) {
            query.ge(${entity}::getCreateTime, searchDTO.getBeginTime());
        }
        if (searchDTO.getEndTime() != null) {
            Date endTime = searchDTO.getEndTime();
            query.le(${entity}::getCreateTime, endTime);
        }
        //关键字比较，多个列or
        //if (!StringUtils.isEmpty(searchDTO.getKeyword())) {
        //单个列用like
        //query. like(${entity}::getxxx, searchDTO.getKeyword());
        //多个列用like
        //query. and(sub -> sub.like(${entity}::getxx1, searchDTO.getKeyword())
        // оr(). like(${entity}::getXX2, searchDTO.getKeyword()))
        //);
        //}
        //默认updateTime倒序排序
        query.orderByDesc(${entity}::getUpdateTime);
        List<${entity}> list = super.find(query);
        return new PageResult<>(list);
    }
    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    @Override
    public boolean findByIdAndName(Serializable id, String name) {
        LambdaQueryWrapper<${entity}> query = Wrappers.lambdaQuery();
        if (id != null) {
            query.ne(${entity}::getId, id);
        }
        //if (Func.isNotBlank(name)) {
         //   query.eq(${entity}::getName, name);
        //}

        return baseMapper.selectCount(query) > 0;
    }
}
