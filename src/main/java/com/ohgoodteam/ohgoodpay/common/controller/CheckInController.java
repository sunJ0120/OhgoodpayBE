package com.ohgoodteam.ohgoodpay.common.controller;

import com.ohgoodteam.ohgoodpay.common.service.CheckInService;
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

    @PostMapping("/roulette")
    public int rouletteCheckIn(@RequestParam int point) {
        checkInService.saveCheckInPoint(point);
        return point;
    }
    @GetMapping("/today")
    public boolean checkToday(@RequestParam Long customerId) {
        return checkInService.hasCheckedInToday(customerId);
    }
}