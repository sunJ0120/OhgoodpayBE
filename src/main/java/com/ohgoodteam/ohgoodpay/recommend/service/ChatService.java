package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.*;

public interface ChatService {
    ChatStartResponse startChat(Long customerId);
    ChatMoodResponse moodChat(Long customerId, String mood);
    ChatCheckHobbyResponse checkHobby(Long customerId);
    ChatUpdateHobbyResponse updateHobby(Long customerId, String newHobby);
    ChatAnalyzePurchasesResponse analyzePurchases(Long customerId);
}
