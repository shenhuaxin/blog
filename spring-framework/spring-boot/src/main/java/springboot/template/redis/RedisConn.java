package springboot.template.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisConn {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void connect() {
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
        System.out.println("nme");
    }

}
