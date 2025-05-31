package com.example.breastcancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RiskResponseMapper {

    public JsonObject toRequestJson(RiskInput in) {
        JsonObject body = new JsonObject();

        body.addProperty("age", in.getAge());
        body.addProperty("menarch_age", in.getMenarchAgeIndex());
        body.addProperty("live_birth_age", in.getLiveBirthAgeIndex());
        body.addProperty("ever_had_biopsy", in.getBiopsyIndex());

        if (in.getBiopsyIndex() == 0) {
            body.addProperty("num_biopsy", 0);
            body.addProperty("ihyp", 99);
        } else {
            body.addProperty("num_biopsy", in.getNumBiopsyIndex());
            int ihyp = in.getIhypIndex() == 2 ? 99 : in.getIhypIndex();
            body.addProperty("ihyp", ihyp);
        }

        body.addProperty("first_deg_relatives", in.getRelativesIndex());
        body.addProperty("race", in.getRaceCode());
        return body;
    }

    public RiskAssessment toDomain(RiskInput input, JsonObject raw) {
        JsonObject assessment = raw.getAsJsonObject("assessment");
        JsonObject results = assessment.getAsJsonObject("results");

        double five = get(results, "five_year_abs", "absolute_risk_5yr");
        double life = get(results, "lifetime_abs", "absolute_risk_lifetime");

        RiskResult rr = new RiskResult();
        rr.setFiveYearRisk(five);
        rr.setLifetimeRisk(life);

        RiskAssessment ra = new RiskAssessment();
        ra.setInput(input);
        ra.setResult(rr);
        ra.setRawResults(results.deepCopy());
        return ra;
    }

    private double get(JsonObject obj, String modern, String legacy) {
        JsonElement el = obj.get(modern);
        if (el == null || el.isJsonNull())
            el = obj.get(legacy);
        return (el == null || el.isJsonNull()) ? Double.NaN : el.getAsDouble();
    }
}

/**
 * Format conversion logic and Getters/Setters for RiskInput and RiskResult.
 * Nothing big here... it is just complicated mandatory labor work.
 * Just following SOLID principles and keeping the code clean.
 */