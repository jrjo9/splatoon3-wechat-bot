package com.mayday9.splatoonbot.business.convert;

import com.mayday9.splatoonbot.business.dto.basic.GroupWxUserInfoDTO;
import com.mayday9.splatoonbot.business.entity.TBasicWxUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Lianjiannan
 * @since 2024/10/9 16:23
 **/
@Mapper
public interface TBasicWxUserConvert {

    TBasicWxUserConvert INSTANCE = Mappers.getMapper(TBasicWxUserConvert.class);

    @Mappings({})
    GroupWxUserInfoDTO convertDO(TBasicWxUser wxUser);

}
