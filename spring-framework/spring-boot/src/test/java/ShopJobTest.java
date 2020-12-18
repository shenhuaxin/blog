import lombok.Data;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.shopjob.entity.DateUtil;
import springboot.shopjob.entity.JacksonUtil;
import springboot.shopjob.entity.PeriodDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author shenhuaxin
 * @date 2020/12/18
 */

@SpringBootTest
public class ShopJobTest {


    @Test
    public void supplyHistoryShopStatement() {


    }


    /**
     * 生成合同的sql
     */
    private StringBuilder generateContractSql(ShopContractNode shopContractNode) {
        String sql = "insert into iss_shop_contract_record_info(" +
                "shop_seq, " +
                "shop_name, " +
                "operation_org_id, " +
                "operation_org_name, " +
                "contract_party, " +
                "agent_id, " +
                "agent_name, " +
                "start_date, " +
                "end_date, " +
                "modify_channel, " +
                "modify_channel_related_info, " +
                "effective_time, " +
                "bill_period_rule, " +
                "agency_amount_unit, " +
                "agency_amount, " +
                "charge_type, " +
                "total_park_cost, " +
                "total_park_cost_stage, " +
                "contract_file_down_path, " +
                "misc_desc) " +
                "value (";
        StringBuilder contractSql = new StringBuilder();
        contractSql.append(sql);
        contractSql.append("'").append(shopContractNode.getShopSeq()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getShopName()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getOperationOrgId()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getOperationOrgName()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getContractParty()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getAgentId()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getAgentName()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getStartDate()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getEndDate()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getModifyChannel()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getModifyChannelRelatedInfo()).append("'").append(",");
        contractSql.append("'").append(DateUtil.getFormatDate(shopContractNode.getEffectiveTime(), DateUtil.DATE_TYPE1)).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getBillPeriodRule()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getAgencyAmountUnit()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getAgencyAmount()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getChargeType()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getTotalParkCost()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getTotalParkCostStage()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getContractFileDownPath()).append("'").append(",");
        contractSql.append("'").append(shopContractNode.getMiscDesc()).append("'");
        contractSql.append(");");
        return contractSql;
    }

    /**
     * 生成job sql
     */
    private StringBuilder generateJobSql(ShopStatementJob shopStatementJob) {
        StringBuilder jobSql = new StringBuilder();
        jobSql.append("INSERT INTO `iss`.`iss_shop_statement_middle_job_info` (" +
                "`shop_seq`," +
                "`contract_id`," +
                "`year`," +
                "`month`," +
                "`start_time`," +
                "`end_time`," +
                "`trigger_time`," +
                "`binding_type`," +
                "`misc_desc`) value (");
        jobSql.append("'").append(shopStatementJob.getShopSeq()).append("'").append(",");
        jobSql.append("(select last_insert_id())").append(",");
        jobSql.append("'").append(shopStatementJob.getYear()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getMonth()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getStartTime()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getEndTime()).append("'").append(",");

        LocalDate endTime = LocalDate.parse(shopStatementJob.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (endTime.compareTo(LocalDate.now()) <= 0) {
            // 第二天执行
            jobSql.append("'").append(LocalDate.now().plusDays(1L).format(DateTimeFormatter.ofPattern(DateUtil.DATE_TYPE5))).append("'").append(",");
        }else {
            // 账期结束时间第二天执行。
            jobSql.append("'").append(endTime.plusDays(1L).format(DateTimeFormatter.ofPattern(DateUtil.DATE_TYPE5))).append("'").append(",");
        }
        jobSql.append("'").append(shopStatementJob.getBindingType()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getMiscDesc()).append("'");
        jobSql.append(");");
        return jobSql;
    }

    /**
     * 生成更新iss_shop_build_info的sql
     */
    private StringBuilder updateShopBuildInfo(Long shopSeq, List<PeriodDTO> list) {
        StringBuilder updateBuildInfoSql = new StringBuilder();
        updateBuildInfoSql.append("update iss.iss_shop_build_info set bill_period_rule = 3, bill_period = ");
        updateBuildInfoSql.append("'").append(JacksonUtil.toJsonString(list)).append("'");
        updateBuildInfoSql.append("where shopSeq = ").append(shopSeq);
        updateBuildInfoSql.append(";");
        return updateBuildInfoSql;
    }

    @Data
    static class ShopContractNode {
        private Long shopSeq;

        private String shopName;

        private String operationOrgId;

        private String operationOrgName;

        private String contractParty;

        private Long agentId;

        private String agentName;

        private String startDate;

        private String endDate;

        private String modifyChannel = "手动初始化";

        private String modifyChannelRelatedInfo = "evcard-iss";

        private Date effectiveTime;

        private Integer billPeriodRule = 3;  // 自定义账期

        private Integer agencyAmountUnit;    // 代理费单位     历史数据需要明确是网点还是车位，费用单位。

        private BigDecimal agencyAmount;     // 代理费

        private Integer chargeType;          // 网点车位费计费方式：  历史数据是元/月  针对网点

        private BigDecimal totalParkCost;    // 网点总费用

        private String totalParkCostStage;   // 分段计费

        private String contractFileDownPath; // 合同文件地址

        private String miscDesc = "补录历史数据";

    }


    @Data
    static class ShopStatementJob {
        private Long shopSeq;
        private Long contractId;
        private String year;
        private String month;
        private String startTime;
        private String endTime;
        private String triggerTime;
        private Integer bindingType;
        private String miscDesc = "系统生成";
    }
}
