package springboot.shopjob.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author shenhuaxin
 * @date 2020/12/17
 */
@Entity
@Table(name = "need", schema = "shop", catalog = "")
public class NeedEntity {
    @Id
    private Integer shopSeq;
    private String shopName;
    private String orgId;
    private String orgName;
    private String tenementName;
    private Integer supplierId;
    private String supplierName;
    private String contractBeginTime;
    private String contractEndTime;
    private Integer billPeriodRule;
    private String billPeriod;
    private Integer agencyAmountUnit;
    private Integer agencyAmount;
    private Integer chargeType;
    private Integer totalParkCost;
    private String totalParkCostStage;
    private String contractScanning;

    @Basic
    @Column(name = "shop_seq")
    public Integer getShopSeq() {
        return shopSeq;
    }

    public void setShopSeq(Integer shopSeq) {
        this.shopSeq = shopSeq;
    }

    @Basic
    @Column(name = "shop_name")
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @Basic
    @Column(name = "org_id")
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Basic
    @Column(name = "org_name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "tenement_name")
    public String getTenementName() {
        return tenementName;
    }

    public void setTenementName(String tenementName) {
        this.tenementName = tenementName;
    }

    @Basic
    @Column(name = "supplier_id")
    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Basic
    @Column(name = "supplier_name")
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Basic
    @Column(name = "contract_begin_time")
    public String getContractBeginTime() {
        return contractBeginTime;
    }

    public void setContractBeginTime(String contractBeginTime) {
        this.contractBeginTime = contractBeginTime;
    }

    @Basic
    @Column(name = "contract_end_time")
    public String getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(String contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    @Basic
    @Column(name = "bill_period_rule")
    public Integer getBillPeriodRule() {
        return billPeriodRule;
    }

    public void setBillPeriodRule(Integer billPeriodRule) {
        this.billPeriodRule = billPeriodRule;
    }

    @Basic
    @Column(name = "bill_period")
    public String getBillPeriod() {
        return billPeriod;
    }

    public void setBillPeriod(String billPeriod) {
        this.billPeriod = billPeriod;
    }

    @Basic
    @Column(name = "agency_amount_unit")
    public Integer getAgencyAmountUnit() {
        return agencyAmountUnit;
    }

    public void setAgencyAmountUnit(Integer agencyAmountUnit) {
        this.agencyAmountUnit = agencyAmountUnit;
    }

    @Basic
    @Column(name = "agency_amount")
    public Integer getAgencyAmount() {
        return agencyAmount;
    }

    public void setAgencyAmount(Integer agencyAmount) {
        this.agencyAmount = agencyAmount;
    }

    @Basic
    @Column(name = "charge_type")
    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    @Basic
    @Column(name = "total_park_cost")
    public Integer getTotalParkCost() {
        return totalParkCost;
    }

    public void setTotalParkCost(Integer totalParkCost) {
        this.totalParkCost = totalParkCost;
    }

    @Basic
    @Column(name = "total_park_cost_stage")
    public String getTotalParkCostStage() {
        return totalParkCostStage;
    }

    public void setTotalParkCostStage(String totalParkCostStage) {
        this.totalParkCostStage = totalParkCostStage;
    }

    @Basic
    @Column(name = "contract_scanning")
    public String getContractScanning() {
        return contractScanning;
    }

    public void setContractScanning(String contractScanning) {
        this.contractScanning = contractScanning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeedEntity that = (NeedEntity) o;
        return Objects.equals(shopSeq, that.shopSeq) && Objects.equals(shopName, that.shopName) && Objects.equals(orgId, that.orgId) && Objects.equals(orgName, that.orgName) && Objects.equals(tenementName, that.tenementName) && Objects.equals(supplierId, that.supplierId) && Objects.equals(supplierName, that.supplierName) && Objects.equals(contractBeginTime, that.contractBeginTime) && Objects.equals(contractEndTime, that.contractEndTime) && Objects.equals(billPeriodRule, that.billPeriodRule) && Objects.equals(billPeriod, that.billPeriod) && Objects.equals(agencyAmountUnit, that.agencyAmountUnit) && Objects.equals(agencyAmount, that.agencyAmount) && Objects.equals(chargeType, that.chargeType) && Objects.equals(totalParkCost, that.totalParkCost) && Objects.equals(totalParkCostStage, that.totalParkCostStage) && Objects.equals(contractScanning, that.contractScanning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopSeq, shopName, orgId, orgName, tenementName, supplierId, supplierName, contractBeginTime, contractEndTime, billPeriodRule, billPeriod, agencyAmountUnit, agencyAmount, chargeType, totalParkCost, totalParkCostStage, contractScanning);
    }
}
