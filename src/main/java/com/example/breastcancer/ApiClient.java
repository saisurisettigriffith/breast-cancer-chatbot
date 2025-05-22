package com.example.breastcancer;

import com.google.gson.JsonObject;

/**
 * Lean interface – higher‑level code depends on this abstraction rather than
 * on java.net.http.  We add two default helpers so callers can still use the
 * familiar postRisk / postChat methods exactly like in the original code.
 */
public interface ApiClient {

    /** Low‑level generic POST (path starts with "/api/"). */
    JsonObject post(String path, JsonObject body) throws Exception;

    /* ───────────────── convenience helpers ───────────────── */

    default JsonObject postRisk(JsonObject body) throws Exception {
        return post("/api/risk", body);
    }

    default JsonObject postChat(JsonObject body) throws Exception {
        return post("/api/chat", body);
    }
}