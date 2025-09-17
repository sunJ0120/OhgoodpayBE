package com.ohgoodteam.ohgoodpay.pay.controller;

import com.ohgoodteam.ohgoodpay.pay.dto.*;
import com.ohgoodteam.ohgoodpay.pay.service.PaymentService;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// @CrossOrigin(
//     origins = "http://localhost:5173", 
//     allowedHeaders = "*",
//     methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
// )
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JWTUtil jwtUtil;
    /**
     * [POST] /api/payment/requestment
     * 결제 요청 생성 : QR코드/Pin코드 발급
     * @param dto 결제요청 생성에 필요한 정보
     * @return 생성된 요청의 ID, QR/PIN 코드 등
     */
    @PostMapping("/public/payment/requestment")
    public PaymentResponseDTO createCode(@RequestBody PaymentRequestDTO dto) {
        return paymentService.createCode(dto);
    }

    /**
     * [POST] /api/payment/expiration/{id}
     * 특정 결제요청을 만료 처리(isValidated=false)
     * @param id 만료할 결제요청 ID
     * @return 만료 처리 결과
     */
    @PostMapping("/public/payment/expiration/{id}")
    public PaymentResponseDTO expireCode(@PathVariable Long id) {
        return paymentService.expireCode(id);
    }

    /**
     *[POST] /api/payment/validate
     * QR 또는 Pin코드 검증
     * @param req 코드유형/값/고객ID를 담은 요청 DTO
     * @return 상점명, 총금액, 보유포인트, 잔액/한도, requestId 등 모달 표시 데이터
     */
    @PostMapping("/payment/validate")
    public PaymentModalDTO validateCode(@RequestBody ValidateRequestDTO req, HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        return paymentService.validateCode(
                req.getCodeType(),
                req.getValue(),
                Long.parseLong(customerId)
        );
    }
    /**
     * [GET] /api/payment/modal/{requestId}/{customerId}
     * @param requestId 결제요청 ID
     * @param customerId 고객 ID
     * @return 모달 표시 데이터
     */
    // @GetMapping("/payment/modal/{requestId}/{customerId}")
    // public PaymentModalDTO getModalInfo(@PathVariable Long requestId, @PathVariable Long customerId) {
    //     return paymentService.getModalInfo(requestId, customerId);
    // }

    /**
     * [POST] /api/payment/final
     * 최종 결제 수행, @RequestParam으로 쿼리스트링으로 받음
     * @param customerId 고객 ID
     * @param point 사용할 포인트
     * @param requestId 결제요청 ID
     * @return 성공/실패 결과
     */
    @PostMapping("/payment/final")
    public PaymentConfirmDTO finalPayment(HttpServletRequest request,
                                          @RequestParam int point,
                                          @RequestParam Long requestId) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        return paymentService.finalPayment(Long.parseLong(customerId), point, requestId);
    }

    /**
     * [GET] /api/payment/check/{orderId}
     * 주문ID 기준으로 결제 결과 조회
     * @param orderId 주문 ID
     * @return 결제 존재 여부(success/result=true/false)
     *
     */
    @GetMapping("/public/payment/check/{orderId}")
    public PaymentConfirmDTO checkPayment(@PathVariable String orderId) {
        return paymentService.checkPayment(orderId);
    }
}
