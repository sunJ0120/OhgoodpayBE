package com.ohgoodteam.ohgoodpay.pay.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;

@SpringBootTest
public class PayImmediatelyTests {

    // private final CustomerRepository customerRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    // 고객의 결제 내역 조회
    @Test
    public void testFindByCustomerId() {
        // CustomerEntity customer = customerRepository.findById(1L).orElseThrow(() -> new RuntimeException("Customer not found"));
        List<PaymentEntity> payments = paymentRepository.findByCustomerCustomerId(1L);
        System.out.println("--------------------------------");
        for (PaymentEntity payment : payments) {
            System.out.println(payment);
        }
        System.out.println("--------------------------------");
    }

    // 결제 단일 건 조회
    @Test
    public void testFindByPaymentId() {
        PaymentEntity payment = paymentRepository.findByPaymentId(1L);
        System.out.println("--------------------------------");
        System.out.println(payment);
        System.out.println("--------------------------------");
    }
}
