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
        /**
         * These are NETWORK BASED OPERATIONS, and are
         * long-running tasks to calculate the risk assessment
         * and to update the UI with the results.
         *
         * If we naively called the service.calculate(input) method
         * without using a background thread,
         * it could potentially block the UI thread, making the application unresponsive.
         * 
         * JavaFX EQUIVALENT: <<Task>> instead of SwingWorker.
         *
         * Summary from Java Swing documentation (Tailored for this example):
         *
         *      1. SwingWorker lets you run long-running tasks (like service.calculate(input))
         *          off the Event Dispatch Thread, so the UI doesn’t lock up.
         *
         *      2. doInBackground() is where you perform the risk calculation,
         *          map JSON, and assemble strings—none of which should run on the EDT.
         *
         *      3. done() is automatically invoked on the EDT once doInBackground()
         *          returns (or throws), so it’s the safe place to inspect err and either
         *          show an error popup (JOptionPane) or navigate to the chat panel
         *          (mainApp.onRiskSuccess(...)).
         *
         */
        mainApp.onRiskCalculationStart();
        new SwingWorker<Void, Void>() {
            private JsonObject merged;
            private String greet;
            private Exception err;

            /**
             * 
             * Abbrevations:
             *    {ComplicatedJSONMappings}: Complicated Json to JsonObject or JsonObject to Json.
             *           or JSON to String or String to JSON.
             * 
             * Nutshell:
             *     1. Complicated Json to JsonObject or JsonObject to Json.
             *     2. RiskCalculationService class is imported as
             *           parameters to the constructor and it is
             *           assigned to the class variable <service>.
             *     3. RiskCalculationService -> RiskCalculationService.calculate(RiskInput input)...
             *           ...-> {ComplicatedJSONMappings} -> HttpApiClient.postRisk(JsonObject body)...
             *           ...-> see HttpApiClient.java for details.
             *           ... returns a RiskAssessment object which contains a JsonObject
             *          ... which is meant to store the results of the risk assessment.
             *          ... before returning the RiskAssessment object, it calls mapper.toDomain(input, res)
             *          ... which maps the input and the response to a RiskAssessment object.
             *          ... so at the end of this method, we have a RiskAssessment object
             *         ... which contains the input, the result, and the raw results.
             *         ... the result is used where?
             *         ... see the line where it says:
             *         ... <merged> = mapper.toRequestJson(input);
             *         ... which converts the input to a JsonObject and adds the absolute risk values.
             *         ... then see the next two lines where it adds the absolute risk values
             *        ... to the <merged> JsonObject.
             *      4. We call onRiskSuccess(merged, greet) on the mainApp object,
             *        ... which is a reference to the MainApp class, which is responsible for
             *        ... navigating to the chat panel and displaying the results.
             * 
             * So, what we just sent to the mainApp.onRiskSuccess(merged, greet)
             * is a JsonObject that contains the input data and the calculated risk values,
             * i.e, absolute risk values for 5 years and lifetime,
             * along with a greeting message, which is a String
             * and it is used to inform the user about their risk assessment results.
             * 
             * -> see MainApp.java for the onRiskSuccess method.
             * -> see RiskFormPanel.java for .onRiskCalculationStart() and onRiskCalculationEnd() methods.
             */

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
                // Below is GUARENTEED REGARDLESS OF SUCCESS OR FAILURE
                mainApp.onRiskCalculationEnd();
                // ON FAILURE:
                if (err != null) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Unable to calculate risk right now.\nDetails: " + err,
                            "Connection problem",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // ON SUCCESS:
                mainApp.onRiskSuccess(merged, greet);
            }
        }.execute();
    }

    public void onLogout() {
        mainApp.logout();
    }
}