package com.ohgoodteam.ohgoodpay.pay.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.dto.PayImmediatelyResponseDTO;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentDTO;
import com.ohgoodteam.ohgoodpay.pay.service.PayImmediatelyService;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayImmediatelyController {

    private final PayImmediatelyService payImmediatelyService;
    private final JWTUtil jwtUtil;

    // 납부 페이지 진입시 초기 정보 조회
    @GetMapping("/payment/info")
    public ResponseEntity<PayImmediatelyResponseDTO> getPayImmediately(HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);

        PayImmediatelyResponseDTO response = payImmediatelyService.classifyUnpaidBills(Long.parseLong(customerId));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 즉시 납부 요청
    @PostMapping("/payment/immediately")
    public ResponseEntity<PayImmediatelyResponseDTO> payImmediately(HttpServletRequest request, @RequestBody Long[] lists) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        boolean isPayImmediately = payImmediatelyService.payImmediately(Long.parseLong(customerId), lists);
        if(isPayImmediately) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 수동 연장 요청
    @PostMapping("/payment/extension")
    public ResponseEntity<PayImmediatelyResponseDTO> requestCustomerExtension(HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        boolean isRequestCustomerExtension = payImmediatelyService.requestCustomerExtension(Long.parseLong(customerId));
        if(isRequestCustomerExtension) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 결제 상세 내역 페이지 진입시 초기 정보 조회
    @GetMapping("/payment/history")
    public ResponseEntity<List<PaymentDTO>> getPaymentDetail(HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        List<PaymentDTO> response = payImmediatelyService.getAllPayment(Long.parseLong(customerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
