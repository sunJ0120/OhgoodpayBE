package com.ohgoodteam.ohgoodpay.pay.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ohgoodteam.ohgoodpay.pay.dto.PointHistoryDTO;
import com.ohgoodteam.ohgoodpay.pay.service.PointHistoryService;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PointHistoryController {
    
    private final PointHistoryService pointHistoryService;
    private final JWTUtil jwtUtil;
    /**
     * [GET] /api/point/history/{customerId}
     * @param customerId
     * @return 포인트 내역 리스트
     */
    @GetMapping("/point/history")
    public ResponseEntity<List<PointHistoryDTO>> getPointHistory(HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        List<PointHistoryDTO> pointHistoryDTOs = pointHistoryService.getPointHistory(Long.parseLong(customerId));
        return new ResponseEntity<>(pointHistoryDTOs, HttpStatus.OK);
    }
}
