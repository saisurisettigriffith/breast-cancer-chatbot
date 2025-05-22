package com.example.breastcancer;

import com.google.gson.JsonObject;

/**
 * Callback used by RiskFormController → MainApp.
 * Exactly the same signature as in your original working code.
 */
@FunctionalInterface
public interface RiskSubmitListener {
    void onRiskSuccess(JsonObject merged, String greeting);
}