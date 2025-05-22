package com.example.breastcancer;

import com.google.gson.JsonObject;

/** Use‑case class that hides the HTTP call from callers. */
class RiskCalculationService {
    private ApiClient apiClient;
    private RiskResponseMapper mapper = new RiskResponseMapper();

    RiskCalculationService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    RiskAssessment calculate(RiskInput input) throws Exception {
        JsonObject reqBody = mapper.toRequestJson(input);
        JsonObject resJson = apiClient.postRisk(reqBody);
        return mapper.toDomain(input, resJson);
    }
}