package com.mayday9.splatoonbot.business.infrastructure.dao;
import com.mayday9.splatoonbot.business.entity.TBasicWxGroup;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * 微信群信息表 数据访问接口
 *
 * @author AutoGenerator
 * @since 2024-09-25
 */
public interface TBasicWxGroupDao extends IBaseDao<TBasicWxGroup> {

    /**
    * 新增
    * @param tBasicWxGroup  新增参数
    * @return 成功或失败
    */
    boolean add(TBasicWxGroup tBasicWxGroup);
    /**
    * 编辑
    * @param tBasicWxGroup  编辑参数
    * @return 成功或失败
    */
    boolean edit(TBasicWxGroup tBasicWxGroup);
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
    PageResult<TBasicWxGroup> search(SearchDTO searchDTO);
    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    boolean findByIdAndName(Serializable id, String name);

    /**
     * 根据组ID查找组
     *
     * @param gid 组ID
     * @return TBasicWxGroup
     */
    TBasicWxGroup findGroupByGid(String gid);
}
