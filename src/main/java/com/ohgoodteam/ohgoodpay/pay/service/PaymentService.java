package com.ohgoodteam.ohgoodpay.pay.service;

import com.ohgoodteam.ohgoodpay.pay.dto.PaymentConfirmDTO;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentModalDTO;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentRequestDTO;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentResponseDTO;

public interface PaymentService {

    /**
     * QR, PIN 생성 요청 API
     * @param requestDto 결제 요청 정보
     * @return QR코드, PIN코드 정보 및 성공 여부
     */
    PaymentResponseDTO createCode(PaymentRequestDTO requestDto);

    /**
     * 만료 요청 API
     * @param requestId 결제 요청 ID
     * @return 요청 만료 처리 결과
     */
    PaymentResponseDTO expireCode(Long requestId);

    /**
     * QR/PIN 인증 요청 API (유효성 검사 + 모달 데이터 반환)
     * @param codeType qrcode 또는 pincode
     * @param value 코드 값
     * @param customerId 고객 ID
     * @return 결제 모달에 필요한 데이터
     */
    PaymentModalDTO validateCode(String codeType, String value, Long customerId);

    /**
     * 결제 모달 정보 요청 API
     * @param requestId 결제 요청 ID
     * @param customerId 고객 ID
     * @return 모달에 표시할 결제 정보
     */
    PaymentModalDTO getModalInfo(Long requestId, Long customerId);

    /**
     * 최종 결제 요청 API
     * @param customerId 고객 ID
     * @param point 사용 포인트
     * @param requestId 결제 요청 ID
     * @return 결제 결과 성공 여부
     */
    PaymentConfirmDTO finalPayment(Long customerId, int point, Long requestId);

    /**
     * 결제 확인 결과 요청 API
     * @param orderId 주문 ID
     * @return 결제 성공 여부
     */
    PaymentConfirmDTO checkPayment(String orderId);
}
