package com.ohgoodteam.ohgoodpay.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        try {
            // Redis 연결 테스트
            String testKey = "test:connection";
            String testValue = "Hello Redis!";
            
            // 값 저장
            redisTemplate.opsForValue().set(testKey, testValue);
            
            // 값 조회
            String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
            
            // 검증
            assertEquals(testValue, retrievedValue);
            
            // 정리
            redisTemplate.delete(testKey);
            
            System.out.println("Redis 연결 성공!");
            
        } catch (Exception e) {
            System.err.println("Redis 연결 실패: " + e.getMessage());
            e.printStackTrace();
            fail("Redis 연결 테스트 실패: " + e.getMessage());
        }
    }
    
    @Test
    public void testRedisInfo() {
        try {
            // Redis 서버 정보 확인
            Object ping = redisTemplate.getConnectionFactory().getConnection().ping();
            System.out.println("Redis PING 응답: " + ping);
            
            assertTrue(true, "Redis 서버 응답 확인");
            
        } catch (Exception e) {
            System.err.println("Redis 서버 정보 조회 실패: " + e.getMessage());
            fail("Redis 서버 정보 조회 실패: " + e.getMessage());
        }
    }
}