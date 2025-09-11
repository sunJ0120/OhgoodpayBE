package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatRecommendRequestDTO extends BaseChatRequestDTO {
    // TODO : 이거 어떤 방식으로 사용할지에 대한 고민 필요.
    private String require;
}
