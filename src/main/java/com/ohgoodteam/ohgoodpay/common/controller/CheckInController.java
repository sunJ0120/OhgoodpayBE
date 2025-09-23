package com.ohgoodteam.ohgoodpay.common.controller;

import com.ohgoodteam.ohgoodpay.common.service.CheckInService;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkin")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class CheckInController {

    private final CheckInService checkInService;
    private final JWTUtil jwtUtil;
    /**
     * [POST] /api/checkin/roulette
     * 출석 체크 포인트 적립
     *
     * @param point 룰렛을 통해 획득한 포인트 값
     * @return 적립된 포인트 값
     */
    @PostMapping("/roulette")
    public int rouletteCheckIn(HttpServletRequest request, @RequestParam int point) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        checkInService.saveCheckInPoint(point, Long.parseLong(customerId));
        return point;
    }

    /**
     * [GET] /api/checkin/today
     * 출석 체크 여부 확인 : 오늘 이미 출석 체크를 했는지 확인
     *
     * @param customerId 고객 ID
     * @return true: 이미 출석 체크 완료, false: 출석 체크 안 함
     */
    @GetMapping("/today")
    public boolean checkToday(HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        return checkInService.hasCheckedInToday(Long.parseLong(customerId));
    }
}