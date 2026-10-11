package com.ohgoodteam.ohgoodpay.llm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MlProduct(int rank, String name, int price, String image, String url, String category) {
}
