package sunj.redisserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //직렬화기 설정 (역직렬화는 자동으로 된다.)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);

        //Key는 String, value는 json으로 직렬화
        StringRedisSerializer stringSer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSer = new GenericJackson2JsonRedisSerializer();

        //기본 설정 되어있는 redis 바이너리를 변경
        template.setKeySerializer(stringSer);
        template.setValueSerializer(jsonSer);
        template.setHashKeySerializer(stringSer);
        template.setHashValueSerializer(jsonSer);

        template.afterPropertiesSet();
        return template;
    }
}
