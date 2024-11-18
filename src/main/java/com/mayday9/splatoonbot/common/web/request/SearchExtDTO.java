package com.mayday9.splatoonbot.common.web.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//搜索请求参数，未定义的参数用map接收
@ApiModel(value = "SearchExtDTO对象", description = "SearchExtDTO搜索管理")
public class SearchExtDTO extends SearchDTO {

    @ApiModelProperty(value = "开始时间")
    private String beginTimeStr;
    @ApiModelProperty(value = "结束时间")
    private String endTimeStr;

    public String getBeginTimeStr() {
        return beginTimeStr;
    }

    public void setBeginTimeStr(String beginTimeStr) {
        this.beginTimeStr = beginTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

}
