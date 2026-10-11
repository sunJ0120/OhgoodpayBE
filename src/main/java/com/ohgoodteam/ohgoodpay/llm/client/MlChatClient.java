package com.ohgoodteam.ohgoodpay.llm.client;

import com.ohgoodteam.ohgoodpay.llm.dto.MlChatRequest;
import com.ohgoodteam.ohgoodpay.llm.dto.MlChatResponse;
import com.ohgoodteam.ohgoodpay.llm.dto.MlValidationRequest;
import com.ohgoodteam.ohgoodpay.llm.dto.MlValidationResponse;

/** ML(FastAPI) 채팅 API. 프롬프트는 요청에 실어 보내고, 호출 메타데이터는 응답에서 받는다. */
public interface MlChatClient {

    MlChatResponse chat(MlChatRequest request);

    MlValidationResponse validate(MlValidationRequest request);
}
