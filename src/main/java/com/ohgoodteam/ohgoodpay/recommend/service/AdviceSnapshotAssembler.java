package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.AdviceDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class AdviceSnapshotAssembler {

    public static AdviceDTO.In fromResponses(Long customerId,
                                             DashSayMyNameResponseDTO say,
                                             SpendingAnalyzeResponseDTO spend) {

        var identity = AdviceDTO.In.Identity.builder()
                .customerId(customerId)
                .username(nullSafe(say::getSessionId))
                .tier(null)
                .gradePoint(say.getGradePoint())
                .autoExtensionThisMonth(say.isAutoExtensionThisMonth())
                .autoExtensionCnt12m(say.getAutoExtensionCnt12m())
                .blocked(say.isBlocked())
                .paymentCnt12m(say.getPaymentCnt12m())
                .paymentAmount12m(toBD(say.getPaymentAmount12m()))
                .currentCycleSpend(toBD(say.getCurrentCycleSpend()))
                .build();

        var start = opt(() -> spend.getSummary().getDate_range().getStart());
        var end   = opt(() -> spend.getSummary().getDate_range().getEnd());
        var md    = spend.getMonthly_data();

        // date_range: null 허용 (키만 조건부로 넣기)
        var dateRange = new java.util.LinkedHashMap<String, String>();
        if (start != null) dateRange.put("start", start);
        if (end   != null) dateRange.put("end", end);

        var latest = latestMonth(md);
        var latestBlock = latest == null ? null : md.get(latest);
        var latestTotal = latestBlock == null ? null : latestBlock.getTotal_spend();

        var topCats = latestBlock == null ? List.<AdviceDTO.In.Spending.Category>of()
                : latestBlock.getCategories().entrySet().stream()
                .sorted((a,b) -> b.getValue().getAmount().compareTo(a.getValue().getAmount()))
                .limit(3)
                .map(e -> AdviceDTO.In.Spending.Category.builder()
                        .category(e.getKey())
                        .amount(e.getValue().getAmount())
                        .share(e.getValue().getShare())
                        .build())
                .collect(Collectors.toList());

        var shareMap = latestBlock == null ? Map.<String, Double>of()
                : latestBlock.getCategories().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getShare()));

        var spending = AdviceDTO.In.Spending.builder()
                .dateRange(dateRange)
                .latestMonth(latest)
                .latestTotalSpend(latestTotal)
                .momGrowth(computeMomGrowth(spend))
                .spikeFlag(computeSpikeFlag(spend))
                .topCategoriesLatest(topCats)
                .categoriesShareLatest(shareMap)
                .build();

        return AdviceDTO.In.builder()
                .identity(identity)
                .spending(spending)
                .build();
    }

    // --- 계산/유틸 ---

    private static Double computeMomGrowth(SpendingAnalyzeResponseDTO spend) {
        var md = spend.getMonthly_data();
        if (md == null || md.size() < 2) return null;
        var months = new ArrayList<>(md.keySet());
        Collections.sort(months);
        var last = md.get(months.get(months.size()-1)).getTotal_spend();
        var prev = md.get(months.get(months.size()-2)).getTotal_spend();
        if (last == null || prev == null || prev.compareTo(BigDecimal.ZERO) == 0) return null;
        return last.subtract(prev).divide(prev, 6, RoundingMode.HALF_UP)
                .setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private static Boolean computeSpikeFlag(SpendingAnalyzeResponseDTO spend) {
        var md = spend.getMonthly_data();
        if (md == null || md.isEmpty()) return false;
        var months = new ArrayList<>(md.keySet());
        Collections.sort(months);
        var window = months.subList(Math.max(0, months.size()-3), months.size());
        var vals = window.stream()
                .map(m -> md.get(m).getTotal_spend())
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .toList();
        if (vals.size() <= 1) return false;
        double mu = vals.stream().mapToDouble(x->x).average().orElse(0.0);
        double sd = Math.sqrt(vals.stream().mapToDouble(x->(x-mu)*(x-mu)).sum()/vals.size());
        if (sd == 0.0) return false;
        double z = (md.get(months.get(months.size()-1)).getTotal_spend().doubleValue()-mu)/sd;
        return z >= 1.5;
    }

    private static String latestMonth(Map<String, SpendingAnalyzeResponseDTO.MonthlyData> md) {
        if (md == null || md.isEmpty()) return null;
        var keys = new ArrayList<>(md.keySet());
        Collections.sort(keys);
        return keys.get(keys.size()-1);
    }

    private static BigDecimal toBD(double v) { return BigDecimal.valueOf(v); }
    private static <T> T nullSafe(SupplierEx<T> s){ try{return s.get();}catch(Exception e){return null;}}
    private static String opt(SupplierEx<String> s){ var v=nullSafe(s); return v==null? null:v;}
    @FunctionalInterface interface SupplierEx<T>{ T get() throws Exception; }
}
