package com.ohgoodteam.ohgoodpay.recommend.controller;


import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponseDTO;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;
import com.ohgoodteam.ohgoodpay.recommend.service.SayMyNameService;
//import com.ohgoodteam.ohgoodpay.recommend.service.SpendingAnalyzeService;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
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
public class DashController {

    private final SayMyNameService sayMyNameService;
//    private final SpendingAnalyzeService spendingAnalyzeService;

    @PostMapping("/saymyname")
    public ResponseEntity<ApiResponseWrapper<DashSayMyNameResponseDTO>> sayMyName(
            @RequestBody @Valid DashSayMyNameRequestDTO req
    ) {
        var out = sayMyNameService.execute(req.getCustomerId());
        return ResponseEntity.ok(ApiResponseWrapper.ok(out));
    }


//    @Operation(summary = "사용자 3개월 결제내역 input", description = "사용자 3개월 결제 정보를 바탕으로 카테고리 분류 및 소비 패턴 분석")
//    @PostMapping(value="/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ApiResponseWrapper<SpendingAnalyzeResponse> analyze(@RequestBody @Valid SpendingAnalyzeRequest body) {
//        return ApiResponseWrapper.ok(spendingAnalyzeService.execute(body)); // 이 경우 A로 가야 함
//    }
//
//    @PostMapping(value="/analyze", params="customerId")
//    public ApiResponseWrapper<SpendingAnalyzeResponse> analyzeByCustomerId(@RequestParam Long customerId) {
//        try {
//            return ApiResponseWrapper.ok(spendingAnalyzeService.execute(customerId));
//        } catch (IllegalArgumentException e) { return ApiResponseWrapper.error(400, e.getMessage()); }
//        catch (IllegalStateException e)  { return ApiResponseWrapper.error(502, e.getMessage()); }
//        catch (Exception e)              { return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다"); }
//    }
}