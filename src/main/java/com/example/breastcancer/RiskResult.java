package com.example.breastcancer;

/** Holds the two absolute-risk numbers returned by Flask. */
public class RiskResult {
    private double fiveYearRisk;
    private double lifetimeRisk;

    public double getFiveYearRisk()      { return fiveYearRisk; }
    public void   setFiveYearRisk(double v) { fiveYearRisk = v; }

    public double getLifetimeRisk()      { return lifetimeRisk; }
    public void   setLifetimeRisk(double v) { lifetimeRisk = v; }
}