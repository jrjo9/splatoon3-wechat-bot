package com.mayday9.splatoonbot.business.infrastructure.dao;

import com.mayday9.splatoonbot.business.entity.TBasicSignIn;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * 签到表 数据访问接口
 *
 * @author AutoGenerator
 * @since 2024-09-23
 */
public interface TBasicSignInDao extends IBaseDao<TBasicSignIn> {

    /**
     * 新增
     *
     * @param tBasicSignIn 新增参数
     * @return 成功或失败
     */
    boolean add(TBasicSignIn tBasicSignIn);

    /**
     * 编辑
     *
     * @param tBasicSignIn 编辑参数
     * @return 成功或失败
     */
    boolean edit(TBasicSignIn tBasicSignIn);

    /**
     * 批量删除
     *
     * @param ids 主键ids
     * @return 成功或失败
     */
    int delete(Collection<? extends Serializable> ids);

    /**
     * 关键字搜索
     *
     * @param searchDTO 搜索参数
     * @return 分页列表
     */
    PageResult<TBasicSignIn> search(SearchDTO searchDTO);

    /**
     * 名称不重复
     *
     * @param id   主键
     * @param name 名称
     * @return 名称重复数量
     */
    boolean findByIdAndName(Serializable id, String name);

    /**
     * 根据日期查找签到信息
     *
     * @param date 日期
     * @return TBasicSignIn
     */
    TBasicSignIn findByDate(String wxid, String gid, Date date);
}
