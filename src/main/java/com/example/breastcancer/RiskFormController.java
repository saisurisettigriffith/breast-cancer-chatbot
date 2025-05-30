package com.example.breastcancer;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.gson.JsonObject;

public class RiskFormController {

    private final RiskCalculationService service;
    private final RiskResponseMapper mapper = new RiskResponseMapper();
    private final MainApp mainApp;
    private Session session;

    public RiskFormController(RiskCalculationService service, Session session, MainApp mainApp) {
        this.service = service;
        this.mainApp = mainApp;
        this.session = session;
    }

    public void onSubmit(final RiskInput input) {
        new SwingWorker<Void, Void>() {
            private JsonObject merged;
            private String greet;
            private Exception err;

            @Override
            protected Void doInBackground() {
                try {
                    RiskAssessment ra = service.calculate(input);

                    merged = mapper.toRequestJson(input);
                    merged.addProperty(
                            "absolute_risk_5yr",
                            ra.getResult().getFiveYearRisk());
                    merged.addProperty(
                            "absolute_risk_lifetime",
                            ra.getResult().getLifetimeRisk());

                    double five = ra.getResult().getFiveYearRisk();
                    greet = Double.isNaN(five)
                            ? "Here are your results:"
                            : String.format(
                                    "Your 5-year absolute risk is %.2f%%. Let us go through what it means.",
                                    five * 100.0);

                } catch (Exception ex) {
                    err = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (err != null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Unable to calculate risk right now.\nDetails: " + err,
                            "Connection problem",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                mainApp.onRiskSuccess(merged, greet);
            }
        }.execute();
    }

    public void onLogout() {
        mainApp.logout();
    }
}