package com.bubble.concurrent;

import java.io.Serializable;

/**
 * @author wugang
 * date: 2020-09-23 17:40
 **/
public class SrcCount implements Serializable {
    private static final long serialVersionUID = 4386648578293949548L;

    private int totalCount;
    private int supplyCount;
    private int apiCount;
    private int crawlerCount;
    private int selfSupportCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSupplyCount() {
        return supplyCount;
    }

    public void setSupplyCount(int supplyCount) {
        this.supplyCount = supplyCount;
    }

    public int getApiCount() {
        return apiCount;
    }

    public void setApiCount(int apiCount) {
        this.apiCount = apiCount;
    }

    public int getCrawlerCount() {
        return crawlerCount;
    }

    public void setCrawlerCount(int crawlerCount) {
        this.crawlerCount = crawlerCount;
    }

    public int getSelfSupportCount() {
        return selfSupportCount;
    }

    public void setSelfSupportCount(int selfSupportCount) {
        this.selfSupportCount = selfSupportCount;
    }

    @Override
    public String toString() {
        return "[" +
                "totalCount=" + totalCount +
                ", supplyCount=" + supplyCount +
                ", apiCount=" + apiCount +
                ", crawlerCount=" + crawlerCount +
                ", selfSupportCount=" + selfSupportCount +
                ']';
    }
}
