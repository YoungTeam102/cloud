package com.igniubi.common.dtos;

/**
 * 公共分页请求头
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class PagerReqDTO extends BaseDTO{
    /**
     * 第几页,默认1
     */
    private Integer pageNum;

    /**
     * 每页展示数,默认10
     */
    private Integer pageSize;

    public PagerReqDTO(){
        this.pageNum = 1;
        this.pageSize = 10;
    }

    public PagerReqDTO(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Integer getPageNum() { return pageNum; }

    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }

    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
