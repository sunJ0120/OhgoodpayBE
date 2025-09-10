package com.ohgoodteam.ohgoodpay.pay.controller;

import com.ohgoodteam.ohgoodpay.pay.dto.*;
import com.ohgoodteam.ohgoodpay.pay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * [POST] /api/payment/requestment
     * 결제 요청 생성 : QR코드/Pin코드 발급
     * @param dto 결제요청 생성에 필요한 정보
     * @return 생성된 요청의 ID, QR/PIN 코드 등
     */
    @PostMapping("/requestment")
    public PaymentResponseDto createCode(@RequestBody PaymentRequestDto dto) {
        return paymentService.createCode(dto);
    }

    /**
     * [POST] /api/payment/expiration/{id}
     * 특정 결제요청을 만료 처리(isValidated=false)
     * @param id 만료할 결제요청 ID
     * @return 만료 처리 결과
     */
    @PostMapping("/expiration/{id}")
    public PaymentResponseDto expireCode(@PathVariable Long id) {
        return paymentService.expireCode(id);
    }

    /**
     *[POST] /api/payment/validate
     * QR 또는 Pin코드 검증
     * @param req 코드유형/값/고객ID를 담은 요청 DTO
     * @return 상점명, 총금액, 보유포인트, 잔액/한도, requestId 등 모달 표시 데이터
     */
    @PostMapping("/validate")
    public PaymentModalDto validateCode(@RequestBody ValidateRequestDto req) {
        return paymentService.validateCode(
                req.getCodeType(),
                req.getValue(),
                req.getCustomerId()
        );
    }
    /**
     * [GET] /api/payment/modal/{requestId}/{customerId}
     * @param requestId 결제요청 ID
     * @param customerId 고객 ID
     * @return 모달 표시 데이터
     */
    @GetMapping("/modal/{requestId}/{customerId}")
    public PaymentModalDto getModalInfo(@PathVariable Long requestId, @PathVariable Long customerId) {
        return paymentService.getModalInfo(requestId, customerId);
    }

    /**
     * [POST] /api/payment/final
     * 최종 결제 수행, @RequestParam으로 쿼리스트링으로 받음
     * @param customerId 고객 ID
     * @param point 사용할 포인트
     * @param requestId 결제요청 ID
     * @return 성공/실패 결과
     */
    @PostMapping("/final")
    public PaymentConfirmDto finalPayment(@RequestParam Long customerId,
                                          @RequestParam int point,
                                          @RequestParam Long requestId) {
        return paymentService.finalPayment(customerId, point, requestId);
    }

    /**
     * [GET] /api/payment/check/{orderId}
     * 주문ID 기준으로 결제 결과 조회
     * @param orderId 주문 ID
     * @return 결제 존재 여부(success/result=true/false)
     *
     */
    @GetMapping("/check/{orderId}")
    public PaymentConfirmDto checkPayment(@PathVariable String orderId) {
        return paymentService.checkPayment(orderId);
    }
}
