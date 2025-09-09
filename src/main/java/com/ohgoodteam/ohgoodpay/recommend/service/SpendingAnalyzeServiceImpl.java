package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.pay.repository.PaymentRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.DashAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpendingAnalyzeServiceImpl implements SpendingAnalyzeService {

    private final DashAiClient dashAiClient;
    private final PaymentRepository paymentRepository; // 최근 3개월 승인 거래 조회

    @Override
    public SpendingAnalyzeResponse execute(SpendingAnalyzeRequest req) {
        return dashAiClient.analyzeSpending(req);
    }

    @Override
    public SpendingAnalyzeResponse execute(Long customerId) {
        //  구현) DB에서 최근 3개월 승인 거래 조회 → 아래 TxnIn 리스트로 매핑
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var txs = List.of(
                SpendingAnalyzeRequest.TxnIn.builder()
                        .id("T-001").ts(now.minusDays(2).toString()).amount(5800.0)
                        .currency("KRW").status("paid").merchantName("STARBUCKS GANGNAM")
                        .mcc("5814").channel("CARD").memo("AMERICANO").build(),
                SpendingAnalyzeRequest.TxnIn.builder()
                        .id("T-002").ts(now.minusDays(25).toString()).amount(82000.0)
                        .currency("KRW").status("paid").merchantName("EMART SEONGSU")
                        .mcc("5411").channel("CARD").memo("GROCERY").build()
        );

        var req = SpendingAnalyzeRequest.builder()
                .transactions(txs)
                .useLlmFallback(false)
                .build();

        return dashAiClient.analyzeSpending(req);
    }
}