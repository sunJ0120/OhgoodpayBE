package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SpendingAnalyzeDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.DashAiClient;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentViewDTO;
import com.ohgoodteam.ohgoodpay.pay.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpendingAnalyzeServiceImpl implements SpendingAnalyzeService {

    private final PaymentRepository paymentRepository;
    private final DashAiClient ai;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter YMD = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional(readOnly = true)
    public SpendingAnalyzeResponseDTO analyze(SpendingAnalyzeRequestDTO req) {
        Long customerId = req.getCustomerId();
        int months = (req.getWindowMonths() == null || req.getWindowMonths() <= 0) ? 3 : req.getWindowMonths();

        // 최근 3개월 (당월은 '지금까지')
        LocalDateTime now = LocalDateTime.now(KST);
        YearMonth startYm = YearMonth.from(now).minusMonths(months - 1);
        LocalDateTime start = startYm.atDay(1).atStartOfDay();
        LocalDateTime end   = now;

        //  RAW 거래 조회
        List<PaymentViewDTO> rows = paymentRepository.findRecentByCustomer(customerId, start, end);

        // FastAPI 요청 In으로 매핑
        var tx = rows.stream().map(v ->
                SpendingAnalyzeDTO.In.Transaction.builder()
                        .paymentId(v.paymentId())
                        .date(v.date().toLocalDate().toString())     // "YYYY-MM-DD"
                        .requestName(v.requestName())
                        .amount(BigDecimal.valueOf(v.totalPrice()))
                        .build()
        ).toList();

        SpendingAnalyzeDTO.In payload = SpendingAnalyzeDTO.In.builder()
                .customerId(customerId)
                .timezone("Asia/Seoul")
                .transactions(tx)
                .build();

        // FastAPI 호출 → Out 수신
        SpendingAnalyzeDTO.Out out = ai.analyzeSpending(payload);

        // Out → 프론트 계약(SpendingAnalyzeResponseDTO)으로 변환
        SpendingAnalyzeResponseDTO.Summary summary =
                SpendingAnalyzeResponseDTO.Summary.builder()
                        .total_months(out.getSummary().getTotalMonths())
                        .date_range(
                                SpendingAnalyzeResponseDTO.DateRange.builder()
                                        .start(out.getSummary().getDateRange().getStart())
                                        .end(out.getSummary().getDateRange().getEnd())
                                        .build()
                        )
                        .build();

        Map<String, SpendingAnalyzeResponseDTO.MonthlyData> monthlyData = new LinkedHashMap<>();
        if (out.getMonthlyData() != null) {
            for (Map.Entry<String, SpendingAnalyzeDTO.Out.MonthlyData> e : out.getMonthlyData().entrySet()) {
                SpendingAnalyzeDTO.Out.MonthlyData md = e.getValue();

                // 카테고리 매핑
                Map<String, SpendingAnalyzeResponseDTO.CategoryStat> cats = new LinkedHashMap<>();
                if (md.getCategories() != null) {
                    for (Map.Entry<String, SpendingAnalyzeDTO.Out.CategoryStat> ce : md.getCategories().entrySet()) {
                        var cs = ce.getValue();
                        cats.put(ce.getKey(),
                                SpendingAnalyzeResponseDTO.CategoryStat.builder()
                                        .amount(cs.getAmount())
                                        .share(cs.getShare())
                                        .rank(cs.getRank())
                                        .build()
                        );
                    }
                }

                // Top 거래 매핑
                var topTx = (md.getTopTransactions() == null) ? List.<SpendingAnalyzeResponseDTO.TopTransaction>of()
                        : md.getTopTransactions().stream()
                        .map(t -> SpendingAnalyzeResponseDTO.TopTransaction.builder()
                                .payment_id(t.getPaymentId())
                                .request_name(t.getRequestName())
                                .amount(t.getAmount())
                                .date(t.getDate())
                                .category(t.getCategory())
                                .build())
                        .collect(Collectors.toList());

                monthlyData.put(e.getKey(),
                        SpendingAnalyzeResponseDTO.MonthlyData.builder()
                                .total_spend(md.getTotalSpend())
                                .categories(cats)
                                .top_transactions(topTx)
                                .build()
                );
            }
        }

        return SpendingAnalyzeResponseDTO.builder()
                .summary(summary)
                .monthly_data(monthlyData)
                .build();
    }
}
