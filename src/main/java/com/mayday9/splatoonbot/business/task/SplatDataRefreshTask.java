package com.mayday9.splatoonbot.business.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.util.StringUtils;
import com.mayday9.splatoonbot.business.constants.SplatoonConstant;
import com.mayday9.splatoonbot.business.dto.TSplatMatchInfoAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatMatchInfoDetailAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatSalmonRunInfoAddDTO;
import com.mayday9.splatoonbot.business.dto.TSplatSalmonRunInfoWeaponAddDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.SplatDataDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.bankara.SplatBankaraSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.bankara.SplatBankaraSchedulesMatchSettingDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.bankara.SplatBankaraSchedulesNodeDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.comm.SplatVsRuleCommDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.comm.SplatVsStageCommDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.fest.SplatFestSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.fest.SplatFestSchedulesMatchSettingDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.fest.SplatFestSchedulesNodeDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.regular.SplatRegularSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.regular.SplatRegularSchedulesMatchSettingDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.regular.SplatRegularSchedulesNodeDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SalmonRunBossDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SalmonRunCoopStageDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SalmonRunWeaponDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SplatCoopGroupingScheduleDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SplatSalmonRunSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SplatSalmonRunSchedulesNodeDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.salmonrun.SplatSalmonRunSchedulesNodeSettingDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.vsstage.SplatVsStagesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.vsstage.SplatVsStagesNodeDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.x.SplatXSchedulesDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.x.SplatXSchedulesMatchSettingDTO;
import com.mayday9.splatoonbot.business.dto.splatoon.x.SplatXSchedulesNodeDTO;
import com.mayday9.splatoonbot.business.entity.TLangCn;
import com.mayday9.splatoonbot.business.entity.TSplatVsStage;
import com.mayday9.splatoonbot.business.entity.TSysFile;
import com.mayday9.splatoonbot.business.infrastructure.dao.TLangCnDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSplatVsStageDao;
import com.mayday9.splatoonbot.business.infrastructure.dao.TSysFileDao;
import com.mayday9.splatoonbot.business.service.IFileUploadService;
import com.mayday9.splatoonbot.business.service.TSplatMatchInfoService;
import com.mayday9.splatoonbot.business.vo.UploadFileVO;
import com.mayday9.splatoonbot.common.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Lianjiannan
 * @since 2024/9/19 9:23
 **/
@Component
@Slf4j
public class SplatDataRefreshTask {

    @Resource
    private TLangCnDao tLangCnDao;

    @Resource
    private TSplatVsStageDao tSplatVsStageDao;


    @Resource
    private TSplatMatchInfoService tSplatMatchInfoService;

    @Resource
    private IFileUploadService fileUploadService;

    @Resource
    private TSysFileDao tSysFileDao;

    private static final String DATA_URL = "https://splatoon3.ink/data/schedules.json";

    private static final String LANG_CN_URL = "https://splatoon3.ink/data/locale/zh-CN.json";


    private static final String JSON_DATA = "{\"data\":{\"regularSchedules\":{\"nodes\":[{\"startTime\":\"2024-10-11T08:00:00Z\",\"endTime\":\"2024-10-11T10:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":15,\"name\":\"MakoMart\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/a8ba96c3dbd015b7bc6ea4fa067245c4e9aee62b6696cb41e02d35139dd21fe7_1.png\"},\"id\":\"VnNTdGFnZS0xNQ==\"},{\"vsStageId\":24,\"name\":\"Lemuria Hub\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/2ba481293efc554ac217f21b6d56dd08f9d66e72b286f20714abd5ef1520f47a_1.png\"},\"id\":\"VnNTdGFnZS0yNA==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T10:00:00Z\",\"endTime\":\"2024-10-11T12:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":4,\"name\":\"Undertow Spillway\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/9b1c17b2075479d0397d2fb96efbc6fa3a28900712920e5fe1e9dfc59c6abc5c_1.png\"},\"id\":\"VnNTdGFnZS00\"},{\"vsStageId\":11,\"name\":\"Museum d'Alfonsino\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b9d8cfa186d197a27e075600a107c99d9e21646d116730f0843e0fff0aaba7dd_1.png\"},\"id\":\"VnNTdGFnZS0xMQ==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T12:00:00Z\",\"endTime\":\"2024-10-11T14:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":17,\"name\":\"Humpback Pump Track\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/7b3cf118bd9f45d141cd6db0ee75b06e697fa83945c7fe1e6f8483de6a591f5f_1.png\"},\"id\":\"VnNTdGFnZS0xNw==\"},{\"vsStageId\":19,\"name\":\"Crableg Capital\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/4e0e9e2046aff1d635e23946d9f0a461486d2aab349079e551037e426ac82c7a_1.png\"},\"id\":\"VnNTdGFnZS0xOQ==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T14:00:00Z\",\"endTime\":\"2024-10-11T16:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":9,\"name\":\"Flounder Heights\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/488017f3ce712fca9fb37d61fe306343054449bb2d2bb1751d95f54a98564cae_1.png\"},\"id\":\"VnNTdGFnZS05\"},{\"vsStageId\":16,\"name\":\"Wahoo World\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/61ea801fa4ed32360dcaf83986222ded46a72dbf56194acc6d0cf4659a92ba85_1.png\"},\"id\":\"VnNTdGFnZS0xNg==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T16:00:00Z\",\"endTime\":\"2024-10-11T18:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":1,\"name\":\"Scorch Gorge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_1.png\"},\"id\":\"VnNTdGFnZS0x\"},{\"vsStageId\":5,\"name\":\"Um'ami Ruins\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f14c2a64e49d243679fc0884af91e1a07dc65600f9b90aefe92d7790dcffb191_1.png\"},\"id\":\"VnNTdGFnZS01\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T18:00:00Z\",\"endTime\":\"2024-10-11T20:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":10,\"name\":\"Hammerhead Bridge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/1db8ab338b64b464df50e7f9e270e59423ff8caac6f09679a24f1b7acf3a82f3_1.png\"},\"id\":\"VnNTdGFnZS0xMA==\"},{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T20:00:00Z\",\"endTime\":\"2024-10-11T22:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":2,\"name\":\"Eeltail Alley\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/898e1ae6c737a9d44552c7c81f9b710676492681525c514eadc68a6780aa52af_1.png\"},\"id\":\"VnNTdGFnZS0y\"},{\"vsStageId\":21,\"name\":\"Robo ROM-en\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/692365fa7e56cf19cfa403a8546e69cf60fd9ca2171bde66cdaa53dc0e736ac9_1.png\"},\"id\":\"VnNTdGFnZS0yMQ==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T22:00:00Z\",\"endTime\":\"2024-10-12T00:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":20,\"name\":\"Shipshape Cargo Co.\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b65a70eedf129848b9d2b492bb68712abf91a5cbda13131848947fb04c51d665_1.png\"},\"id\":\"VnNTdGFnZS0yMA==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T00:00:00Z\",\"endTime\":\"2024-10-12T02:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":5,\"name\":\"Um'ami Ruins\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f14c2a64e49d243679fc0884af91e1a07dc65600f9b90aefe92d7790dcffb191_1.png\"},\"id\":\"VnNTdGFnZS01\"},{\"vsStageId\":16,\"name\":\"Wahoo World\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/61ea801fa4ed32360dcaf83986222ded46a72dbf56194acc6d0cf4659a92ba85_1.png\"},\"id\":\"VnNTdGFnZS0xNg==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T02:00:00Z\",\"endTime\":\"2024-10-12T04:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":1,\"name\":\"Scorch Gorge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_1.png\"},\"id\":\"VnNTdGFnZS0x\"},{\"vsStageId\":8,\"name\":\"Barnacle & Dime\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f70e9f5af477a39ccfab631bfb81c9e2cedb4cd0947fe260847c214a6d23695f_1.png\"},\"id\":\"VnNTdGFnZS04\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T04:00:00Z\",\"endTime\":\"2024-10-12T06:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"},{\"vsStageId\":17,\"name\":\"Humpback Pump Track\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/7b3cf118bd9f45d141cd6db0ee75b06e697fa83945c7fe1e6f8483de6a591f5f_1.png\"},\"id\":\"VnNTdGFnZS0xNw==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T06:00:00Z\",\"endTime\":\"2024-10-12T08:00:00Z\",\"regularMatchSetting\":{\"__isVsSetting\":\"RegularMatchSetting\",\"__typename\":\"RegularMatchSetting\",\"vsStages\":[{\"vsStageId\":6,\"name\":\"Mincemeat Metalworks\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/de1f212e9ff0648f36cd3b8e0917ef36b3bd51445159297dcb948f34a09f2f05_1.png\"},\"id\":\"VnNTdGFnZS02\"},{\"vsStageId\":15,\"name\":\"MakoMart\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/a8ba96c3dbd015b7bc6ea4fa067245c4e9aee62b6696cb41e02d35139dd21fe7_1.png\"},\"id\":\"VnNTdGFnZS0xNQ==\"}],\"vsRule\":{\"name\":\"Turf War\",\"rule\":\"TURF_WAR\",\"id\":\"VnNSdWxlLTA=\"}},\"festMatchSettings\":null}]},\"bankaraSchedules\":{\"nodes\":[{\"startTime\":\"2024-10-11T08:00:00Z\",\"endTime\":\"2024-10-11T10:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":7,\"name\":\"Brinewater Springs\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/cd84d711b47a424334569ac20f33f8e0ab6a652dc07854dcd36508a0081e9034_1.png\"},\"id\":\"VnNTdGFnZS03\"},{\"vsStageId\":17,\"name\":\"Humpback Pump Track\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/7b3cf118bd9f45d141cd6db0ee75b06e697fa83945c7fe1e6f8483de6a591f5f_1.png\"},\"id\":\"VnNTdGFnZS0xNw==\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":1,\"name\":\"Scorch Gorge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_1.png\"},\"id\":\"VnNTdGFnZS0x\"},{\"vsStageId\":22,\"name\":\"Bluefin Depot\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/3e468df1f38d6323cc8d9446049696d66e47831e68cd31032502349b11960500_1.png\"},\"id\":\"VnNTdGFnZS0yMg==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T10:00:00Z\",\"endTime\":\"2024-10-11T12:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":2,\"name\":\"Eeltail Alley\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/898e1ae6c737a9d44552c7c81f9b710676492681525c514eadc68a6780aa52af_1.png\"},\"id\":\"VnNTdGFnZS0y\"},{\"vsStageId\":13,\"name\":\"Inkblot Art Academy\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/40aba8b36a9439e2d670fde5b3478819ea8a94f9e503b9d783248a5716786f35_1.png\"},\"id\":\"VnNTdGFnZS0xMw==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":7,\"name\":\"Brinewater Springs\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/cd84d711b47a424334569ac20f33f8e0ab6a652dc07854dcd36508a0081e9034_1.png\"},\"id\":\"VnNTdGFnZS03\"},{\"vsStageId\":9,\"name\":\"Flounder Heights\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/488017f3ce712fca9fb37d61fe306343054449bb2d2bb1751d95f54a98564cae_1.png\"},\"id\":\"VnNTdGFnZS05\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T12:00:00Z\",\"endTime\":\"2024-10-11T14:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":6,\"name\":\"Mincemeat Metalworks\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/de1f212e9ff0648f36cd3b8e0917ef36b3bd51445159297dcb948f34a09f2f05_1.png\"},\"id\":\"VnNTdGFnZS02\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":13,\"name\":\"Inkblot Art Academy\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/40aba8b36a9439e2d670fde5b3478819ea8a94f9e503b9d783248a5716786f35_1.png\"},\"id\":\"VnNTdGFnZS0xMw==\"},{\"vsStageId\":14,\"name\":\"Sturgeon Shipyard\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/48684c69d5c5a4ffaf16b712a4895545a8d01196115d514fc878ce99863bb3e9_1.png\"},\"id\":\"VnNTdGFnZS0xNA==\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T14:00:00Z\",\"endTime\":\"2024-10-11T16:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":15,\"name\":\"MakoMart\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/a8ba96c3dbd015b7bc6ea4fa067245c4e9aee62b6696cb41e02d35139dd21fe7_1.png\"},\"id\":\"VnNTdGFnZS0xNQ==\"},{\"vsStageId\":21,\"name\":\"Robo ROM-en\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/692365fa7e56cf19cfa403a8546e69cf60fd9ca2171bde66cdaa53dc0e736ac9_1.png\"},\"id\":\"VnNTdGFnZS0yMQ==\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":4,\"name\":\"Undertow Spillway\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/9b1c17b2075479d0397d2fb96efbc6fa3a28900712920e5fe1e9dfc59c6abc5c_1.png\"},\"id\":\"VnNTdGFnZS00\"},{\"vsStageId\":10,\"name\":\"Hammerhead Bridge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/1db8ab338b64b464df50e7f9e270e59423ff8caac6f09679a24f1b7acf3a82f3_1.png\"},\"id\":\"VnNTdGFnZS0xMA==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T16:00:00Z\",\"endTime\":\"2024-10-11T18:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":23,\"name\":\"Marlin Airport\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/21ea0549b3d29de56ec20affef8866297929d0defb82a27cd199c9d8dade508c_1.png\"},\"id\":\"VnNTdGFnZS0yMw==\"},{\"vsStageId\":24,\"name\":\"Lemuria Hub\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/2ba481293efc554ac217f21b6d56dd08f9d66e72b286f20714abd5ef1520f47a_1.png\"},\"id\":\"VnNTdGFnZS0yNA==\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":2,\"name\":\"Eeltail Alley\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/898e1ae6c737a9d44552c7c81f9b710676492681525c514eadc68a6780aa52af_1.png\"},\"id\":\"VnNTdGFnZS0y\"},{\"vsStageId\":19,\"name\":\"Crableg Capital\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/4e0e9e2046aff1d635e23946d9f0a461486d2aab349079e551037e426ac82c7a_1.png\"},\"id\":\"VnNTdGFnZS0xOQ==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T18:00:00Z\",\"endTime\":\"2024-10-11T20:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":9,\"name\":\"Flounder Heights\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/488017f3ce712fca9fb37d61fe306343054449bb2d2bb1751d95f54a98564cae_1.png\"},\"id\":\"VnNTdGFnZS05\"},{\"vsStageId\":19,\"name\":\"Crableg Capital\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/4e0e9e2046aff1d635e23946d9f0a461486d2aab349079e551037e426ac82c7a_1.png\"},\"id\":\"VnNTdGFnZS0xOQ==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":11,\"name\":\"Museum d'Alfonsino\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b9d8cfa186d197a27e075600a107c99d9e21646d116730f0843e0fff0aaba7dd_1.png\"},\"id\":\"VnNTdGFnZS0xMQ==\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T20:00:00Z\",\"endTime\":\"2024-10-11T22:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":5,\"name\":\"Um'ami Ruins\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f14c2a64e49d243679fc0884af91e1a07dc65600f9b90aefe92d7790dcffb191_1.png\"},\"id\":\"VnNTdGFnZS01\"},{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":8,\"name\":\"Barnacle & Dime\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f70e9f5af477a39ccfab631bfb81c9e2cedb4cd0947fe260847c214a6d23695f_1.png\"},\"id\":\"VnNTdGFnZS04\"},{\"vsStageId\":20,\"name\":\"Shipshape Cargo Co.\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b65a70eedf129848b9d2b492bb68712abf91a5cbda13131848947fb04c51d665_1.png\"},\"id\":\"VnNTdGFnZS0yMA==\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T22:00:00Z\",\"endTime\":\"2024-10-12T00:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":13,\"name\":\"Inkblot Art Academy\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/40aba8b36a9439e2d670fde5b3478819ea8a94f9e503b9d783248a5716786f35_1.png\"},\"id\":\"VnNTdGFnZS0xMw==\"},{\"vsStageId\":18,\"name\":\"Manta Maria\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/0b7fd997781e03eb9d5bf1875ed070f698afc654f4fe929452c65aa26c0a35fd_1.png\"},\"id\":\"VnNTdGFnZS0xOA==\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":1,\"name\":\"Scorch Gorge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_1.png\"},\"id\":\"VnNTdGFnZS0x\"},{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T00:00:00Z\",\"endTime\":\"2024-10-12T02:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":11,\"name\":\"Museum d'Alfonsino\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b9d8cfa186d197a27e075600a107c99d9e21646d116730f0843e0fff0aaba7dd_1.png\"},\"id\":\"VnNTdGFnZS0xMQ==\"},{\"vsStageId\":20,\"name\":\"Shipshape Cargo Co.\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b65a70eedf129848b9d2b492bb68712abf91a5cbda13131848947fb04c51d665_1.png\"},\"id\":\"VnNTdGFnZS0yMA==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":21,\"name\":\"Robo ROM-en\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/692365fa7e56cf19cfa403a8546e69cf60fd9ca2171bde66cdaa53dc0e736ac9_1.png\"},\"id\":\"VnNTdGFnZS0yMQ==\"},{\"vsStageId\":22,\"name\":\"Bluefin Depot\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/3e468df1f38d6323cc8d9446049696d66e47831e68cd31032502349b11960500_1.png\"},\"id\":\"VnNTdGFnZS0yMg==\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T02:00:00Z\",\"endTime\":\"2024-10-12T04:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":4,\"name\":\"Undertow Spillway\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/9b1c17b2075479d0397d2fb96efbc6fa3a28900712920e5fe1e9dfc59c6abc5c_1.png\"},\"id\":\"VnNTdGFnZS00\"},{\"vsStageId\":16,\"name\":\"Wahoo World\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/61ea801fa4ed32360dcaf83986222ded46a72dbf56194acc6d0cf4659a92ba85_1.png\"},\"id\":\"VnNTdGFnZS0xNg==\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":6,\"name\":\"Mincemeat Metalworks\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/de1f212e9ff0648f36cd3b8e0917ef36b3bd51445159297dcb948f34a09f2f05_1.png\"},\"id\":\"VnNTdGFnZS02\"},{\"vsStageId\":20,\"name\":\"Shipshape Cargo Co.\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b65a70eedf129848b9d2b492bb68712abf91a5cbda13131848947fb04c51d665_1.png\"},\"id\":\"VnNTdGFnZS0yMA==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T04:00:00Z\",\"endTime\":\"2024-10-12T06:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":20,\"name\":\"Shipshape Cargo Co.\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b65a70eedf129848b9d2b492bb68712abf91a5cbda13131848947fb04c51d665_1.png\"},\"id\":\"VnNTdGFnZS0yMA==\"},{\"vsStageId\":24,\"name\":\"Lemuria Hub\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/2ba481293efc554ac217f21b6d56dd08f9d66e72b286f20714abd5ef1520f47a_1.png\"},\"id\":\"VnNTdGFnZS0yNA==\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":21,\"name\":\"Robo ROM-en\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/692365fa7e56cf19cfa403a8546e69cf60fd9ca2171bde66cdaa53dc0e736ac9_1.png\"},\"id\":\"VnNTdGFnZS0yMQ==\"},{\"vsStageId\":23,\"name\":\"Marlin Airport\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/21ea0549b3d29de56ec20affef8866297929d0defb82a27cd199c9d8dade508c_1.png\"},\"id\":\"VnNTdGFnZS0yMw==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T06:00:00Z\",\"endTime\":\"2024-10-12T08:00:00Z\",\"bankaraMatchSettings\":[{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":7,\"name\":\"Brinewater Springs\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/cd84d711b47a424334569ac20f33f8e0ab6a652dc07854dcd36508a0081e9034_1.png\"},\"id\":\"VnNTdGFnZS03\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"},\"bankaraMode\":\"CHALLENGE\"},{\"__isVsSetting\":\"BankaraMatchSetting\",\"__typename\":\"BankaraMatchSetting\",\"vsStages\":[{\"vsStageId\":11,\"name\":\"Museum d'Alfonsino\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b9d8cfa186d197a27e075600a107c99d9e21646d116730f0843e0fff0aaba7dd_1.png\"},\"id\":\"VnNTdGFnZS0xMQ==\"},{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"},\"bankaraMode\":\"OPEN\"}],\"festMatchSettings\":null}]},\"xSchedules\":{\"nodes\":[{\"startTime\":\"2024-10-11T08:00:00Z\",\"endTime\":\"2024-10-11T10:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"},{\"vsStageId\":23,\"name\":\"Marlin Airport\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/21ea0549b3d29de56ec20affef8866297929d0defb82a27cd199c9d8dade508c_1.png\"},\"id\":\"VnNTdGFnZS0yMw==\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T10:00:00Z\",\"endTime\":\"2024-10-11T12:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":15,\"name\":\"MakoMart\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/a8ba96c3dbd015b7bc6ea4fa067245c4e9aee62b6696cb41e02d35139dd21fe7_1.png\"},\"id\":\"VnNTdGFnZS0xNQ==\"},{\"vsStageId\":24,\"name\":\"Lemuria Hub\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/2ba481293efc554ac217f21b6d56dd08f9d66e72b286f20714abd5ef1520f47a_1.png\"},\"id\":\"VnNTdGFnZS0yNA==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T12:00:00Z\",\"endTime\":\"2024-10-11T14:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":11,\"name\":\"Museum d'Alfonsino\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/b9d8cfa186d197a27e075600a107c99d9e21646d116730f0843e0fff0aaba7dd_1.png\"},\"id\":\"VnNTdGFnZS0xMQ==\"},{\"vsStageId\":16,\"name\":\"Wahoo World\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/61ea801fa4ed32360dcaf83986222ded46a72dbf56194acc6d0cf4659a92ba85_1.png\"},\"id\":\"VnNTdGFnZS0xNg==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T14:00:00Z\",\"endTime\":\"2024-10-11T16:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":1,\"name\":\"Scorch Gorge\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_1.png\"},\"id\":\"VnNTdGFnZS0x\"},{\"vsStageId\":5,\"name\":\"Um'ami Ruins\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f14c2a64e49d243679fc0884af91e1a07dc65600f9b90aefe92d7790dcffb191_1.png\"},\"id\":\"VnNTdGFnZS01\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T16:00:00Z\",\"endTime\":\"2024-10-11T18:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":4,\"name\":\"Undertow Spillway\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/9b1c17b2075479d0397d2fb96efbc6fa3a28900712920e5fe1e9dfc59c6abc5c_1.png\"},\"id\":\"VnNTdGFnZS00\"},{\"vsStageId\":8,\"name\":\"Barnacle & Dime\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f70e9f5af477a39ccfab631bfb81c9e2cedb4cd0947fe260847c214a6d23695f_1.png\"},\"id\":\"VnNTdGFnZS04\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T18:00:00Z\",\"endTime\":\"2024-10-11T20:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":6,\"name\":\"Mincemeat Metalworks\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/de1f212e9ff0648f36cd3b8e0917ef36b3bd51445159297dcb948f34a09f2f05_1.png\"},\"id\":\"VnNTdGFnZS02\"},{\"vsStageId\":14,\"name\":\"Sturgeon Shipyard\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/48684c69d5c5a4ffaf16b712a4895545a8d01196115d514fc878ce99863bb3e9_1.png\"},\"id\":\"VnNTdGFnZS0xNA==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T20:00:00Z\",\"endTime\":\"2024-10-11T22:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":18,\"name\":\"Manta Maria\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/0b7fd997781e03eb9d5bf1875ed070f698afc654f4fe929452c65aa26c0a35fd_1.png\"},\"id\":\"VnNTdGFnZS0xOA==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-11T22:00:00Z\",\"endTime\":\"2024-10-12T00:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":2,\"name\":\"Eeltail Alley\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/898e1ae6c737a9d44552c7c81f9b710676492681525c514eadc68a6780aa52af_1.png\"},\"id\":\"VnNTdGFnZS0y\"},{\"vsStageId\":9,\"name\":\"Flounder Heights\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/488017f3ce712fca9fb37d61fe306343054449bb2d2bb1751d95f54a98564cae_1.png\"},\"id\":\"VnNTdGFnZS05\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T00:00:00Z\",\"endTime\":\"2024-10-12T02:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":8,\"name\":\"Barnacle & Dime\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f70e9f5af477a39ccfab631bfb81c9e2cedb4cd0947fe260847c214a6d23695f_1.png\"},\"id\":\"VnNTdGFnZS04\"},{\"vsStageId\":15,\"name\":\"MakoMart\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/a8ba96c3dbd015b7bc6ea4fa067245c4e9aee62b6696cb41e02d35139dd21fe7_1.png\"},\"id\":\"VnNTdGFnZS0xNQ==\"}],\"vsRule\":{\"name\":\"Tower Control\",\"rule\":\"LOFT\",\"id\":\"VnNSdWxlLTI=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T02:00:00Z\",\"endTime\":\"2024-10-12T04:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":2,\"name\":\"Eeltail Alley\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/898e1ae6c737a9d44552c7c81f9b710676492681525c514eadc68a6780aa52af_1.png\"},\"id\":\"VnNTdGFnZS0y\"},{\"vsStageId\":5,\"name\":\"Um'ami Ruins\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/f14c2a64e49d243679fc0884af91e1a07dc65600f9b90aefe92d7790dcffb191_1.png\"},\"id\":\"VnNTdGFnZS01\"}],\"vsRule\":{\"name\":\"Rainmaker\",\"rule\":\"GOAL\",\"id\":\"VnNSdWxlLTM=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T04:00:00Z\",\"endTime\":\"2024-10-12T06:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":6,\"name\":\"Mincemeat Metalworks\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/de1f212e9ff0648f36cd3b8e0917ef36b3bd51445159297dcb948f34a09f2f05_1.png\"},\"id\":\"VnNTdGFnZS02\"}],\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"}},\"festMatchSettings\":null},{\"startTime\":\"2024-10-12T06:00:00Z\",\"endTime\":\"2024-10-12T08:00:00Z\",\"xMatchSetting\":{\"__isVsSetting\":\"XMatchSetting\",\"__typename\":\"XMatchSetting\",\"vsStages\":[{\"vsStageId\":9,\"name\":\"Flounder Heights\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/488017f3ce712fca9fb37d61fe306343054449bb2d2bb1751d95f54a98564cae_1.png\"},\"id\":\"VnNTdGFnZS05\"},{\"vsStageId\":17,\"name\":\"Humpback Pump Track\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/7b3cf118bd9f45d141cd6db0ee75b06e697fa83945c7fe1e6f8483de6a591f5f_1.png\"},\"id\":\"VnNTdGFnZS0xNw==\"}],\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"}},\"festMatchSettings\":null}]},\"eventSchedules\":{\"nodes\":[{\"leagueMatchSetting\":{\"leagueMatchEvent\":{\"leagueMatchEventId\":\"CompetitionsJP\",\"name\":\"Tournament Qualifiers (Japan)\",\"desc\":\"Play your way into a tournament!\",\"regulationUrl\":\"https://www.nintendo.com/jp/topics/article/c0ac3d50-bd1a-4345-80e0-6656eaf6384b?utm_source=SplatNet3&utm_medium=schedule&utm_campaign=202410_gachiking\",\"regulation\":\"This type of Challenge is a qualifier for a tournament in the Japan region. However, players who do not plan on participating in the tournament can still participate in this type of Challenge.<br /><br />A separate application may be required to participate in tournaments, regardless of qualification status. Additionally, participation may be restricted depending on a player's country of residence or other factors.\",\"id\":\"TGVhZ3VlTWF0Y2hFdmVudC1Db21wZXRpdGlvbnNKUA==\"},\"vsStages\":[{\"vsStageId\":4,\"name\":\"Undertow Spillway\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/9b1c17b2075479d0397d2fb96efbc6fa3a28900712920e5fe1e9dfc59c6abc5c_1.png\"},\"id\":\"VnNTdGFnZS00\"},{\"vsStageId\":24,\"name\":\"Lemuria Hub\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/2ba481293efc554ac217f21b6d56dd08f9d66e72b286f20714abd5ef1520f47a_1.png\"},\"id\":\"VnNTdGFnZS0yNA==\"}],\"__isVsSetting\":\"LeagueMatchSetting\",\"__typename\":\"LeagueMatchSetting\",\"vsRule\":{\"name\":\"Clam Blitz\",\"rule\":\"CLAM\",\"id\":\"VnNSdWxlLTQ=\"}},\"timePeriods\":[{\"startTime\":\"2024-10-12T10:00:00Z\",\"endTime\":\"2024-10-12T12:00:00Z\"}]},{\"leagueMatchSetting\":{\"leagueMatchEvent\":{\"leagueMatchEventId\":\"MonthlyLeagueMatchReal\",\"name\":\"Monthly Challenge\",\"desc\":\"Aim for the highest Challenge Power!\",\"regulationUrl\":null,\"regulation\":\"How high can you get your Challenge Power this month?<br /><br />・ Only primary gear abilities will be enabled in Turf War battles. Secondary gear abilities will have no effect!<br />・ There will be no limits on gear abilities in Anarchy Battle modes!\",\"id\":\"TGVhZ3VlTWF0Y2hFdmVudC1Nb250aGx5TGVhZ3VlTWF0Y2hSZWFs\"},\"vsStages\":[{\"vsStageId\":3,\"name\":\"Hagglefish Market\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_1.png\"},\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":12,\"name\":\"Mahi-Mahi Resort\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_1.png\"},\"id\":\"VnNTdGFnZS0xMg==\"}],\"__isVsSetting\":\"LeagueMatchSetting\",\"__typename\":\"LeagueMatchSetting\",\"vsRule\":{\"name\":\"Splat Zones\",\"rule\":\"AREA\",\"id\":\"VnNSdWxlLTE=\"}},\"timePeriods\":[{\"startTime\":\"2024-10-13T00:00:00Z\",\"endTime\":\"2024-10-13T02:00:00Z\"},{\"startTime\":\"2024-10-13T04:00:00Z\",\"endTime\":\"2024-10-13T06:00:00Z\"},{\"startTime\":\"2024-10-13T08:00:00Z\",\"endTime\":\"2024-10-13T10:00:00Z\"},{\"startTime\":\"2024-10-13T12:00:00Z\",\"endTime\":\"2024-10-13T14:00:00Z\"},{\"startTime\":\"2024-10-13T16:00:00Z\",\"endTime\":\"2024-10-13T18:00:00Z\"},{\"startTime\":\"2024-10-13T20:00:00Z\",\"endTime\":\"2024-10-13T22:00:00Z\"}]}]},\"festSchedules\":{\"nodes\":[{\"startTime\":\"2024-10-11T08:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T10:00:00Z\"},{\"startTime\":\"2024-10-11T10:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T12:00:00Z\"},{\"startTime\":\"2024-10-11T12:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T14:00:00Z\"},{\"startTime\":\"2024-10-11T14:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T16:00:00Z\"},{\"startTime\":\"2024-10-11T16:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T18:00:00Z\"},{\"startTime\":\"2024-10-11T18:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T20:00:00Z\"},{\"startTime\":\"2024-10-11T20:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-11T22:00:00Z\"},{\"startTime\":\"2024-10-11T22:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-12T00:00:00Z\"},{\"startTime\":\"2024-10-12T00:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-12T02:00:00Z\"},{\"startTime\":\"2024-10-12T02:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-12T04:00:00Z\"},{\"startTime\":\"2024-10-12T04:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-12T06:00:00Z\"},{\"startTime\":\"2024-10-12T06:00:00Z\",\"festMatchSettings\":null,\"endTime\":\"2024-10-12T08:00:00Z\"}]},\"coopGroupingSchedule\":{\"bannerImage\":null,\"regularSchedules\":{\"nodes\":[{\"startTime\":\"2024-10-10T16:00:00Z\",\"endTime\":\"2024-10-12T08:00:00Z\",\"setting\":{\"__typename\":\"CoopNormalSetting\",\"boss\":{\"name\":\"Horrorboros\",\"id\":\"Q29vcEVuZW15LTI0\"},\"coopStage\":{\"name\":\"Jammin' Salmon Junction\",\"thumbnailImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/0e05d4caa34089a447535708370286f4ee6068661359b4d7cf6c319863424f84_1.png\"},\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/0e05d4caa34089a447535708370286f4ee6068661359b4d7cf6c319863424f84_0.png\"},\"id\":\"Q29vcFN0YWdlLTg=\"},\"__isCoopSetting\":\"CoopNormalSetting\",\"weapons\":[{\"__splatoon3ink_id\":\"7065ab46c609e6d7\",\"name\":\"Douser Dualies FF\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/d6d8c3bce9bd3934a5900642cb6f87c7e340e39cccfde1f8f28ce17e3a1769b0_0.png\"}},{\"__splatoon3ink_id\":\"5e5481f626f02cbf\",\"name\":\"H-3 Nozzlenose\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/418d75d9ca0304922f06eff539c511238b143ef8331969e20d54a9560df57d5a_0.png\"}},{\"__splatoon3ink_id\":\"eb5098db38e209e1\",\"name\":\"Bloblobber\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/0199e455872acba1ab8ef0040eca7f41afca48c1f9ad2c5d274323d6dbc49133_0.png\"}},{\"__splatoon3ink_id\":\"c100f88e8b925e1c\",\"name\":\"Hydra Splatling\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/34fe0401b6f6a0b09839696fc820ece9570a9d56e3a746b65f0604dec91a9920_0.png\"}}]},\"__splatoon3ink_king_salmonid_guess\":\"Horrorboros\"},{\"startTime\":\"2024-10-12T08:00:00Z\",\"endTime\":\"2024-10-14T00:00:00Z\",\"setting\":{\"__typename\":\"CoopNormalSetting\",\"boss\":{\"name\":\"Megalodontia\",\"id\":\"Q29vcEVuZW15LTI1\"},\"coopStage\":{\"name\":\"Spawning Grounds\",\"thumbnailImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/be584c7c7f547b8cbac318617f646680541f88071bc71db73cd461eb3ea6326e_1.png\"},\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/be584c7c7f547b8cbac318617f646680541f88071bc71db73cd461eb3ea6326e_0.png\"},\"id\":\"Q29vcFN0YWdlLTE=\"},\"__isCoopSetting\":\"CoopNormalSetting\",\"weapons\":[{\"__splatoon3ink_id\":\"b4ebb5629d8c3e5f\",\"name\":\"Undercover Brella\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/7508ba286e5ac5befe63daea807ab54996c3f0ef3577be9ab5d2827c49dedd75_0.png\"}},{\"__splatoon3ink_id\":\"3ab1a91f59f81345\",\"name\":\"Painbrush\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/c1f1f56982bd7d28714615a69da6e33c5157ec22b1c62092ec8d60a67b6b29ef_0.png\"}},{\"__splatoon3ink_id\":\"5567ca94a5374d21\",\"name\":\"Sploosh-o-matic\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/6e58a0747ab899badcb6f351512c6034e0a49bd6453281f32c7f550a2132fd65_0.png\"}},{\"__splatoon3ink_id\":\"9e45e49e1e9c4853\",\"name\":\"Jet Squelcher\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/035920eb9428955c25aecb8a56c2b1b58f3e322af3657d921db1778de4b80c59_0.png\"}}]},\"__splatoon3ink_king_salmonid_guess\":\"Megalodontia\"},{\"startTime\":\"2024-10-14T00:00:00Z\",\"endTime\":\"2024-10-15T16:00:00Z\",\"setting\":{\"__typename\":\"CoopNormalSetting\",\"boss\":{\"name\":\"Cohozuna\",\"id\":\"Q29vcEVuZW15LTIz\"},\"coopStage\":{\"name\":\"Marooner's Bay\",\"thumbnailImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/1a29476c1ab5fdbc813e2df99cd290ce56dfe29755b97f671a7250e5f77f4961_1.png\"},\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/1a29476c1ab5fdbc813e2df99cd290ce56dfe29755b97f671a7250e5f77f4961_0.png\"},\"id\":\"Q29vcFN0YWdlLTY=\"},\"__isCoopSetting\":\"CoopNormalSetting\",\"weapons\":[{\"__splatoon3ink_id\":\"79e6297eb501599f\",\"name\":\"Dapple Dualies\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/f1c8fc32bd90fc9258dc17e9f9bcfd5e6498f6e283709bf1896b78193b8e39e9_0.png\"}},{\"__splatoon3ink_id\":\"fe3b9b3b87ca491e\",\"name\":\"Slosher\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/4a8bf6b4ad3b2942728bbd270bf64d5848b64f3c843a3b12ef83c0ebb5de1b3d_0.png\"}},{\"__splatoon3ink_id\":\"830801a9f4555917\",\"name\":\"Clash Blaster\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/be8ba95bd3017a83876e7f769ee37ee459ee4b2d6eca03fceeb058c510adbb61_0.png\"}},{\"__splatoon3ink_id\":\"2136716c07457658\",\"name\":\"Tri-Stringer\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/676d9f49276f171a93ac06646c0fbdfbeb8c3d0284a057aee306404a6034ffef_0.png\"}}]},\"__splatoon3ink_king_salmonid_guess\":\"Cohozuna\"},{\"startTime\":\"2024-10-15T16:00:00Z\",\"endTime\":\"2024-10-17T08:00:00Z\",\"setting\":{\"__typename\":\"CoopNormalSetting\",\"boss\":{\"name\":\"Horrorboros\",\"id\":\"Q29vcEVuZW15LTI0\"},\"coopStage\":{\"name\":\"Bonerattle Arena\",\"thumbnailImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/5f09625c9031652ca1edcf8028265b03ecc28475ab3d56910960a68430d7948a_1.png\"},\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/5f09625c9031652ca1edcf8028265b03ecc28475ab3d56910960a68430d7948a_0.png\"},\"id\":\"Q29vcFN0YWdlLTk=\"},\"__isCoopSetting\":\"CoopNormalSetting\",\"weapons\":[{\"__splatoon3ink_id\":\"c19e07d4db9a4075\",\"name\":\"Explosher\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/1e32f5e1e65793585f6423e4fcae1a146a79d2a09e6e15575015af8a2032a4fe_0.png\"}},{\"__splatoon3ink_id\":\"e60629ecf35192a1\",\"name\":\"Splattershot\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/e3874d7d504acf89488ad7f68d29a348caea1a41cd43bd9a272069b0c0466570_0.png\"}},{\"__splatoon3ink_id\":\"05a0ddd5b919b698\",\"name\":\"Luna Blaster\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/10d4a1584d1428cb164ddfbc5febc9b1e77fd05e2e9ed9de851838a94d202c15_0.png\"}},{\"__splatoon3ink_id\":\"3aac272457998973\",\"name\":\"Heavy Splatling\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/fd06f01742a3b25ac57941150b3b81d56633831902f2da1f19a6244f2d8dd6fd_0.png\"}}]},\"__splatoon3ink_king_salmonid_guess\":\"Horrorboros\"},{\"startTime\":\"2024-10-17T08:00:00Z\",\"endTime\":\"2024-10-19T00:00:00Z\",\"setting\":{\"__typename\":\"CoopNormalSetting\",\"boss\":{\"name\":\"Megalodontia\",\"id\":\"Q29vcEVuZW15LTI1\"},\"coopStage\":{\"name\":\"Spawning Grounds\",\"thumbnailImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/low_resolution/be584c7c7f547b8cbac318617f646680541f88071bc71db73cd461eb3ea6326e_1.png\"},\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/be584c7c7f547b8cbac318617f646680541f88071bc71db73cd461eb3ea6326e_0.png\"},\"id\":\"Q29vcFN0YWdlLTE=\"},\"__isCoopSetting\":\"CoopNormalSetting\",\"weapons\":[{\"__splatoon3ink_id\":\"ed647072fa99c73e\",\"name\":\"Splash-o-matic\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/25e98eaba1e17308db191b740d9b89e6a977bfcd37c8dc1d65883731c0c72609_0.png\"}},{\"__splatoon3ink_id\":\"439343ee49a5126b\",\"name\":\"Ballpoint Splatling\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/206dbf3b5dfc9962b6a783acf68a856f0c8fbf0c56257c2ca5c25d63198dd6e1_0.png\"}},{\"__splatoon3ink_id\":\"513c64dd6cd5e723\",\"name\":\".52 Gal\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/01e8399a3c56707b6e9f7500d3d583ba1d400eec06449d8fe047cda1956a4ccc_0.png\"}},{\"__splatoon3ink_id\":\"884321fa6165eb37\",\"name\":\"Rapid Blaster\",\"image\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/weapon_illust/0a929d514403d07e1543e638141ebace947ffd539f5f766b42f4d6577d40d7b8_0.png\"}}]},\"__splatoon3ink_king_salmonid_guess\":\"Megalodontia\"}]},\"bigRunSchedules\":{\"nodes\":[]},\"teamContestSchedules\":{\"nodes\":[]}},\"currentFest\":null,\"currentPlayer\":{\"userIcon\":{\"url\":\"https://cdn-image-e0d67c509fb203858ebcb2fe3f88c2aa.baas.nintendo.com/1/2066ebe78273022a\"}},\"vsStages\":{\"nodes\":[{\"vsStageId\":1,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/35f9ca08ccc2bf759774ab2cb886567c117b9287875ca92fb590c1294ddcdc1e_0.png\"},\"name\":\"Scorch Gorge\",\"stats\":null,\"id\":\"VnNTdGFnZS0x\"},{\"vsStageId\":2,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/898e1ae6c737a9d44552c7c81f9b710676492681525c514eadc68a6780aa52af_0.png\"},\"name\":\"Eeltail Alley\",\"stats\":null,\"id\":\"VnNTdGFnZS0y\"},{\"vsStageId\":3,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/8dc2f16d39c630bab40cead5b2485ca3559e829d0d3de0c2232c7a62fefb5fa9_0.png\"},\"name\":\"Hagglefish Market\",\"stats\":null,\"id\":\"VnNTdGFnZS0z\"},{\"vsStageId\":4,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/9b1c17b2075479d0397d2fb96efbc6fa3a28900712920e5fe1e9dfc59c6abc5c_0.png\"},\"name\":\"Undertow Spillway\",\"stats\":null,\"id\":\"VnNTdGFnZS00\"},{\"vsStageId\":5,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/f14c2a64e49d243679fc0884af91e1a07dc65600f9b90aefe92d7790dcffb191_0.png\"},\"name\":\"Um'ami Ruins\",\"stats\":null,\"id\":\"VnNTdGFnZS01\"},{\"vsStageId\":6,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/de1f212e9ff0648f36cd3b8e0917ef36b3bd51445159297dcb948f34a09f2f05_0.png\"},\"name\":\"Mincemeat Metalworks\",\"stats\":null,\"id\":\"VnNTdGFnZS02\"},{\"vsStageId\":7,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/cd84d711b47a424334569ac20f33f8e0ab6a652dc07854dcd36508a0081e9034_0.png\"},\"name\":\"Brinewater Springs\",\"stats\":null,\"id\":\"VnNTdGFnZS03\"},{\"vsStageId\":8,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/f70e9f5af477a39ccfab631bfb81c9e2cedb4cd0947fe260847c214a6d23695f_0.png\"},\"name\":\"Barnacle & Dime\",\"stats\":null,\"id\":\"VnNTdGFnZS04\"},{\"vsStageId\":9,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/488017f3ce712fca9fb37d61fe306343054449bb2d2bb1751d95f54a98564cae_0.png\"},\"name\":\"Flounder Heights\",\"stats\":null,\"id\":\"VnNTdGFnZS05\"},{\"vsStageId\":10,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/1db8ab338b64b464df50e7f9e270e59423ff8caac6f09679a24f1b7acf3a82f3_0.png\"},\"name\":\"Hammerhead Bridge\",\"stats\":null,\"id\":\"VnNTdGFnZS0xMA==\"},{\"vsStageId\":11,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/b9d8cfa186d197a27e075600a107c99d9e21646d116730f0843e0fff0aaba7dd_0.png\"},\"name\":\"Museum d'Alfonsino\",\"stats\":null,\"id\":\"VnNTdGFnZS0xMQ==\"},{\"vsStageId\":12,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/8273118c1ffe1bf6fe031c7d8c9795dab52632c9b76e8e9f01f644ac5ae0ccc0_0.png\"},\"name\":\"Mahi-Mahi Resort\",\"stats\":null,\"id\":\"VnNTdGFnZS0xMg==\"},{\"vsStageId\":13,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/40aba8b36a9439e2d670fde5b3478819ea8a94f9e503b9d783248a5716786f35_0.png\"},\"name\":\"Inkblot Art Academy\",\"stats\":null,\"id\":\"VnNTdGFnZS0xMw==\"},{\"vsStageId\":14,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/48684c69d5c5a4ffaf16b712a4895545a8d01196115d514fc878ce99863bb3e9_0.png\"},\"name\":\"Sturgeon Shipyard\",\"stats\":null,\"id\":\"VnNTdGFnZS0xNA==\"},{\"vsStageId\":15,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/a8ba96c3dbd015b7bc6ea4fa067245c4e9aee62b6696cb41e02d35139dd21fe7_0.png\"},\"name\":\"MakoMart\",\"stats\":null,\"id\":\"VnNTdGFnZS0xNQ==\"},{\"vsStageId\":16,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/61ea801fa4ed32360dcaf83986222ded46a72dbf56194acc6d0cf4659a92ba85_0.png\"},\"name\":\"Wahoo World\",\"stats\":null,\"id\":\"VnNTdGFnZS0xNg==\"},{\"vsStageId\":17,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/7b3cf118bd9f45d141cd6db0ee75b06e697fa83945c7fe1e6f8483de6a591f5f_0.png\"},\"name\":\"Humpback Pump Track\",\"stats\":null,\"id\":\"VnNTdGFnZS0xNw==\"},{\"vsStageId\":18,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/0b7fd997781e03eb9d5bf1875ed070f698afc654f4fe929452c65aa26c0a35fd_0.png\"},\"name\":\"Manta Maria\",\"stats\":null,\"id\":\"VnNTdGFnZS0xOA==\"},{\"vsStageId\":19,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/4e0e9e2046aff1d635e23946d9f0a461486d2aab349079e551037e426ac82c7a_0.png\"},\"name\":\"Crableg Capital\",\"stats\":null,\"id\":\"VnNTdGFnZS0xOQ==\"},{\"vsStageId\":20,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/b65a70eedf129848b9d2b492bb68712abf91a5cbda13131848947fb04c51d665_0.png\"},\"name\":\"Shipshape Cargo Co.\",\"stats\":null,\"id\":\"VnNTdGFnZS0yMA==\"},{\"vsStageId\":21,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/692365fa7e56cf19cfa403a8546e69cf60fd9ca2171bde66cdaa53dc0e736ac9_0.png\"},\"name\":\"Robo ROM-en\",\"stats\":null,\"id\":\"VnNTdGFnZS0yMQ==\"},{\"vsStageId\":22,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/3e468df1f38d6323cc8d9446049696d66e47831e68cd31032502349b11960500_0.png\"},\"name\":\"Bluefin Depot\",\"stats\":null,\"id\":\"VnNTdGFnZS0yMg==\"},{\"vsStageId\":23,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/21ea0549b3d29de56ec20affef8866297929d0defb82a27cd199c9d8dade508c_0.png\"},\"name\":\"Marlin Airport\",\"stats\":null,\"id\":\"VnNTdGFnZS0yMw==\"},{\"vsStageId\":24,\"originalImage\":{\"url\":\"https://splatoon3.ink/assets/splatnet/v3/stage_img/icon/high_resolution/2ba481293efc554ac217f21b6d56dd08f9d66e72b286f20714abd5ef1520f47a_0.png\"},\"name\":\"Lemuria Hub\",\"stats\":null,\"id\":\"VnNTdGFnZS0yNA==\"}]}}}";


    @Transactional(rollbackFor = Exception.class)
    public void invokeSplatDataRefreshOnce() throws Exception {
        this.invokeSplatDataRefresh();
    }

    /**
     * 每天0点、2点、4点、6点、8点、10点、12点、14点、16点、18点、20点、22点执行
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 0,2,4,6,8,10,12,14,16,18,20,22 * * ?")
    @Retryable(value = Exception.class, maxAttempts = 15, backoff = @Backoff(delay = 2000, multiplier = 1))
    public void invokeSplatDataRefresh() throws Exception {
        log.info("执行Splatoon数据刷新任务...");
        // 获取数据
        String result = OkHttpUtil.builder()
            .url(DATA_URL)
            .addHeader(":authority", "splatoon3.ink")
            .addHeader(":method", "GET")
            .addHeader(":path", "/data/schedules.json")
            .addHeader(":scheme", "https")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;g=0.9,image/avif,image/webp,image/apng,*/*;g=0.8,application/signed-exchange;v=b3;q=0.7")
            .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
            .addHeader("Accept-Language", "zh-CN,zh;g=0.9")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Pragma", "no-cache")
            .addHeader("Priority", "u=0,i")
            .addHeader("Sec-Ch-Ua", "\"Chromium\":v=\"130\",\"Google Chrome\":v=\"130\",\"Not?A Brand\";v=\"99\"")
            .addHeader("Sec-Ch-Ua-Mobile", "?0")
            .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
            .addHeader("Sec-Fetch-Dest", "document")
            .addHeader("Sec-Fetch-Mode", "navigate")
            .addHeader("Sec-Fetch-Site", "none")
            .addHeader("Sec-Fetch-User", "?1")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
            .get()
            .sync();
//        String result = JSON_DATA;
        if (StringUtils.isEmpty(result)) {
            throw new Exception("获取Splatoon数据失败，返回数据为空");
        }
        log.info("获取Splatoon数据成功，开始进行实体转换...");
        log.info("获取数据为:{}", result);
        JSONObject resultJsonObj = JSONUtil.parseObj(result);
        JSONConfig config = new JSONConfig();
        config.setTransientSupport(true);
        config.setIgnoreNullValue(true);
        config.setIgnoreError(true);
        SplatDataDTO data = JSONUtil.toBean(JSONUtil.toJsonStr(resultJsonObj.getJSONObject("data")), config, SplatDataDTO.class);
        log.info("数据转换完成，开始入库...");

        // 1、更新地图信息
        Map<Integer, TSplatVsStage> tSplatVsStageMap = new HashMap<>();
        List<TSplatVsStage> tSplatVsStageList = tSplatVsStageDao.findAll();
        if (!CollectionUtils.isEmpty(tSplatVsStageList)) {
            tSplatVsStageMap = tSplatVsStageList.stream().collect(Collectors.toMap(TSplatVsStage::getVsStageId, Function.identity()));
        }

        SplatVsStagesDTO splatVsStagesDTO = data.getVsStages();
        List<SplatVsStagesNodeDTO> splatVsStagesNodeList = splatVsStagesDTO.getNodes();
        List<TSplatVsStage> tSplatVsStageInsertList = new ArrayList<>();
        for (SplatVsStagesNodeDTO node : splatVsStagesNodeList) {
            // 判断地图信息是否已存储
            if (tSplatVsStageMap.containsKey(node.getVsStageId())) {
                continue;
            }
            TSplatVsStage tSplatVsStage = new TSplatVsStage();
            tSplatVsStage.setVsStageId(node.getVsStageId());
            tSplatVsStage.setVsStageName(node.getName());
            tSplatVsStage.setOriginalImage(node.getImage().getUrl());
            tSplatVsStage.setKeyword(node.getId());
            tSplatVsStageInsertList.add(tSplatVsStage);
        }
        if (!CollectionUtils.isEmpty(tSplatVsStageInsertList)) {
            tSplatVsStageDao.saveBatch(tSplatVsStageInsertList);
        }

        // 上传图片
        List<TSysFile> fileInsertList = new ArrayList<>();
        for (TSplatVsStage tSplatVsStage : tSplatVsStageInsertList) {
            UploadFileVO uploadFileVO = fileUploadService.uploadSplatoonFile(tSplatVsStage.getOriginalImage());
            TSysFile tSysFileDTO = new TSysFile();
            tSysFileDTO.setBizArgs("vs_stage");
            tSysFileDTO.setFileTitle(uploadFileVO.getFileTitle());
            tSysFileDTO.setFileSize(uploadFileVO.getFileSize());
            tSysFileDTO.setFileType(uploadFileVO.getFileType());
            tSysFileDTO.setFileUrl(uploadFileVO.getFilePath());
            tSysFileDTO.setWidth(uploadFileVO.getWidth());
            tSysFileDTO.setHeight(uploadFileVO.getHeight());
            tSysFileDTO.setBizId(tSplatVsStage.getId());
            fileInsertList.add(tSysFileDTO);
        }
        if (!CollectionUtils.isEmpty(fileInsertList)) {
            tSysFileDao.saveBatch(fileInsertList);
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        try {
            // 2、存储涂地信息
            threadPool.execute(() -> this.saveOrUpdateRegularMatchInfo(data));


            // 3、存储真格信息
            threadPool.execute(() -> this.saveOrUpdateBankaraMatchInfo(data));

            // 存储打工信息
            threadPool.execute(() -> {
                try {
                    this.saveOrUpdateSalmonRunInfo(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("保存鲑鱼跑日程信息失败：", e);
                }
            });

            // 存储X赛信息
            threadPool.execute(() -> this.saveOrUpdateXMatchInfo(data));

            // 存储祭典比赛信息
            threadPool.execute(() -> this.saveOrUpdateFestMatchInfo(data));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 防止异常没有关闭线程池
            if (!threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        }


    }

    /**
     * 保存祭典比赛信息
     *
     * @param data 数据
     * @return void
     */
    private void saveOrUpdateFestMatchInfo(SplatDataDTO data) {
        List<TSplatMatchInfoAddDTO> festMatchInfoInsertList = new ArrayList<>();
        SplatFestSchedulesDTO splatFestSchedulesDTO = data.getFestSchedules();
        List<SplatFestSchedulesNodeDTO> splatFestSchedulesDTONodeList = splatFestSchedulesDTO.getNodes();
        for (SplatFestSchedulesNodeDTO node : splatFestSchedulesDTONodeList) {
            // 根据时间查询是否已存在该场地数据，存在不做操作，不存在则进行插入
            Date startTime = node.getStartTime();
            Date endTime = node.getEndTime();
            TSplatMatchInfoAddDTO matchInfo = new TSplatMatchInfoAddDTO();
            matchInfo.setStartTime(startTime);
            matchInfo.setEndTime(endTime);
            matchInfo.setMatchType(SplatoonConstant.MATCH_TYPE_FEST);
            List<SplatFestSchedulesMatchSettingDTO> matchSettingDTOList = node.getFestSchedulesMatchSettingList();
            if (CollectionUtils.isEmpty(matchSettingDTOList)) {
                // 空节点，祭典的时候会出现
                continue;
            }
            List<TSplatMatchInfoDetailAddDTO> matchInfoDetailList = new ArrayList<>();
            for (SplatFestSchedulesMatchSettingDTO matchSettingDTO : matchSettingDTOList) {
                List<SplatVsStageCommDTO> matchVsStageList = matchSettingDTO.getVsStages();
                SplatVsRuleCommDTO vsRule = matchSettingDTO.getVsRule();
                for (SplatVsStageCommDTO matchVsStage : matchVsStageList) {
                    TSplatMatchInfoDetailAddDTO tSplatMatchInfoDetail = new TSplatMatchInfoDetailAddDTO();
                    tSplatMatchInfoDetail.setVsStageId(matchVsStage.getVsStageId());
                    // LOFT 塔楼
                    // AREA 区域
                    // CLAM 蛤蜊
                    // GOAL 鱼虎
                    tSplatMatchInfoDetail.setMatchRule(vsRule.getRule().toLowerCase());
                    // OPEN 开放
                    // CHALLENGE 挑战
                    tSplatMatchInfoDetail.setMatchMode(matchSettingDTO.getFestMode().toLowerCase());
                    matchInfoDetailList.add(tSplatMatchInfoDetail);
                }
            }
            matchInfo.setDetailList(matchInfoDetailList);
            festMatchInfoInsertList.add(matchInfo);
        }
        if (!CollectionUtils.isEmpty(festMatchInfoInsertList)) {
            tSplatMatchInfoService.saveMatchInfoBatch(festMatchInfoInsertList, SplatoonConstant.MATCH_TYPE_FEST);
        }
        log.info("同步祭典比赛数据完成！");
    }

    /**
     * 保存X赛信息
     *
     * @param data 数据
     * @return void
     */
    private void saveOrUpdateXMatchInfo(SplatDataDTO data) {
        List<TSplatMatchInfoAddDTO> regularMatchInfoInsertList = new ArrayList<>();
        SplatXSchedulesDTO splatXSchedulesDTO = data.getXSchedules();
        List<SplatXSchedulesNodeDTO> splatXSchedulesDTONodeList = splatXSchedulesDTO.getNodes();
        for (SplatXSchedulesNodeDTO node : splatXSchedulesDTONodeList) {
            // 根据时间查询是否已存在该场地数据，存在不做操作，不存在则进行插入
            Date startTime = node.getStartTime();
            Date endTime = node.getEndTime();
            TSplatMatchInfoAddDTO matchInfo = new TSplatMatchInfoAddDTO();
            matchInfo.setStartTime(startTime);
            matchInfo.setEndTime(endTime);
            matchInfo.setMatchType(SplatoonConstant.MATCH_TYPE_X);
            SplatXSchedulesMatchSettingDTO matchSettingDTO = node.getXMatchSetting();
            if (matchSettingDTO == null) {
                continue;
            }
            List<TSplatMatchInfoDetailAddDTO> matchInfoDetailList = new ArrayList<>();
            List<SplatVsStageCommDTO> matchVsStageList = matchSettingDTO.getVsStages();
            SplatVsRuleCommDTO vsRule = matchSettingDTO.getVsRule();
            for (SplatVsStageCommDTO matchVsStage : matchVsStageList) {
                TSplatMatchInfoDetailAddDTO tSplatMatchInfoDetail = new TSplatMatchInfoDetailAddDTO();
                tSplatMatchInfoDetail.setVsStageId(matchVsStage.getVsStageId());
                tSplatMatchInfoDetail.setMatchRule(vsRule.getRule().toLowerCase());
                matchInfoDetailList.add(tSplatMatchInfoDetail);
            }
            matchInfo.setDetailList(matchInfoDetailList);
            regularMatchInfoInsertList.add(matchInfo);
        }
        if (!CollectionUtils.isEmpty(regularMatchInfoInsertList)) {
            tSplatMatchInfoService.saveMatchInfoBatch(regularMatchInfoInsertList, SplatoonConstant.MATCH_TYPE_X);
        }
        log.info("同步X比赛数据完成！");
    }

    /**
     * 保存或更新涂地赛程数据
     *
     * @param data 赛程数据
     * @return void
     */
    private void saveOrUpdateRegularMatchInfo(SplatDataDTO data) {
        List<TSplatMatchInfoAddDTO> regularMatchInfoInsertList = new ArrayList<>();
        SplatRegularSchedulesDTO splatRegularSchedulesDTO = data.getRegularSchedules();
        List<SplatRegularSchedulesNodeDTO> splatRegularSchedulesDTONodeList = splatRegularSchedulesDTO.getNodes();
        for (SplatRegularSchedulesNodeDTO node : splatRegularSchedulesDTONodeList) {
            // 根据时间查询是否已存在该场地数据，存在不做操作，不存在则进行插入
            Date startTime = node.getStartTime();
            Date endTime = node.getEndTime();
            TSplatMatchInfoAddDTO matchInfo = new TSplatMatchInfoAddDTO();
            matchInfo.setStartTime(startTime);
            matchInfo.setEndTime(endTime);
            matchInfo.setMatchType(SplatoonConstant.MATCH_TYPE_REGULAR);
            SplatRegularSchedulesMatchSettingDTO matchSettingDTO = node.getRegularMatchSetting();
            if (matchSettingDTO == null) {
                // 空节点，祭典的时候会出现
                continue;
            }
            List<TSplatMatchInfoDetailAddDTO> matchInfoDetailList = new ArrayList<>();
            List<SplatVsStageCommDTO> matchVsStageList = matchSettingDTO.getVsStages();
            SplatVsRuleCommDTO vsRule = matchSettingDTO.getVsRule();
            for (SplatVsStageCommDTO matchVsStage : matchVsStageList) {
                TSplatMatchInfoDetailAddDTO tSplatMatchInfoDetail = new TSplatMatchInfoDetailAddDTO();
                tSplatMatchInfoDetail.setVsStageId(matchVsStage.getVsStageId());
                tSplatMatchInfoDetail.setMatchRule(vsRule.getRule().toLowerCase());
                matchInfoDetailList.add(tSplatMatchInfoDetail);
            }
            matchInfo.setDetailList(matchInfoDetailList);
            regularMatchInfoInsertList.add(matchInfo);
        }
        if (!CollectionUtils.isEmpty(regularMatchInfoInsertList)) {
            tSplatMatchInfoService.saveMatchInfoBatch(regularMatchInfoInsertList, SplatoonConstant.MATCH_TYPE_REGULAR);
        }
        log.info("同步一般比赛数据完成！");
    }

    /**
     * 保存或更新真格赛程数据
     *
     * @param data 赛程数据
     * @return void
     */
    private void saveOrUpdateBankaraMatchInfo(SplatDataDTO data) {
        List<TSplatMatchInfoAddDTO> bankaraMatchInfoInsertList = new ArrayList<>();
        SplatBankaraSchedulesDTO splatBankaraSchedulesDTO = data.getBankaraSchedules();
        List<SplatBankaraSchedulesNodeDTO> splatBankaraSchedulesDTONodeList = splatBankaraSchedulesDTO.getNodes();
        for (SplatBankaraSchedulesNodeDTO node : splatBankaraSchedulesDTONodeList) {
            // 根据时间查询是否已存在该场地数据，存在不做操作，不存在则进行插入
            Date startTime = node.getStartTime();
            Date endTime = node.getEndTime();
            TSplatMatchInfoAddDTO matchInfo = new TSplatMatchInfoAddDTO();
            matchInfo.setStartTime(startTime);
            matchInfo.setEndTime(endTime);
            matchInfo.setMatchType(SplatoonConstant.MATCH_TYPE_BANKARA);
            List<SplatBankaraSchedulesMatchSettingDTO> matchSettingDTOList = node.getBankaraSchedulesMatchSettingList();
            if (CollectionUtils.isEmpty(matchSettingDTOList)) {
                // 空节点，祭典的时候会出现
                continue;
            }
            List<TSplatMatchInfoDetailAddDTO> matchInfoDetailList = new ArrayList<>();
            for (SplatBankaraSchedulesMatchSettingDTO matchSettingDTO : matchSettingDTOList) {
                List<SplatVsStageCommDTO> matchVsStageList = matchSettingDTO.getVsStages();
                SplatVsRuleCommDTO vsRule = matchSettingDTO.getVsRule();
                for (SplatVsStageCommDTO matchVsStage : matchVsStageList) {
                    TSplatMatchInfoDetailAddDTO tSplatMatchInfoDetail = new TSplatMatchInfoDetailAddDTO();
                    tSplatMatchInfoDetail.setVsStageId(matchVsStage.getVsStageId());
                    // LOFT 塔楼
                    // AREA 区域
                    // CLAM 蛤蜊
                    // GOAL 鱼虎
                    tSplatMatchInfoDetail.setMatchRule(vsRule.getRule().toLowerCase());
                    // OPEN 开放
                    // CHALLENGE 挑战
                    tSplatMatchInfoDetail.setMatchMode(matchSettingDTO.getBankaraMode().toLowerCase());
                    matchInfoDetailList.add(tSplatMatchInfoDetail);
                }
            }
            matchInfo.setDetailList(matchInfoDetailList);
            bankaraMatchInfoInsertList.add(matchInfo);
        }
        if (!CollectionUtils.isEmpty(bankaraMatchInfoInsertList)) {
            tSplatMatchInfoService.saveMatchInfoBatch(bankaraMatchInfoInsertList, SplatoonConstant.MATCH_TYPE_BANKARA);
        }
        log.info("同步真格比赛数据完成！");
    }

    /**
     * 保存或更新鲑鱼跑数据
     *
     * @param data 赛程数据
     * @return void
     */
    private void saveOrUpdateSalmonRunInfo(SplatDataDTO data) throws Exception {
        List<TSplatSalmonRunInfoAddDTO> salmonRunInfoAddDTOList = new ArrayList<>();
        SplatCoopGroupingScheduleDTO splatCoopGroupingScheduleDTO = data.getCoopGroupingSchedule();
        SplatSalmonRunSchedulesDTO salmonRunSchedulesDTO = splatCoopGroupingScheduleDTO.getRegularSchedules();
        List<SplatSalmonRunSchedulesNodeDTO> nodes = salmonRunSchedulesDTO.getNodes();
        for (SplatSalmonRunSchedulesNodeDTO node : nodes) {
            TSplatSalmonRunInfoAddDTO salmonRunInfoAddDTO = new TSplatSalmonRunInfoAddDTO();
            salmonRunInfoAddDTO.setStartTime(node.getStartTime());
            salmonRunInfoAddDTO.setEndTime(node.getEndTime());
            salmonRunInfoAddDTO.setSalmonRunType(SplatoonConstant.SALMON_RUN_TYPE_REGULAR);
            SplatSalmonRunSchedulesNodeSettingDTO setting = node.getSetting();
            SalmonRunBossDTO boss = setting.getBoss();
            salmonRunInfoAddDTO.setBossKeyword(boss.getId());
            salmonRunInfoAddDTO.setBossName(boss.getName());
            SalmonRunCoopStageDTO stage = setting.getCoopStage();
            salmonRunInfoAddDTO.setStageKeyword(stage.getId());
            salmonRunInfoAddDTO.setStageName(stage.getName());
            salmonRunInfoAddDTO.setStageThumbnailImage(stage.getThumbnailImage().getUrl());
            salmonRunInfoAddDTO.setStageImage(stage.getImage().getUrl());
            List<TSplatSalmonRunInfoWeaponAddDTO> tSplatSalmonRunInfoWeaponAddDTOList = new ArrayList<>();
            for (SalmonRunWeaponDTO weapon : setting.getWeapons()) {
                TSplatSalmonRunInfoWeaponAddDTO tSplatSalmonRunInfoWeaponAddDTO = new TSplatSalmonRunInfoWeaponAddDTO();
                tSplatSalmonRunInfoWeaponAddDTO.setWeaponKeyword(weapon.getSplatoon3inkId());
                tSplatSalmonRunInfoWeaponAddDTO.setWeaponName(weapon.getName());
                tSplatSalmonRunInfoWeaponAddDTO.setWeaponImage(weapon.getImage().getUrl());
                tSplatSalmonRunInfoWeaponAddDTOList.add(tSplatSalmonRunInfoWeaponAddDTO);
            }
            salmonRunInfoAddDTO.setWeaponList(tSplatSalmonRunInfoWeaponAddDTOList);
            salmonRunInfoAddDTOList.add(salmonRunInfoAddDTO);
        }
        if (!CollectionUtils.isEmpty(salmonRunInfoAddDTOList)) {
            tSplatMatchInfoService.saveSalmonRunBatch(salmonRunInfoAddDTOList, SplatoonConstant.SALMON_RUN_TYPE_REGULAR);
        }
        log.info("同步鲑鱼跑数据完成！");
    }


    @Transactional(rollbackFor = Exception.class)
    public void invokeLangCnDataRefreshOnce() throws Exception {
        this.invokeSplatDataRefresh();
    }

    /**
     * 每天23点55分执行
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 55 23 * * ?")
    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 5000, multiplier = 1))
    public void invokeLangCnDataRefresh() throws Exception {
        log.info("执行splatoon中文名称数据刷新任务...");
        // 获取数据
        String result = OkHttpUtil.builder()
            .url(LANG_CN_URL)
            .addHeader(":authority", "splatoon3.ink")
            .addHeader(":method", "GET")
            .addHeader(":path", "/data/locale/zh-CN.json")
            .addHeader(":scheme", "https")
            .addHeader("accept", "text/html,application/xhtml+xml,application/xml;g=0.9,image/avif,image/webp,image/apng,*/*;g=0.8,application/signed-exchange;v=b3;q=0.7")
            .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
            .addHeader("Accept-Language", "zh-CN,zh;g=0.9")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Pragma", "no-cache")
            .addHeader("Priority", "u=0,i")
            .addHeader("Sec-Ch-Ua", "\"Chromium\":v=\"130\",\"Google Chrome\":v=\"130\",\"Not?A Brand\";v=\"99\"")
            .addHeader("Sec-Ch-Ua-Mobile", "?0")
            .addHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
            .addHeader("Sec-Fetch-Dest", "document")
            .addHeader("Sec-Fetch-Mode", "navigate")
            .addHeader("Sec-Fetch-Site", "none")
            .addHeader("Sec-Fetch-User", "?1")
            .addHeader("Upgrade-Insecure-Requests", "1")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
            .get()
            .sync();
        if (StringUtils.isEmpty(result)) {
            throw new Exception("获取splatoon中文名称数据失败，返回数据为空");
        }
        log.info("获取splatoon中文名称数据成功，开始进行实体转换...");
        log.info("获取splatoon中文名称数据为:{}", result);
        JSONObject resultJsonObj = JSONUtil.parseObj(result);
        List<TLangCn> langCnList = new ArrayList<>();
        for (String type : resultJsonObj.keySet()) {
            if ("festivals".equals(type) || "events".equals(type)) {
                continue;
            }
            JSONObject typeItem = resultJsonObj.getJSONObject(type);
            for (String key : typeItem.keySet()) {
                JSONObject value = typeItem.getJSONObject(key);
                TLangCn langCn = new TLangCn();
                langCn.setKeyword(key);
                langCn.setCnName(value.getStr("name"));
                langCn.setType(type);
                langCnList.add(langCn);
            }
        }
        log.info("数据转换完成，开始入库...");
        // 跟库表中比较，判断更新或者插入
        List<TLangCn> allLangCnList = tLangCnDao.findAll();
        Map<String, TLangCn> allLangCnMap = allLangCnList.stream().collect(Collectors.toMap(TLangCn::getKeyword, Function.identity(), (oldValue, newValue) -> oldValue));
        List<TLangCn> langCnInsertList = new ArrayList<>();
        List<TLangCn> langCnUpdateList = new ArrayList<>();
        for (TLangCn tLangCn : langCnList) {
            if (allLangCnMap.containsKey(tLangCn.getKeyword())) {
                // 更新操作
                TLangCn langCn = allLangCnMap.get(tLangCn.getKeyword());
                langCn.setCnName(tLangCn.getCnName());
                langCn.setType(tLangCn.getType());
                langCnUpdateList.add(langCn);
            } else {
                // 插入操作
                langCnInsertList.add(tLangCn);
            }
        }
        if (!CollectionUtils.isEmpty(langCnInsertList)) {
            tLangCnDao.saveBatch(langCnInsertList);
        }
        if (!CollectionUtils.isEmpty(langCnUpdateList)) {
            tLangCnDao.updateBatchById(langCnUpdateList);
        }
        log.info("执行splatoon中文名称数据刷新任务完成！");
    }

}
