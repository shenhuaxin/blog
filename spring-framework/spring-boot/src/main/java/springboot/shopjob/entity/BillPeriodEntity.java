package springboot.shopjob.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author shenhuaxin
 * @date 2020/12/17
 */
@Entity
@Table(name = "bill_period", schema = "shop", catalog = "")
public class BillPeriodEntity {

    @Id
    @JsonIgnore
    private Integer id;
    @JsonIgnore
    private Integer shopSeq;

    private String month;
    private Date startTime;
    private Date endTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "shop_seq")
    public Integer getShopSeq() {
        return shopSeq;
    }

    public void setShopSeq(Integer shopSeq) {
        this.shopSeq = shopSeq;
    }

    @Basic
    @Column(name = "month")
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @Basic
    @Column(name = "start_time")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "end_time")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillPeriodEntity that = (BillPeriodEntity) o;
        return Objects.equals(shopSeq, that.shopSeq) && Objects.equals(month, that.month) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopSeq, month, startTime, endTime);
    }
}
