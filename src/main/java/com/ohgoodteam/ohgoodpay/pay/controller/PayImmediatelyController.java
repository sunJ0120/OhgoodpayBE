package com.ohgoodteam.ohgoodpay.pay.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.dto.PayImmediatelyResponseDTO;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentResponseDTO;
import com.ohgoodteam.ohgoodpay.pay.service.PayImmediatelyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayImmediatelyController {

    private final PayImmediatelyService payImmediatelyService;
    private final CustomerRepository customerRepository;

    // 납부 페이지 진입시 초기 정보 조회
    @GetMapping("/payment/info/{customerId}")
    public ResponseEntity<PayImmediatelyResponseDTO> getPayImmediately(@PathVariable Long customerId) {
        GradeEntity grade = payImmediatelyService.findbyCustomerGradeName(customerId);
        List<List<PaymentEntity>> unpaidBills = payImmediatelyService.classifyUnpaidBills(customerId);
        List<List<PaymentResponseDTO>> unpaidBillsResponseDTO = new ArrayList<>();

        for (List<PaymentEntity> unpaidBill : unpaidBills) {
            List<PaymentResponseDTO> paymentResponseDTOList = new ArrayList<>();
            for (PaymentEntity payment : unpaidBill) {
                PaymentResponseDTO paymentResponseDTO = payImmediatelyService.entityToDto(payment);
                paymentResponseDTOList.add(paymentResponseDTO);
            }
            unpaidBillsResponseDTO.add(paymentResponseDTOList);
        }

        PayImmediatelyResponseDTO response = PayImmediatelyResponseDTO.builder()
            .customerId(customerId)
            .gradeName(grade.getGradeName())
            .limitPrice(grade.getLimitPrice())
            .pointPercent(0.01f)
            .account(customerRepository.findByCustomerId(customerId).getAccount())
            .accountName(customerRepository.findByCustomerId(customerId).getAccountName())
            .balance(customerRepository.findByCustomerId(customerId).getBalance())
            .isExtension(customerRepository.findByCustomerId(customerId).isExtension())
            .isAuto(customerRepository.findByCustomerId(customerId).isAuto())
            .unpaidBills(unpaidBillsResponseDTO)
            .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 즉시 납부 요청
    @PostMapping("/payment/immediately/{customerId}")
    public ResponseEntity<PayImmediatelyResponseDTO> payImmediately(@PathVariable Long customerId, @RequestBody Long[] lists) {
        boolean isPayImmediately = payImmediatelyService.payImmediately(customerId, lists);
        if(isPayImmediately) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 수동 연장 요청
    @PostMapping("/payment/extension/{customerId}")
    public ResponseEntity<PayImmediatelyResponseDTO> requestCustomerExtension(@PathVariable Long customerId) {
        boolean isRequestCustomerExtension = payImmediatelyService.requestCustomerExtension(customerId);
        if(isRequestCustomerExtension) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 결제 상세 내역 페이지 진입시 초기 정보 조회
    @GetMapping("/payment/history/{customerId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentDetail(@PathVariable Long customerId) {
        List<PaymentEntity> payments = payImmediatelyService.getAllPayment(customerId);
        List<PaymentResponseDTO> paymentResponseDTOs = new ArrayList<>();
        for (PaymentEntity payment : payments) {
            PaymentResponseDTO paymentResponseDTO = payImmediatelyService.entityToDto(payment);
            paymentResponseDTOs.add(paymentResponseDTO);
        }
        List<PaymentResponseDTO> response = paymentResponseDTOs;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
