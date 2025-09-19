package com.ohgoodteam.ohgoodpay.scheduled.service;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.repository.PaymentRepository;

import jakarta.transaction.Transactional;

import java.time.YearMonth;
import java.util.List;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;

@Service
@RequiredArgsConstructor
public class ScheduledServiceImpl implements ScheduledService {
    
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    /**
     * 매월 1일 등급 업데이트, 등급으로 한도 초기화
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")
    public synchronized void updateCustomerGrade() {
        List<CustomerEntity> customers = customerRepository.findAll();
        for (CustomerEntity customer : customers) {
            if (customer.getGradePoint() >= 90) {
                customerRepository.updateCustomerGradeName("diamond", customer.getCustomerId());
                customerRepository.updateCustomerBalance(300000, customer.getCustomerId());
            } else if (customer.getGradePoint() >= 60) {
                customerRepository.updateCustomerGradeName("platinum", customer.getCustomerId());
                customerRepository.updateCustomerBalance(250000, customer.getCustomerId());
            } else if (customer.getGradePoint() >= 40) {
                customerRepository.updateCustomerGradeName("gold", customer.getCustomerId());
                customerRepository.updateCustomerBalance(200000, customer.getCustomerId());
            } else if (customer.getGradePoint() >= 20) {
                customerRepository.updateCustomerGradeName("silver", customer.getCustomerId());
                customerRepository.updateCustomerBalance(150000, customer.getCustomerId());
            } else {
                customerRepository.updateCustomerGradeName("bronze", customer.getCustomerId());
                customerRepository.updateCustomerBalance(100000, customer.getCustomerId());
            }
        }
    }

    /**
     * 매월 1일 제재 적용
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")
    public synchronized void applyPenalty() {
        List<CustomerEntity> customers = customerRepository.findByIsExtensionTrue();
        for (CustomerEntity customer : customers) {
            customerRepository.updateCustomerIsBlocked(true, customer.getCustomerId());
        }
    }

    /**
     * 매월 16일 자동 연장 업데이트
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 16 * *")
    public synchronized void updateCustomerAutoExtension() {
        List<CustomerEntity> customers = customerRepository.findByIsExtensionFalse();
        for (CustomerEntity customer : customers) {
            List<PaymentEntity> payments = paymentRepository.findByCustomerCustomerIdAndIsExpiredFalseOrderByDateAsc(customer.getCustomerId());
            if (payments.size() > 0) {
                if(YearMonth.from(payments.get(0).getDate()).equals(YearMonth.now())) {
                    customerRepository.updateCustomerIsExtension(true, customer.getCustomerId());
                }
            }
        }
    }

}
