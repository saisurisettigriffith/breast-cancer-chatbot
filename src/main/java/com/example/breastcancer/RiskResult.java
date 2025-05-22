package com.example.breastcancer;

/** Holds the two risk values returned by the Flask API. */
public class RiskResult {
    private double fiveYearRisk;
    private double lifetimeRisk;

    public double getFiveYearRisk() { return fiveYearRisk; }
    public void setFiveYearRisk(double fiveYearRisk) { this.fiveYearRisk = fiveYearRisk; }

    public double getLifetimeRisk() { return lifetimeRisk; }
    public void setLifetimeRisk(double lifetimeRisk) { this.lifetimeRisk = lifetimeRisk; }
}