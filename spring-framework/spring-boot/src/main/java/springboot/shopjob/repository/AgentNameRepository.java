package springboot.shopjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.shopjob.entity.AgentNameEntity;


public interface AgentNameRepository extends JpaRepository<AgentNameEntity, Integer> {


}
