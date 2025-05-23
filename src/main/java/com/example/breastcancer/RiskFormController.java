package com.example.breastcancer;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.gson.JsonObject;

/**
 * Orchestrates form → API → UI flow.
 * No interface needed—calls back to MainApp directly.
 *
 * Added (23 May 2025):
 *  • {@link #onLogout()} helper invoked by RiskFormPanel.
 */
public class RiskFormController {

    private final RiskCalculationService service;
    private final RiskResponseMapper     mapper  = new RiskResponseMapper();
    private final MainApp                mainApp;
    private Session session;

    public RiskFormController(RiskCalculationService service, Session session, MainApp mainApp) {
        this.service = service;
        this.mainApp = mainApp;
        this.session = session;
    }

    /** Invoked by RiskFormPanel when user clicks “Calculate Risk”. */
    public void onSubmit(final RiskInput input) {
        new SwingWorker<Void, Void>() {
            private JsonObject merged;
            private String     greet;
            private Exception  err;

            @Override
            protected Void doInBackground() {
                try {
                    RiskAssessment ra = service.calculate(input);

                    // flatten input + results for chat
                    merged = mapper.toRequestJson(input);
                    merged.addProperty(
                        "absolute_risk_5yr",
                        ra.getResult().getFiveYearRisk()
                    );
                    merged.addProperty(
                        "absolute_risk_lifetime",
                        ra.getResult().getLifetimeRisk()
                    );

                    // one-liner greeting (inlined from GreetingBuilder)
                    double five = ra.getResult().getFiveYearRisk();
                    greet = Double.isNaN(five)
                        ? "Here are your results:"
                        : String.format(
                            "Your 5-year absolute risk is %.2f%% – let’s go through what it means.",
                            five * 100.0
                          );

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
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                // Call MainApp directly
                mainApp.onRiskSuccess(merged, greet);
            }
        }.execute();
    }

    /* Invoked by RiskFormPanel when the user clicks “Logout”. */
    public void onLogout() {
        mainApp.logout();
    }
}
