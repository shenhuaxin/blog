package springboot.shopjob.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author shenhuaxin
 * @date 2020/12/17
 */
@Entity
@Table(name = "agent_name", schema = "shop", catalog = "")
public class AgentNameEntity {
    @Id
    private Integer shopSeq;
    private String contractParty;
    private String agentName;
    private Integer agentId;

    @Basic
    @Column(name = "shop_seq")
    public Integer getShopSeq() {
        return shopSeq;
    }

    public void setShopSeq(Integer shopSeq) {
        this.shopSeq = shopSeq;
    }

    @Basic
    @Column(name = "contract_party")
    public String getContractParty() {
        return contractParty;
    }

    public void setContractParty(String contractParty) {
        this.contractParty = contractParty;
    }

    @Basic
    @Column(name = "agent_name")
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Basic
    @Column(name = "agent_id")
    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentNameEntity that = (AgentNameEntity) o;
        return Objects.equals(shopSeq, that.shopSeq) && Objects.equals(contractParty, that.contractParty) && Objects.equals(agentName, that.agentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shopSeq, contractParty, agentName);
    }
}
