package com.ohgoodteam.ohgoodpay.common.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

@SpringBootTest
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFindByExtensionTrue() {
        List<CustomerEntity> customers = customerRepository.findByIsExtensionTrue();
        System.out.println("--------------------------------");
        System.out.println(customers);
        System.out.println("--------------------------------");
    }
}
