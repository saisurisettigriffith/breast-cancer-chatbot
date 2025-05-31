package com.example.breastcancer;

public class RiskInput {
    private int age;
    private int menarchAgeIndex;
    private int liveBirthAgeIndex;
    private int biopsyIndex;
    private int numBiopsyIndex;
    private int ihypIndex;
    private int relativesIndex;
    private int raceCode;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMenarchAgeIndex() {
        return menarchAgeIndex;
    }

    public void setMenarchAgeIndex(int menarchAgeIndex) {
        this.menarchAgeIndex = menarchAgeIndex;
    }

    public int getLiveBirthAgeIndex() {
        return liveBirthAgeIndex;
    }

    public void setLiveBirthAgeIndex(int liveBirthAgeIndex) {
        this.liveBirthAgeIndex = liveBirthAgeIndex;
    }

    public int getBiopsyIndex() {
        return biopsyIndex;
    }

    public void setBiopsyIndex(int biopsyIndex) {
        this.biopsyIndex = biopsyIndex;
    }

    public int getNumBiopsyIndex() {
        return numBiopsyIndex;
    }

    public void setNumBiopsyIndex(int numBiopsyIndex) {
        this.numBiopsyIndex = numBiopsyIndex;
    }

    public int getIhypIndex() {
        return ihypIndex;
    }

    public void setIhypIndex(int ihypIndex) {
        this.ihypIndex = ihypIndex;
    }

    public int getRelativesIndex() {
        return relativesIndex;
    }

    public void setRelativesIndex(int relativesIndex) {
        this.relativesIndex = relativesIndex;
    }

    public int getRaceCode() {
        return raceCode;
    }

    public void setRaceCode(int raceCode) {
        this.raceCode = raceCode;
    }

    public int getMenarchAgeCode() {
        return menarchAgeIndex;
    }

    public int getFirstLiveBirthCode() {
        return liveBirthAgeIndex;
    }

    public boolean isHadBiopsy() {
        return biopsyIndex == 1;
    }

    public int getBiopsyCountCode() {
        return numBiopsyIndex;
    }

    public int getHyperplasiaCode() {
        return ihypIndex;
    }

    public int getFirstDegreeRelativesCode() {
        return relativesIndex;
    }
}

/**
 * Format conversion logic and Getters/Setters for RiskInput and RiskResult.
 * Nothing big here... it is just complicated mandatory labor work.
 * Just following SOLID principles and keeping the code clean.
 */