package com.campus.qa.dto;

import java.util.List;

/**
 * 分页查询结果封装
 */
public class PageResult<T> {

    /** 当前页数据列表 */
    private List<T> list;
    /** 总记录数 */
    private Long total;
    /** 当前页码 */
    private Integer pageNum;
    /** 每页条数 */
    private Integer pageSize;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
