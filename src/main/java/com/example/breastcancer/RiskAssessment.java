package com.example.breastcancer;

import com.google.gson.JsonObject;

/**
 * Bundles the user input, the parsed numerical results *and* the raw “results”
 * object that comes back from the Flask /api/risk route.  Keeping the raw JSON
 * lets us flatten everything – exactly like the original React code did –
 * without having to predict which fields may appear in the future.
 */
public class RiskAssessment {

    private RiskInput  input;
    private RiskResult result;
    private JsonObject rawResults;   // ← new

    /* ───── generated getters / setters ───── */

    public RiskInput getInput() { return input; }
    public void setInput(RiskInput input) { this.input = input; }

    public RiskResult getResult() { return result; }
    public void setResult(RiskResult result) { this.result = result; }

    /** Full copy of the “results” object (five_year_abs, lifetime_abs, …). */
    public JsonObject getRawResults() { return rawResults; }
    public void setRawResults(JsonObject rawResults) { this.rawResults = rawResults; }
}
