package com.ohgoodteam.ohgoodpay.recommend.controller;

import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiErrorResponse;
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
    @Operation(summary = "채팅 API", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅 시작 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("")
    public ApiResponseWrapper<BasicChatResponseDTO> startChat(
            @Parameter(description = "채팅 시작 요청 (고객 ID 포함)")
            @RequestBody ChatStartRequestDTO request
    ) {
        try {
            BasicChatResponseDTO resp = chatService.chat(request.getCustomerId());
            return ApiResponseWrapper.ok(resp);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

//    //개인화된 상품 추천 api
//    @Operation(summary = "고객 상품 추천", description = "고객 종합 정보를 바탕으로 최종 상품 추천")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "상품 추천 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 고객 ID",
//                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
//                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
//    })
//    @PostMapping("/recommend")
//    public ApiResponseWrapper<ChatRecommendResponseDTO> recommendsChat(
//            @Parameter(description = "고객 맞춤형 상품 추천 요청")
//            @RequestBody ChatRecommendRequestDTO request
//    ){
//        // TODO : require에 따른 if문 분기 필요.....이건 고민중
//        try {
//            ChatRecommendResponseDTO resp = chatService.recommend(request.getCustomerId());
//            return ApiResponseWrapper.ok(resp);
//        } catch (IllegalArgumentException e) {
//            return ApiResponseWrapper.error(400, e.getMessage());
//        } catch (Exception e) {
//            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
//        }
//    }
}
