package springboot.shopjob.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springboot.shopjob.entity.*;
import springboot.shopjob.repository.AgentNameRepository;
import springboot.shopjob.repository.BillPeriodRepository;
import springboot.shopjob.repository.NeedRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author shenhuaxin
 * @date 2020/12/17
 */
@RestController
public class ShopJobController {

    @Autowired
    private NeedRepository needRepository;
    @Autowired
    private AgentNameRepository agentNameRepository;
    @Autowired
    private BillPeriodRepository billPeriodRepository;


    @GetMapping("test")
    public String test(@RequestParam String time) {

        return "";
    }

    @GetMapping("getsql")
    public String getsql() throws IOException {
        List<NeedEntity> allShop = needRepository.findAll();
        List<AgentNameEntity> allAgentName = agentNameRepository.findAll();
        Map<Integer, AgentNameEntity> agentMap = allAgentName.stream().collect(Collectors.toMap(AgentNameEntity::getShopSeq, agent -> agent));
        List<BillPeriodEntity> allBillPeriodEntity = billPeriodRepository.findAll();
        Map<Integer, List<BillPeriodEntity>> shopPeriodMap = allBillPeriodEntity.stream().collect(Collectors.toMap(BillPeriodEntity::getShopSeq,
                bill -> {
                    List<BillPeriodEntity> billList = new ArrayList<>();
                    billList.add(bill);
                    return billList;
                },
                (oldV, newV) -> {
                    oldV.addAll(newV);
                    return oldV;
                }
        ));
        FileWriter fileWriter = new FileWriter("C:\\Users\\dell\\Desktop\\shop_data.sql");
        for(NeedEntity needEntity : allShop) {
            // 一个合同日志， 对应了多个任务。
            //  agency_amount_unit: 代理费单位 1：元/月 2：元/季度 3：元/年'
            //  charge_type:        计费方式   0：无计费的情况，1：统一计费， 2：分段计费，3：按实际订单结算
            //  bill_period_rule:   账期规则   1：自然月（N+1月)，2：非自然月(N)月 3: 自定义
            ShopContractNode shopContractNode = new ShopContractNode();
            shopContractNode.setShopSeq(needEntity.getShopSeq());
            shopContractNode.setShopName(needEntity.getShopName());
            shopContractNode.setOperationOrgId(needEntity.getOrgId());
            shopContractNode.setOperationOrgName(needEntity.getOrgName());

            // 先使用数据库中， 再使用表格，再使用暂无
            AgentNameEntity agentNameEntity = agentMap.getOrDefault(needEntity.getShopSeq(), new AgentNameEntity());
            if (StringUtils.isEmpty(needEntity.getTenementName())) {
                if (StringUtils.isEmpty(agentNameEntity.getContractParty())) {
                    shopContractNode.setContractParty("暂无");
                } else {
                    shopContractNode.setContractParty(agentNameEntity.getContractParty());
                }
            } else {
                shopContractNode.setContractParty(needEntity.getTenementName());
            }
            // 先使用数据库、再使用表格、再使用抬头
            if (StringUtils.isEmpty(needEntity.getSupplierName())) {
                if (StringUtils.isEmpty(agentNameEntity.getAgentName())) {
                    shopContractNode.setAgentId(0);
                    shopContractNode.setAgentName(shopContractNode.getContractParty());
                } else {
                    shopContractNode.setAgentId(agentNameEntity.getAgentId());
                    shopContractNode.setAgentName(agentNameEntity.getAgentName());
                }
            } else {
                shopContractNode.setAgentId(needEntity.getSupplierId());
                shopContractNode.setAgentName(needEntity.getSupplierName());
            }
            shopContractNode.setStartDate(needEntity.getContractBeginTime());
            shopContractNode.setEndDate(needEntity.getContractEndTime());
            shopContractNode.setModifyChannel("手动初始化");
            shopContractNode.setModifyChannelRelatedInfo("evcard-iss");
            shopContractNode.setEffectiveTime(new Date());
            shopContractNode.setBillPeriodRule(3);
            shopContractNode.setAgencyAmountUnit(1);
            shopContractNode.setAgencyAmount(BigDecimal.valueOf(Optional.ofNullable(needEntity.getAgencyAmount()).orElse(0)));
            shopContractNode.setChargeType(1);
            shopContractNode.setTotalParkCost(BigDecimal.valueOf(needEntity.getTotalParkCost()));
            shopContractNode.setTotalParkCostStage("");
            shopContractNode.setContractFileDownPath(needEntity.getContractScanning());
            shopContractNode.setMiscDesc("2020/12/18，手动初始化对账单，合同生效日期设为2020/12/18，代理费针对网点，单位为元/月，未设置代理费的网点设置为0。 车位费针对网点，单位为元/月。");
            StringBuilder contractSql = generateContractSql(shopContractNode);
            // 合同SQL
            System.out.println(contractSql.toString());
            fileWriter.append(contractSql.toString() + "\n");

            Pattern compile = Pattern.compile("(\\d+)年(\\d+)月");
            List<BillPeriodEntity> billPeriodEntities = shopPeriodMap.get(shopContractNode.getShopSeq());
            List<PeriodDTO> list = new ArrayList<>();
            for (BillPeriodEntity billPeriodEntity : billPeriodEntities) {
                ShopStatementJob shopStatementJob = new ShopStatementJob();
                shopStatementJob.setShopSeq(shopContractNode.getShopSeq());
                shopStatementJob.setContractId(0);
                try {
                    Matcher matcher = compile.matcher(billPeriodEntity.getMonth());
                    matcher.find();
                    String year = matcher.group(1);
                    String month = matcher.group(2);
                    shopStatementJob.setYear(year);
                    shopStatementJob.setMonth(month);
                }catch (Exception e) {
                    System.out.println(billPeriodEntity.getMonth());
                    throw e;
                }
                shopStatementJob.setStartTime(DateUtil.getFormatDate(billPeriodEntity.getStartTime(), DateUtil.DATE_TYPE5));
                shopStatementJob.setEndTime(DateUtil.getFormatDate(billPeriodEntity.getEndTime(), DateUtil.DATE_TYPE5));
                list.add(new PeriodDTO(billPeriodEntity.getStartTime(), billPeriodEntity.getEndTime()));
                shopStatementJob.setMiscDesc("自动生成对账单");
                shopStatementJob.setBindingType(1);
                StringBuilder parkJobSql = generateJobSql(shopStatementJob);
                shopStatementJob.setBindingType(2);
                StringBuilder agentJobSql = generateJobSql(shopStatementJob);
                shopStatementJob.setBindingType(3);
                StringBuilder powerJobSql = generateJobSql(shopStatementJob);
                // 任务SQL
                System.out.println(parkJobSql.toString());
                System.out.println(agentJobSql.toString());
                System.out.println(powerJobSql.toString());
                fileWriter.append(parkJobSql.toString() +  "\n");
                fileWriter.append(agentJobSql.toString() + "\n");
                fileWriter.append(powerJobSql.toString() + "\n");
            }
            // 更新shopBuildInfo
            StringBuilder updateBuildInfoSql = updateShopBuildInfo(shopContractNode.getShopSeq(), list);
            System.out.println(updateBuildInfoSql.toString());
            fileWriter.append(updateBuildInfoSql.toString() + "\n");
            fileWriter.flush();
            break;
        }
        fileWriter.close();
        return "success";
    }


    /**
     * 生成合同的sql
     */
    private StringBuilder generateContractSql(ShopContractNode shopContractNode) {
        String sql = "insert into iss.iss_shop_contract_record_info(" +
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
        jobSql.append("(select id from iss.iss_shop_contract_record_info where shop_seq = " + shopStatementJob.getShopSeq() + " limit 1)").append(",");
        jobSql.append("'").append(shopStatementJob.getYear()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getMonth()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getStartTime()).append("'").append(",");
        jobSql.append("'").append(shopStatementJob.getEndTime()).append("'").append(",");

        LocalDate endTime = LocalDate.parse(shopStatementJob.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (endTime.compareTo(LocalDate.now()) <= 0) {
            // 第二天执行
            jobSql.append("'").append(LocalDate.now().plusDays(1L).format(DateTimeFormatter.ofPattern(DateUtil.DATE_TYPE5))).append("'").append(",");
        } else {
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
    private StringBuilder updateShopBuildInfo(Integer shopSeq, List<PeriodDTO> list) {
        StringBuilder updateBuildInfoSql = new StringBuilder();
        updateBuildInfoSql.append("update iss.iss_shop_build_info set bill_period_rule = 3, charge_type = 1, agency_amount_unit = 1, bill_period =");
        updateBuildInfoSql.append("'").append(JacksonUtil.toJsonString(list)).append("'");
        updateBuildInfoSql.append("where shop_seq = ").append(shopSeq);
        updateBuildInfoSql.append(";");
        return updateBuildInfoSql;
    }

    @Data
    static class ShopContractNode {
        private Integer shopSeq;

        private String shopName;

        private String operationOrgId;

        private String operationOrgName;

        private String contractParty;

        private Integer agentId;

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
        private Integer shopSeq;
        private Integer contractId;
        private String year;
        private String month;
        private String startTime;
        private String endTime;
        private String triggerTime;
        private Integer bindingType;
        private String miscDesc = "系统生成";
    }
}
