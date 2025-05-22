package com.example.breastcancer;


/** Builds the long greeting paragraph exactly like the original code did. */
public class GreetingBuilder {

    public String build(RiskAssessment ra) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello! I’m your Breast Cancer ChatBot. I’ll be assisting you based on your health information and the GAIL model predictions. ");

        sb.append("• Your inputs: ");
        sb.append(inputSummary(ra.getInput()));
        sb.append(". • GAIL predictions: ");
        sb.append(resultSummary(ra.getResult()));
        sb.append(". All responses will be based on your provided health information and predictions from the GAIL model.");
        return sb.toString();
    }

    private String inputSummary(RiskInput in) {
        StringBuilder sb = new StringBuilder();
        sb.append("age=").append(in.getAge()).append(", ");
        sb.append("menarch=").append(in.getMenarchAgeIndex()).append(", ");
        sb.append("liveBirth=").append(in.getLiveBirthAgeIndex()).append(", ");
        sb.append("biopsy=").append(in.getBiopsyIndex()).append(", ");
        sb.append("numBiopsy=").append(in.getNumBiopsyIndex()).append(", ");
        sb.append("ihyp=").append(in.getIhypIndex()).append(", ");
        sb.append("relatives=").append(in.getRelativesIndex()).append(", ");
        sb.append("race=").append(in.getRaceCode());
        return sb.toString();
    }

    private String resultSummary(RiskResult r) {
        StringBuilder sb = new StringBuilder();
        sb.append("5‑year=" ).append(r.getFiveYearRisk()).append(", ");
        sb.append("lifetime=").append(r.getLifetimeRisk());
        return sb.toString();
    }
}