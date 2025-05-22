package com.example.breastcancer;

import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Orchestrates the form-submit flow:
 *   1. Runs the HTTP call on a background thread.
 *   2. Builds a *flat* JSON object that merges user input + all Gail results –
 *      exactly like the original React and “WORKINGJAVACODEBASE” versions.
 *   3. Shows a friendly dialog if anything goes wrong.
 */
public class RiskFormController {

    private final RiskCalculationService service;
    private final GreetingBuilder        greetingBuilder = new GreetingBuilder();
    private final RiskSubmitListener     listener;
    private final RiskResponseMapper     mapper          = new RiskResponseMapper();

    public RiskFormController(RiskCalculationService service,
                              RiskSubmitListener      listener) {
        this.service  = service;
        this.listener = listener;
    }

    /** Invoked by the Swing panel when the user clicks “Calculate Risk”. */
    public void onSubmit(final RiskInput input) {

        new SwingWorker<Void, Void>() {

            private JsonObject merged;
            private String     greeting;
            private Exception  error;

            @Override
            protected Void doInBackground() {
                try {
                    /* ---- 1. back-end call ---- */
                    RiskAssessment ra = service.calculate(input);

                    /* ---- 2. flatten (React logic) ---- */
                    merged = mapper.toRequestJson(input);   // start with form fields

                    /* Copy *every* Gail field so the back-end receives the same
                     * rich vital string the React version sends.                */
                    JsonObject results = ra.getRawResults();
                    java.util.Iterator<Map.Entry<String, JsonElement>> it =
                            results.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, JsonElement> e = it.next();
                        merged.add(e.getKey(), e.getValue());
                    }

                    greeting = greetingBuilder.build(ra);

                } catch (Exception ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Unable to calculate risk right now.\nDetails: " + error,
                        "Connection problem",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                listener.onRiskSuccess(merged, greeting);
            }
        }.execute();
    }
}
