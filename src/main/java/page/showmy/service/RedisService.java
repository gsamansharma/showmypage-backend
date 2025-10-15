package page.showmy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> T get(String key, Class<T> entityClass) {
       try{
           Object dataFromCache = redisTemplate.opsForValue().get(key);
           if(entityClass.isInstance(dataFromCache)) {
               return entityClass.cast(dataFromCache);
           }
           return null;
       } catch (Exception e) {
           return null;
       }
    }

    public void set(String key, Object o, Long ttl) {
        try{
            redisTemplate.opsForValue().set(key, o, ttl, TimeUnit.SECONDS);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
