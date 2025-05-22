package com.example.breastcancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Builds the exact request body expected by the Flask back-end and converts the
 * reply to Java domain objects.  This version understands the *current* field
 * names used by the open-source GAIL endpoint:
 *
 *   • five_year_abs   /  five_year_ave
 *   • lifetime_abs    /  lifetime_ave
 *
 * For robustness we still accept the older
 * absolute_risk_5yr / absolute_risk_lifetime names some deployments use.
 */
public class RiskResponseMapper {

    /* ───────────────── request side ───────────────── */

    public JsonObject toRequestJson(RiskInput in) {
        JsonObject body = new JsonObject();

        body.addProperty("age",               in.getAge());
        body.addProperty("menarch_age",       in.getMenarchAgeIndex());
        body.addProperty("live_birth_age",    in.getLiveBirthAgeIndex());
        body.addProperty("ever_had_biopsy",   in.getBiopsyIndex());

        /* Unknown hyperplasia (combo index 2) must be sent as 99 – just like
         * the React code. */
        if (in.getBiopsyIndex() == 0) {
            body.addProperty("num_biopsy", 0);
            body.addProperty("ihyp",       99);
        } else {
            body.addProperty("num_biopsy", in.getNumBiopsyIndex());
            int ihyp = in.getIhypIndex();
            if (ihyp == 2) ihyp = 99;
            body.addProperty("ihyp", ihyp);
        }

        body.addProperty("first_deg_relatives", in.getRelativesIndex());
        body.addProperty("race",                in.getRaceCode());

        return body;
    }

    /* ───────────────── response side ───────────────── */

    public RiskAssessment toDomain(RiskInput input, JsonObject raw) {
        if (raw == null || raw.isJsonNull()) {
            throw new IllegalStateException("API response is empty.");
        }

        JsonObject assessmentObj = objOrNull(raw.get("assessment"));
        if (assessmentObj == null) {
            throw new IllegalStateException("Response is missing the “assessment” object.");
        }

        JsonObject resultsObj = objOrNull(assessmentObj.get("results"));
        if (resultsObj == null) {
            throw new IllegalStateException("Response is missing the “results” object.");
        }

        /* Pull the *absolute* 5-year and lifetime risks – fall back to the
         * older field names if necessary. */
        double fiveYear = doubleOrNaN(resultsObj.get("five_year_abs"));
        if (Double.isNaN(fiveYear)) {
            fiveYear = doubleOrNaN(resultsObj.get("absolute_risk_5yr"));
        }

        double lifetime = doubleOrNaN(resultsObj.get("lifetime_abs"));
        if (Double.isNaN(lifetime)) {
            lifetime = doubleOrNaN(resultsObj.get("absolute_risk_lifetime"));
        }

        RiskResult rr = new RiskResult();
        rr.setFiveYearRisk(fiveYear);
        rr.setLifetimeRisk(lifetime);

        RiskAssessment ra = new RiskAssessment();
        ra.setInput(input);
        ra.setResult(rr);
        /* Keep an *independent* copy so callers can modify it safely. */
        ra.setRawResults(resultsObj.deepCopy());

        return ra;
    }

    /* ───────────────── helpers ───────────────── */

    private JsonObject objOrNull(JsonElement el) {
        return (el != null && el.isJsonObject()) ? el.getAsJsonObject() : null;
    }

    private double doubleOrNaN(JsonElement el) {
        if (el == null || el.isJsonNull()) return Double.NaN;
        try { return el.getAsDouble(); } catch (Exception ex) { return Double.NaN; }
    }
}
