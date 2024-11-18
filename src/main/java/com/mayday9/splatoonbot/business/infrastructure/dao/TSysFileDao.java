package com.mayday9.splatoonbot.business.infrastructure.dao;
import com.mayday9.splatoonbot.business.entity.TSysFile;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * 附件表 数据访问接口
 *
 * @author AutoGenerator
 * @since 2024-09-27
 */
public interface TSysFileDao extends IBaseDao<TSysFile> {

    /**
    * 新增
    * @param tSysFile  新增参数
    * @return 成功或失败
    */
    boolean add(TSysFile tSysFile);
    /**
    * 编辑
    * @param tSysFile  编辑参数
    * @return 成功或失败
    */
    boolean edit(TSysFile tSysFile);
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
    PageResult<TSysFile> search(SearchDTO searchDTO);
    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    boolean findByIdAndName(Serializable id, String name);
}
