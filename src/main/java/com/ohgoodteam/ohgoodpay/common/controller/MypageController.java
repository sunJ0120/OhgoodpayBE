package com.ohgoodteam.ohgoodpay.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ohgoodteam.ohgoodpay.common.dto.MypageDTO;
import com.ohgoodteam.ohgoodpay.common.service.MypageService;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
    origins = "http://localhost:5173", 
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class MypageController {
    
    private final MypageService mypageService;
    private final JWTUtil jwtUtil;

    @GetMapping("/mypage")
    public ResponseEntity<MypageDTO> getMypage(HttpServletRequest request) throws Exception {
        String customerId = jwtUtil.extractCustomerId(request);
        MypageDTO mypageDTO = mypageService.getMypageInfo(Long.parseLong(customerId));
        if(mypageDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(mypageDTO, HttpStatus.OK);
    }
}
