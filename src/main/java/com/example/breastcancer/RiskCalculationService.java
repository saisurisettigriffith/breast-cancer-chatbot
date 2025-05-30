package com.example.breastcancer;

import com.google.gson.JsonObject;

public class RiskCalculationService {
    private final HttpApiClient api;
    private final RiskResponseMapper mapper = new RiskResponseMapper();

    public RiskCalculationService(HttpApiClient api) {
        this.api = api;
    }

    public RiskAssessment calculate(RiskInput input) throws Exception {
        JsonObject req = mapper.toRequestJson(input);
        JsonObject res = api.postRisk(req);
        return mapper.toDomain(input, res);
    }
}