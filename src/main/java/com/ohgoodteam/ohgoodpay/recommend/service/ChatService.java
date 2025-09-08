package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.ChatCheckHobbyResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatMoodResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatUpdateHobbyResponse;

public interface ChatService {
    ChatStartResponse startChat(Long customerId);
    ChatMoodResponse moodChat(Long customerId, String mood);
    ChatCheckHobbyResponse checkHobby(Long customerId);
    ChatUpdateHobbyResponse updateHobby(Long customerId, String newHobby);
}
