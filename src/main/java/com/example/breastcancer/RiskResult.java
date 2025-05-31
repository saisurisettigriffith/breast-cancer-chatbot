package com.example.breastcancer;

public class RiskResult {
    private double fiveYearRisk;
    private double lifetimeRisk;

    public double getFiveYearRisk() {
        return fiveYearRisk;
    }

    public void setFiveYearRisk(double v) {
        fiveYearRisk = v;
    }

    public double getLifetimeRisk() {
        return lifetimeRisk;
    }

    public void setLifetimeRisk(double v) {
        lifetimeRisk = v;
    }
}

/**
 * Format conversion logic and Getters/Setters for RiskInput and RiskResult.
 * Nothing big here... it is just complicated mandatory labor work.
 * Just following SOLID principles and keeping the code clean.
 */