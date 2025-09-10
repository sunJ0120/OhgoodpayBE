package com.ohgoodteam.ohgoodpay.recommend.controller;

import com.ohgoodteam.ohgoodpay.recommend.util.ApiErrorResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.*;
import com.ohgoodteam.ohgoodpay.recommend.service.ChatService;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 추천 채팅 API
 *
 * v1: Mock 데이터로 구현, 향후 Redis + FastAPI 연동 예정
 */
@Tag(name = "추천 채팅", description = "AI 추천 챗봇 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    // 겉으로 나오는 Service는 ChatService, 내부적으로 RecommendationService, CustomerService, ProductService 사용
    private final ChatService chatService;

    //채팅 시작 api
    @Operation(summary = "채팅 시작", description = "고객 ID로 채팅 시작 후 개인화된 인사 메시지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/start")
    public ApiResponseWrapper<ChatMessageResponseDTO> startChat(
            @Parameter(description = "채팅 시작 요청 (고객 ID 포함)")
            @RequestBody ChatStartRequestDTO request
    ) {
        try {
            ChatMessageResponseDTO resp = chatService.startChat(request.getCustomerId());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

    //기분 받는 api
    @Operation(summary = "사용자 기분 input", description = "고객의 기분을 입력 받은 후, 개인화된 인사 메시지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기분 받기 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/mood")
    public ApiResponseWrapper<ChatMessageResponseDTO> moodChat(
            @Parameter(description = "기분 입력 요청 (고객 ID와 기분 포함)")
            @RequestBody ChatMoodRequestDTO request
    ){
        try {
            ChatMessageResponseDTO resp = chatService.moodChat(request.getCustomerId(), request.getMood());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

    //취미 확인 api
    @Operation(summary = "취미 확인", description = "고객 ID로 DB에 있는 취미 확인 후 개인화된 인사 메시지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취미 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/hobby/check")
    public ApiResponseWrapper<ChatCheckHobbyResponseDTO> checkHobbyChat(
            @Parameter(description = "취미 확인 요청 (고객 ID 포함)")
            @RequestBody ChatStartRequestDTO request
    ){
        try {
            ChatCheckHobbyResponseDTO resp = chatService.checkHobby(request.getCustomerId());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

    //취미 업데이트 api
    @Operation(summary = "취미 업데이트", description = "고객이 입력한 취미로 업데이트 후, 개인화된 인사 메시지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "취미 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/hobby/update")
    public ApiResponseWrapper<ChatUpdateHobbyResponseDTO> updateHobbyChat(
            @Parameter(description = "취미 업데이트 요청 (고객 ID와 새로 입력 받은 취미 포함)")
            @RequestBody ChatUpdateHobbyRequestDTO request
    ){
        try {
            ChatUpdateHobbyResponseDTO resp = chatService.updateHobby(request.getCustomerId(), request.getNewHobby());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

    //최근 구매 카테고리 분석 & 확인 api
    @Operation(summary = "최근 구매 카테고리 분석", description = "고객이 최근에 구매한 제품의 카테고리 가져온 후, 개인화된 인사 메시지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 분석 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/analyze/purchases")
    public ApiResponseWrapper<ChatAnalyzePurchasesResponseDTO> analyzePurchasesChat(
            @Parameter(description = "최근 구매 카테고리 분석 요청 (고객 ID와 새로 입력 받은 취미 포함)")
            @RequestBody ChatStartRequestDTO request
    ){
        try {
            ChatAnalyzePurchasesResponseDTO resp = chatService.analyzePurchases(request.getCustomerId());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

    //개인화된 상품 추천 api
    @Operation(summary = "고객 상품 추천", description = "고객 종합 정보를 바탕으로 최종 상품 추천")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/recommend")
    public ApiResponseWrapper<ChatRecommendResponseDTO> recommendsChat(
            @Parameter(description = "고객 맞춤형 상품 추천 요청")
            @RequestBody ChatRecommendRequestDTO request
    ){
        // TODO : require에 따른 if문 분기 필요.....이건 고민중
        try {
            ChatRecommendResponseDTO resp = chatService.recommend(request.getCustomerId());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }
}
