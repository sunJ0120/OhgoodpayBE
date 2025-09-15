package com.ohgoodteam.ohgoodpay.common.service;

public interface CheckInService {
    void saveCheckInPoint(int point);
    boolean hasCheckedInToday(Long customerId);
}
