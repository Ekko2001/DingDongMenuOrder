package com.yakusa.test;

import com.yakusa.reggie.ReggieApplication;
import net.sf.jsqlparser.parser.feature.Feature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static net.sf.jsqlparser.parser.feature.Feature.values;


@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void  teststring(){
    //redisTemplate.opsForValue().set("sex","man");
//        HashOperations hashOperations = redisTemplate.opsForHash();
//        hashOperations.put("user","name","zhangsan");
//        hashOperations.put("user","age","18");
//
//        String name = (String)hashOperations.get("user", "name");
//        System.out.println(name);
//
//        hashOperations.keys("user").forEach(System.out::println);
//        List myvalues = hashOperations.values("user");
//        for (Object  ty: myvalues) {
//            System.out.println(ty);
//        }
    }
}
