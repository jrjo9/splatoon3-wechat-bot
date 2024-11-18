package com.mayday9.splatoonbot.common.web.response;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

//pagehelper分页查询，替换pagehelper的pageinfo，去掉一些不需要的属性
@ApiModel(value = "PageResult对象", description = "分页管理")
public class PageResult<T> extends PageSerializable<T> {

    //当前再
    @ApiModelProperty(value = "页码")
    private int page;
    //每页的数量
    @ApiModelProperty(value = "每页的条数")
    private int pageSize;
    //总页数
    @ApiModelProperty(value = "总页数")
    private int pageCount;

    public PageResult() {
    }

    public PageResult(List<T> list) {
        super(list);
        if (list instanceof Page) {
            Page page = (Page) list;
            this.page = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.pageCount = page.getPages();
        } else {
            this.page = 1;
            this.pageSize = list.size();
            this.pageCount = 1;
        }
    }

    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.setTotal(total);
        this.page = page;
        this.pageSize = pageSize;
        this.pageCount = (int) ((total + pageSize - 1) / pageSize);
        this.setList(list);
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
