package com.mayday9.splatoonbot.business.infrastructure.mapper;

import com.mayday9.splatoonbot.business.dto.basic.GroupWxUserInfoDTO;
import com.mayday9.splatoonbot.business.entity.TBasicWxUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
*
* 微信人员信息表 Mapper 接口
*
* @author AutoGenerator
* @since 2024-09-24
*/
@Mapper
public interface TBasicWxUserMapper extends BaseMapper<TBasicWxUser> {

    GroupWxUserInfoDTO findGroupWxUserInfo(@Param("gid") String gid,@Param("wxid") String wxid);
}
