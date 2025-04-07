package com.keji.green.lit.engine.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 分页响应
 * @author xiangjun_lee
 */
@Data
public class PageResponse<T> {
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 当前页码
     */
    private int pageNum;
    
    /**
     * 每页大小
     */
    private int pageSize;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 构建分页响应
     */
    public static <T> PageResponse<T> build(List<T> records, long total, int pageNum, int pageSize) {
        PageResponse<T> response = new PageResponse<>();
        response.setRecords(records);
        response.setTotal(total);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setTotalPages((int) Math.ceil((double) total / pageSize));
        return response;
    }
} 