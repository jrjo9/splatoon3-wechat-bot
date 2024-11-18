package com.mayday9.splatoonbot.business.infrastructure.dao;
import com.mayday9.splatoonbot.business.entity.TSplatSalmonRunInfo;
import com.mayday9.splatoonbot.business.vo.TSplatMatchInfoVO;
import com.mayday9.splatoonbot.business.vo.TSplatSalmonRunInfoVO;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.response.PageResult;
import com.mayday9.splatoonbot.common.db.dao.IBaseDao;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * 喷喷鲑鱼跑打工信息表 数据访问接口
 *
 * @author AutoGenerator
 * @since 2024-10-08
 */
public interface TSplatSalmonRunInfoDao extends IBaseDao<TSplatSalmonRunInfo> {

    /**
    * 新增
    * @param tSplatSalmonRunInfo  新增参数
    * @return 成功或失败
    */
    boolean add(TSplatSalmonRunInfo tSplatSalmonRunInfo);
    /**
    * 编辑
    * @param tSplatSalmonRunInfo  编辑参数
    * @return 成功或失败
    */
    boolean edit(TSplatSalmonRunInfo tSplatSalmonRunInfo);
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
    PageResult<TSplatSalmonRunInfo> search(SearchDTO searchDTO);
    /**
    * 名称不重复
    * @param id 主键
    * @param name 名称
    * @return 名称重复数量
    */
    boolean findByIdAndName(Serializable id, String name);

    /**
     * 查询打工日程数据
     *
     * @param params 参数
     * @return List<TSplatSalmonRunInfo>
     */
    List<TSplatSalmonRunInfo> findMatch(Map<String, Object> params);

    /**
     * 根据日期查找日程信息
     *
     * @param date        日期
     * @param matchNumber 比赛数量
     * @return List<TSplatMatchInfoVO>
     */
    List<TSplatSalmonRunInfoVO> findMatchByDate(Date date, Integer matchNumber);
}
