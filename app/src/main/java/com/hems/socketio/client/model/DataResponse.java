package com.hems.socketio.client.model;

/**
 * Created by planet on 7/26/2017.
 */

public class DataResponse<T> extends Response<T> {
    private int dataCount;
    private int pageNo;
    private int limit;

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
