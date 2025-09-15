package com.ohgoodteam.ohgoodpay.recommend.controller;


import com.ohgoodteam.ohgoodpay.recommend.dto.*;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.AdviceDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.DashAdviceService;
import com.ohgoodteam.ohgoodpay.recommend.service.PayThisMonthService;
import com.ohgoodteam.ohgoodpay.recommend.service.SayMyNameService;
//import com.ohgoodteam.ohgoodpay.recommend.service.SpendingAnalyzeService;
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
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/dash")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dash")
public class DashController {

    private final SayMyNameService sayMyNameService;
    private final SpendingAnalyzeService spendingAnalyzeService;
    private final DashAdviceService dashAdviceService;
    private final PayThisMonthService service;


    @Operation(summary = "ohgoodscore 계산", description = "고객 정보를 바탕으로 ohgoodscore 계산 후 한마디 반환하기")
    @PostMapping("/saymyname")
    public ResponseEntity<ApiResponseWrapper<DashSayMyNameResponseDTO>> sayMyName(
            @RequestBody @Valid DashSayMyNameRequestDTO req
    ) {
        var out = sayMyNameService.execute(req.getCustomerId());
        return ResponseEntity.ok(ApiResponseWrapper.ok(out));
    }

    @Operation(summary = "결제 내역 카테고리 분류", description = "고객 정보를 바탕으로 ohgoodscore 계산 후 한마디 반환하기")
    @PostMapping("/analyze")
    public ResponseEntity<ApiEnvelopeDTO<SpendingAnalyzeResponseDTO>> analyze(
            @RequestBody @Valid SpendingAnalyzeRequestDTO req
    ) {
        var data = spendingAnalyzeService.analyze(req);
        return ResponseEntity.ok(
                ApiEnvelopeDTO.<SpendingAnalyzeResponseDTO>builder()
                        .success(true)
                        .code("200")
                        .message("success")
                        .data(data)
                        .build()
        );
    }
    @Operation(summary = "맞춤 ai 조언", description = "고객 정보를 바탕으로 ai 조언 3개 반환하기")
    @PostMapping("/advice")
    public ResponseEntity<ApiEnvelopeDTO<AdviceDTO.Out>> advice(@Valid @RequestBody AdviceRequestDTO req) {
        AdviceDTO.Out data = dashAdviceService.generate(req.getCustomerId());
        return ResponseEntity.ok(
                ApiEnvelopeDTO.<AdviceDTO.Out>builder()
                        .success(true)
                        .code("200")
                        .message("success")
                        .data(data)
                        .build()
        );
    }
    @Operation(summary = "이번달 결제 내역", description = "이번달 결제 내역 반환하기")
    @GetMapping("/{customerId}/pay-this-month")
    public ResponseEntity<?> getThisMonth(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(service.getThisMonth(customerId)));
    }
}