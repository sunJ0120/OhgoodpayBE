package com.ohgoodteam.ohgoodpay.shorts.dto.response;
import lombok.*;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortsCommonResponse {
    /**
     * 쇼츠 공통 응답 DTO
     */
    private String resultCode;
    private String resultMsg;
    private List<?> data;

}
