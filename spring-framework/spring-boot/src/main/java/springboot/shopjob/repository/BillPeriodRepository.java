package springboot.shopjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.shopjob.entity.BillPeriodEntity;

/**
 * @author shenhuaxin
 * @date 2020/12/17
 */
public interface BillPeriodRepository extends JpaRepository<BillPeriodEntity, Integer> {
}
