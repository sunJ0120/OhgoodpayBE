package com.ohgoodteam.ohgoodpay.common.service;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerDTO;

public interface RegisterService {
    
    // 회원 가입
    boolean register(CustomerDTO customerDTO);

}
