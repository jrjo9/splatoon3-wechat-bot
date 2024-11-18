package com.mayday9.splatoonbot.common.web.request;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//搜索请求参数，未定义的参数用map接收
public class SearchRequest {

    private Integer page = 1;

    private Integer pageSize = 10;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String keyword;
    private String sortBy;
    private String sortType;
    private Map<String, Object> params;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public Object getParams(String key) {
        return params != null ? params.get(key) : null;
    }

    public void setParams(String key, Object value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
    }
}
