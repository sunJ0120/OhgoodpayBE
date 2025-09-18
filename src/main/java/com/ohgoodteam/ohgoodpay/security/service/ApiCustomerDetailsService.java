package com.ohgoodteam.ohgoodpay.security.service;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerDTO;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.common.service.CustomerService;
import com.ohgoodteam.ohgoodpay.security.dto.ApiCustomerDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiCustomerDetailsService implements UserDetailsService{

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    
    /**
     * 사용자 정보 로드
     * @param username 사용자 이메일
     * @return 사용자 정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomerEntity customer = customerRepository.findByEmailId(username);
        
        // 사용자 조회 실패
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        // 사용자 차단 여부 확인
        if (customer.isBlocked()) {
            throw new UsernameNotFoundException("User is blocked");
        }

        ApiCustomerDTO apiCustomerDTO = new ApiCustomerDTO(String.valueOf(customer.getCustomerId()), customer.getPwd(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        
        return apiCustomerDTO;
    }

}
