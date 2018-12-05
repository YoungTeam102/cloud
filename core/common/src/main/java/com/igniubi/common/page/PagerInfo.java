package com.igniubi.common.page;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 分页返回结果
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class PagerInfo<T> {
    /**
     * 分页状态码；200：成功， 非200：失败
     */
    private int code;
    /**
     * 第几页
     */
    private int pageNum;
    /**
     * 每页展示几条
     */
    private int pageSize;
    /**
     * 总条数
     */
    private int total;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 分页结果数据集合
     */
    private List<T> list;

    @JsonIgnore
    private String totalMapperId;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getPageNum() { return pageNum; }

    public void setPageNum(int pageNum) { this.pageNum = pageNum; }

    public int getPageSize() { return pageSize; }

    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotal() { return total; }

    public void setTotal(int total) { this.total = total; }

    public int getPages() { return pages; }

    public void setPages(int pages) { this.pages = pages; }

    public List<T> getList() { return list; }

    public void setList(List<T> list) { this.list = list; }

    public String getTotalMapperId() { return totalMapperId; }

    public void setTotalMapperId(String totalMapperId) { this.totalMapperId = totalMapperId; }
}
