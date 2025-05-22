package com.example.breastcancer;

/**
 * Plain‑old data holder for all parameters entered in the GUI.  We keep simple
 * fields + getters/setters, then add a few *derived* helpers expected by
 * RiskAssessmentService so no other class has to recalculate the codes.
 */
public class RiskInput {
    private int age;
    private int menarchAgeIndex;
    private int liveBirthAgeIndex;
    private int biopsyIndex;
    private int numBiopsyIndex;
    private int ihypIndex;
    private int relativesIndex;
    private int raceCode;

    /* ───── generated getters / setters (no lambdas, no for‑each) ───── */
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getMenarchAgeIndex() { return menarchAgeIndex; }
    public void setMenarchAgeIndex(int menarchAgeIndex) { this.menarchAgeIndex = menarchAgeIndex; }

    public int getLiveBirthAgeIndex() { return liveBirthAgeIndex; }
    public void setLiveBirthAgeIndex(int liveBirthAgeIndex) { this.liveBirthAgeIndex = liveBirthAgeIndex; }

    public int getBiopsyIndex() { return biopsyIndex; }
    public void setBiopsyIndex(int biopsyIndex) { this.biopsyIndex = biopsyIndex; }

    public int getNumBiopsyIndex() { return numBiopsyIndex; }
    public void setNumBiopsyIndex(int numBiopsyIndex) { this.numBiopsyIndex = numBiopsyIndex; }

    public int getIhypIndex() { return ihypIndex; }
    public void setIhypIndex(int ihypIndex) { this.ihypIndex = ihypIndex; }

    public int getRelativesIndex() { return relativesIndex; }
    public void setRelativesIndex(int relativesIndex) { this.relativesIndex = relativesIndex; }

    public int getRaceCode() { return raceCode; }
    public void setRaceCode(int raceCode) { this.raceCode = raceCode; }

    /* ───── derived helpers required by RiskAssessmentService ───── */
    public int  getMenarchAgeCode()          { return menarchAgeIndex;            }
    public int  getFirstLiveBirthCode()      { return liveBirthAgeIndex;          }
    public boolean isHadBiopsy()             { return biopsyIndex == 1;           }
    public int  getBiopsyCountCode()         { return numBiopsyIndex;             }
    public int  getHyperplasiaCode()         { return ihypIndex;                  }
    public int  getFirstDegreeRelativesCode(){ return relativesIndex;             }
}