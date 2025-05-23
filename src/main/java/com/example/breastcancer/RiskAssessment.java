package com.example.breastcancer;

import com.google.gson.JsonObject;

/** Bundle of (input + prediction + raw JSON copy) passed around the UI. */
public class RiskAssessment {
    private RiskInput   input;
    private RiskResult  result;
    private JsonObject  rawResults;

    public RiskInput  getInput()                { return input; }
    public void       setInput(RiskInput i)     { input = i; }

    public RiskResult getResult()               { return result; }
    public void       setResult(RiskResult r)   { result = r; }

    public JsonObject getRawResults()           { return rawResults; }
    public void       setRawResults(JsonObject j){ rawResults = j; }
}
