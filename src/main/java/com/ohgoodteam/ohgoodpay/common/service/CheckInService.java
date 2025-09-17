package com.ohgoodteam.ohgoodpay.common.service;

public interface CheckInService {
    void saveCheckInPoint(int point, Long customerId);
    boolean hasCheckedInToday(Long customerId);
}
