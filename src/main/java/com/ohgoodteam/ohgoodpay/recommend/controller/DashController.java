package com.ohgoodteam.ohgoodpay.recommend.controller;

import com.ohgoodteam.ohgoodpay.recommend.dto.*;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.AdviceDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.DashAdviceService;
import com.ohgoodteam.ohgoodpay.recommend.service.PayThisMonthService;
import com.ohgoodteam.ohgoodpay.recommend.service.SayMyNameService;
import com.ohgoodteam.ohgoodpay.recommend.service.SpendingAnalyzeService;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/dash", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@Tag(name = "Dash")
public class DashController {

    private final SayMyNameService sayMyNameService;
    private final SpendingAnalyzeService spendingAnalyzeService;
    private final DashAdviceService dashAdviceService;
    private final PayThisMonthService service;

    @Operation(
            summary = "ohgoodscore 계산 및 한마디",
            description = "고객 식별자로 ohgoodscore를 계산하고 한마디 메시지를 생성합니다."
    )
    @PostMapping(value = "/saymyname", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseWrapper<DashSayMyNameResponseDTO>> sayMyName(
            @RequestBody @Valid DashSayMyNameRequestDTO req
    ) {
        var out = sayMyNameService.execute(req.getCustomerId());
        return ResponseEntity.ok(ApiResponseWrapper.ok(out));
    }

    @Operation(
            summary = "결제 내역 카테고리 분류",
            description = "최근 N개월(기본 3개월) 결제 내역을 월별/카테고리별로 분석합니다."
    )
    @PostMapping(value = "/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseWrapper<SpendingAnalyzeResponseDTO>> analyze(
            @RequestBody @Valid SpendingAnalyzeRequestDTO req
    ) {
        var data = spendingAnalyzeService.analyze(req);
        return ResponseEntity.ok(ApiResponseWrapper.ok(data));
    }

    @Operation(
            summary = "맞춤 AI 조언",
            description = "고객의 최근 소비/상태를 기반으로 개인화된 조언 3가지를 반환합니다."
    )
    @PostMapping(value = "/advice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseWrapper<AdviceDTO.Out>> advice(
            @Valid @RequestBody AdviceRequestDTO req
    ) {
        var data = dashAdviceService.generate(req.getCustomerId());
        return ResponseEntity.ok(ApiResponseWrapper.ok(data));
    }

    @Operation(
            summary = "이번 달 결제 내역",
            description = "이번 달(오늘까지)의 결제 내역과 합계 정보를 반환합니다."
    )
    @GetMapping({"/customer/{customerId}/pay-this-month"})
    public ResponseEntity<ApiResponseWrapper<Object>> getThisMonth(@PathVariable Long customerId) {
        var data = service.getThisMonth(customerId);
        return ResponseEntity.ok(ApiResponseWrapper.ok(data));
    }
}