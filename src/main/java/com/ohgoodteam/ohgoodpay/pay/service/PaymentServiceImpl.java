package com.ohgoodteam.ohgoodpay.pay.service;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PaymentRequestEntity;
import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;
import com.ohgoodteam.ohgoodpay.pay.dto.*;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.pay.enums.GradePointRate;
import com.ohgoodteam.ohgoodpay.pay.repository.PaymentRepository;
import com.ohgoodteam.ohgoodpay.pay.repository.PaymentRequestRepository;
import com.ohgoodteam.ohgoodpay.pay.repository.PointHistoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String generateQrBase64(String text) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
    }

    // qr, pincode 중복 확인
    private String generateUniqueQr() {
        // 검증된 코드와 겹치면 재생성
        String qr;
        do {
            qr = UUID.randomUUID().toString();
        } while (paymentRequestRepository.existsByQrcodeAndIsValidatedTrue(qr));
        return qr;
    }

    private String generateUniquePin6() {
        String pin;
        do {
            // 6자리 숫자 100000~999999
            pin = String.valueOf(SECURE_RANDOM.nextInt(900000) + 100000);
        } while (paymentRequestRepository.existsByPincodeAndIsValidatedTrue(pin));
        return pin;
    }

    /**
     * 1. QR, PIN 생성 요청
     * 결제요청 생성 및 QR/Pin코드 발급
     * isValidated = true로 설정
     */
    @Override
    public PaymentResponseDTO createCode(PaymentRequestDTO requestDto) {
        try {
            String qr = generateUniqueQr();
            String pin = generateUniquePin6();

            PaymentRequestEntity saved = paymentRequestRepository.save(
                    PaymentRequestEntity.builder()
                            .orderId(requestDto.getOrderId())
                            .requestName(requestDto.getRequestName())
                            .totalPrice(requestDto.getTotalPrice())
                            .qrcode(qr)
                            .pincode(pin)
                            .isValidated(true)
                            .date(LocalDateTime.now())
                            .build()
            );

            // Base64 QR 코드 생성
            String qrBase64 = generateQrBase64(qr);

            return PaymentResponseDTO.builder()
                    .paymentRequestId(saved.getPaymentRequestId())
                    .qrCode(saved.getQrcode())
                    .qrImageUrl(qrBase64) // 이제 바로 <img src="..."> 가능
                    .pinCode(saved.getPincode())
                    .success(true)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("QR 코드 생성 실패: " + e.getMessage());
        }
    }


    /**
     * 2. 만료 요청
     */
    @Override
    @Transactional
    public PaymentResponseDTO expireCode(Long requestId) {
        int updated = paymentRequestRepository.expirePaymentRequest(requestId);
        if (updated == 0) {
            throw new RuntimeException("요청 없음");
        }
        // 최신 상태 조회
        PaymentRequestEntity entity = paymentRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("요청 없음"));

        return PaymentResponseDTO.builder()
                .paymentRequestId(entity.getPaymentRequestId())
                .qrCode(entity.getQrcode())
                .pinCode(entity.getPincode())
                .success(true)
                .build();
    }
    /**
     * 3. QR/PIN 인증 요청
     * 코드(QR/Pin)이 유효한지 검증하고, 결제 모달에 필요한 정보 반환
     *
     */
    @Override
    public PaymentModalDTO validateCode(String codeType, String value, Long customerId) {
        PaymentRequestEntity entity = "qrcode".equalsIgnoreCase(codeType)
                ? paymentRequestRepository.findByQrcode(value).orElseThrow(() -> new RuntimeException("QR 없음"))
                : paymentRequestRepository.findByPincode(value).orElseThrow(() -> new RuntimeException("PIN 없음"));

        if (!entity.isValidated()) {
            throw new RuntimeException("유효하지 않은 코드");
        }

        CustomerEntity customer = customerRepository.findByCustomerId(customerId);
        if (customer == null) throw new RuntimeException("고객 없음");

        return PaymentModalDTO.builder()
                .requestName(entity.getRequestName())
                .price(entity.getTotalPrice())
                .point(customer.getPoint())
                .balance(customer.getBalance())
                .requestId(entity.getPaymentRequestId())
                .build();
    }
    // 4. 결제 모달 정보 요청
    @Override
    public PaymentModalDTO getModalInfo(Long requestId, Long customerId) {
        PaymentRequestEntity request = paymentRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("요청 없음"));
        CustomerEntity customer = customerRepository.findByCustomerId(customerId);

        return PaymentModalDTO.builder()
                .requestName(request.getRequestName())
                .price(request.getTotalPrice())
                .point(customer.getPoint())
                .build();
    }
    /**
     * 5. 최종 결제 요청
     * 사전 잔액 확인, 포인트 차감, 잔액(한도) 차감, 결제내역 저장(Payment), 등급별 포인트 적립
     * isValidated=false
     * */
    @Override
    @Transactional
    public PaymentConfirmDTO finalPayment(Long customerId, int point, Long requestId) {
        PaymentRequestEntity request = paymentRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("요청 없음"));
        CustomerEntity customer = customerRepository.findByCustomerId(customerId);

        int totalPrice = request.getTotalPrice();
        int actualPrice = totalPrice - point; // 포인트 사용 후 실결제 금액

        // 사전 잔액 확인
        if (customer.getBalance() < actualPrice) {
            return PaymentConfirmDTO.builder()
                    .success(false)
                    .result(false)
                    .build();
        }
        // 포인트 차감
        if (point > 0) {
            if ( customer.getPoint() - point >= 0 ) {
                customerRepository.minusCustomerPoint(point, customerId);
                pointHistoryRepository.save(PointHistoryEntity.builder()
                        .customer(customer)
                        .point(-point)
                        .pointExplain("포인트 사용")
                        .date(LocalDateTime.now())
                        .build());
            } else {
                return PaymentConfirmDTO.builder()
                        .success(false)
                        .result(false)
                        .build();
            }
        }
        // 잔액 차감 (엔티티 메서드 사용) — @Transactional로 커밋 시 DB 반영
        try {
            if (customer.getBalance() - actualPrice >= 0) {
                customerRepository.minusCustomerBalance(actualPrice, customerId);
            } else {
                return PaymentConfirmDTO.builder()
                        .success(false)
                        .result(false)
                        .build();
            }
        } catch (IllegalArgumentException e) {
            // 경합 등으로 잔액이 부족해진 경우 방어
            return PaymentConfirmDTO.builder()
                    .success(false)
                    .result(false)
                    .build();
        }
        // 결제 내역 저장
        paymentRepository.save(PaymentEntity.builder()
                .customer(customer)
                .paymentRequest(request)
                .totalPrice(totalPrice)
                .price(actualPrice)
                .point(point)
                .requestName(request.getRequestName())
                .date(LocalDateTime.now())
                .isExpired(false)
                .build());

        // 등급별 포인트 적립 (엔티티 메서드 사용)
        double rate = GradePointRate.getRateByGrade(customer.getGrade().getGradeName());
        int earnedPoint = (int) Math.floor(actualPrice * rate);
        if (earnedPoint > 0) {
            customerRepository.plusCustomerPoint(earnedPoint, customerId);
            pointHistoryRepository.save(PointHistoryEntity.builder()
                    .customer(customer)
                    .point(earnedPoint)
                    .pointExplain("결제 적립")
                    .date(LocalDateTime.now())
                    .build());
        }
        paymentRequestRepository.expirePaymentRequest(requestId);
        // 성공 반환
        return PaymentConfirmDTO.builder()
                .success(true)
                .result(true)
                .build();
    }
    // 6. 결제 결과 확인
    @Override
    public PaymentConfirmDTO checkPayment(String orderId) {
        boolean exists = paymentRepository.findByPaymentRequest_OrderId(orderId).isPresent();
        return PaymentConfirmDTO.builder()
                .success(exists)
                .result(exists)
                .build();
    }
}
