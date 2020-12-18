package springboot.shopjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.shopjob.entity.NeedEntity;

/**
 * @author shenhuaxin
 * @date 2020/12/17
 */
public interface NeedRepository extends JpaRepository<NeedEntity, Integer> {
}
