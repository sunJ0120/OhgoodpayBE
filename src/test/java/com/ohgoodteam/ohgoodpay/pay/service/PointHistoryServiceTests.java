package com.ohgoodteam.ohgoodpay.pay.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ohgoodteam.ohgoodpay.pay.dto.PointHistoryDTO;

@SpringBootTest
public class PointHistoryServiceTests {

    @Autowired
    private PointHistoryService pointHistoryService;

    @Test
    public void testGetPointHistory() {
        List<PointHistoryDTO> pointHistoryDTOs = pointHistoryService.getPointHistory(1L);
        System.out.println("--------------------------------");
        for (PointHistoryDTO pointHistoryDTO : pointHistoryDTOs) {
            System.out.println(pointHistoryDTO);
        }
        System.out.println("--------------------------------");
    }
    
}
