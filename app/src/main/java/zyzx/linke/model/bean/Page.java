package zyzx.linke.model.bean;

/**
 * 用于分页的bean
 */

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable {

    public final static int PAGESIZE = 30;

    private int pageSize = PAGESIZE;

    private List items;

    private int totalCount;

    //private int[] indexes = new int[0];

    private int startIndex = 0;

    public Page() {

    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        if (totalCount > 0) {
            this.totalCount = totalCount;
            int count = totalCount / pageSize;
            if (totalCount % pageSize > 0) {
                count++;
            }
            this.setTotalPage(count);
        } else {
            this.totalCount = 0;
        }
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        if (totalCount < 0)
            this.startIndex = 0;
        else if (startIndex >= totalCount)
            this.startIndex = startIndex;
        else if (startIndex < 0)
            this.startIndex = 0;
        else {
            this.startIndex = startIndex;
        }
    }

    private Integer totalPage = 0;
    private Integer curPage = 1;

    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
        this.setStartIndex((curPage - 1) * pageSize);
    }

    public Integer getCurPage() {
        return curPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
