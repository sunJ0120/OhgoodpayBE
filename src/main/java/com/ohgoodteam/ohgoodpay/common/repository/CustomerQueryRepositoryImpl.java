package com.ohgoodteam.ohgoodpay.common.repository;

import com.ohgoodteam.ohgoodpay.common.entity.QCustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.QGradeEntity;
import com.ohgoodteam.ohgoodpay.common.dto.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.common.dto.QCustomerCacheDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.ohgoodteam.ohgoodpay.common.entity.QCustomerEntity.customerEntity;
import static com.ohgoodteam.ohgoodpay.common.entity.QGradeEntity.gradeEntity;

@Repository
@RequiredArgsConstructor //생성자 주입
public class CustomerQueryRepositoryImpl implements CustomerQueryRepository{

    private final JPAQueryFactory query; //config 파일을 통해 바로 주입

    // join을 통해 고객의 등급에 따른 신용한도 정보를 함께 조회해서 CustomerCacheDTO로 저장. redis에 캐싱하기 위함
    @Override
    public CustomerCacheDTO findCustomerCacheInfoById(Long customerId) {
        return query.select(new QCustomerCacheDTO(
                        customerEntity.customerId,
                        customerEntity.name,
                        gradeEntity.limitPrice.as("creditLimit")
                ))
                .from(customerEntity)
                .join(customerEntity.grade, gradeEntity)
                .where(customerEntity.customerId.eq(customerId))
                .fetchOne();
    }
}
