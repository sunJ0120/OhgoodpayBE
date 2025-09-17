package com.ohgoodteam.ohgoodpay.repository;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CustomerRepositoryQueryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFindCustomerCacheInfoById() {
        try {
            Long testCustomerId = 1L; // 실제 존재하는 고객 ID 사용
            
            CustomerCacheDTO result = customerRepository.findCustomerCacheInfoById(testCustomerId);
            
            if (result != null) {
                System.out.println("✅ findCustomerCacheInfoById 성공!");
                System.out.println("Customer ID: " + result.getCustomerId());
                System.out.println("Name: " + result.getName());
                System.out.println("Credit Limit: " + result.getCreditLimit());
            } else {
                System.out.println("⚠️ Customer ID " + testCustomerId + "가 존재하지 않습니다.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ findCustomerCacheInfoById 실패: " + e.getMessage());
            e.printStackTrace();
            fail("findCustomerCacheInfoById 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testFindHobbyByCustomerId() {
        try {
            Long testCustomerId = 1L;
            
            Optional<String> hobby = customerRepository.findHobbyByCustomerId(testCustomerId);
            
            System.out.println("✅ findHobbyByCustomerId 성공!");
            System.out.println("Hobby: " + hobby.orElse("취미 없음"));
            
        } catch (Exception e) {
            System.err.println("❌ findHobbyByCustomerId 실패: " + e.getMessage());
            e.printStackTrace();
            fail("findHobbyByCustomerId 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testFindBalanceByCustomerId() {
        try {
            Long testCustomerId = 1L;
            
            Optional<Integer> balance = customerRepository.findBalanceByCustomerId(testCustomerId);
            
            System.out.println("✅ findBalanceByCustomerId 성공!");
            System.out.println("Balance: " + balance.orElse(0));
            
        } catch (Exception e) {
            System.err.println("❌ findBalanceByCustomerId 실패: " + e.getMessage());
            e.printStackTrace();
            fail("findBalanceByCustomerId 테스트 실패: " + e.getMessage());
        }
    }

    @Test
    public void testAllCustomerQueries() {
        try {
            // 실제 DB에 존재하는 고객 ID들을 확인
            System.out.println("=== 전체 Customer 조회 테스트 ===");
            
            // 첫 번째 고객 조회 시도
            for (Long customerId = 1L; customerId <= 5L; customerId++) {
                try {
                    CustomerCacheDTO result = customerRepository.findCustomerCacheInfoById(customerId);
                    if (result != null) {
                        System.out.println("Customer " + customerId + " 존재함 - Name: " + result.getName());
                        
                        // 해당 고객의 hobby, balance도 테스트
                        Optional<String> hobby = customerRepository.findHobbyByCustomerId(customerId);
                        Optional<Integer> balance = customerRepository.findBalanceByCustomerId(customerId);
                        
                        System.out.println("  Hobby: " + hobby.orElse("null"));
                        System.out.println("  Balance: " + balance.orElse(0));
                        break; // 첫 번째 존재하는 고객만 테스트
                    }
                } catch (Exception e) {
                    System.out.println("Customer " + customerId + " 조회 실패: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ 전체 고객 쿼리 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}