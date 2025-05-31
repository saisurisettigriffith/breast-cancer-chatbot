package com.example.breastcancer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class RiskFormPanel extends JPanel {
    public static final int[] RACE_VALUES = {
            1, 2, 3, 7, 8, 9, 10, 11, 12
    };

    private final RiskFormController controller;
    private final Session session;
    private JSpinner ageSpinner;
    private JComboBox<String> menarchBox,
            liveBirthBox,
            biopsyBox,
            numBiopsyBox,
            ihypBox,
            relativesBox,
            raceBox;
    private JPanel biopsyDetails;
    private JButton submitBtn;
    private JButton logoutBtn;
    private JLabel errorLabel;

    public RiskFormPanel(RiskFormController controller, Session session) {
        this.controller = controller;
        this.session = session;
        build();
    }

    /**
     * Sets the loading state of the form.
     * 
     * When loading is true, the submit button is disabled and an error label
     * displays "Calculating...". When loading is false, the button is enabled
     * and the error label is cleared.
     * 
     * This is controlled by RiskFormController.onSubmit() method
     * specifically in the SwingWorker's done() method - when it is finished.
     * i.e, onRiskCalculationEnd(false) --{caseA}-- is called in done() method to
     * indicate that the risk calculation has ended. 
     * and in the outer scope of the SwingWorker's doInBackground() method where
     * the controller.onRiskCalculationStart() is called to indicate that the
     * risk calculation has started and the UI should reflect that it is loading.
     * 
     * .onRiskCalculationStart() is the method from MainApp.java that calls
     * the *BELOW* RiskFormPanel.setLoadingState(true) --{caseB}-- to indicate that loading has started.
     *
     * 
     * **BELOW** is the RiskFormPanel.setLoadingState(caseA or caseB) method
     * that sets the loading state of the form.
     * 
     */

    public void setLoadingState(boolean loading) {
        submitBtn.setEnabled(!loading);
        if (loading) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText("Calculating...");
        } else {
            errorLabel.setText(" ");
        }
    }


    private void build() {
        /**
         * Sets up the panel’s UI and registers all ActionListeners.
         *
         * build() is called from the constructor and does the following:
         *   1. it creates components (buttons, spinners, combo boxes, etc.).
         *   2. then it adds them to this JPanel.
         *   3. then it registers ActionListeners on each interactive...
         *      ...widget (e.g., submitBtn, logoutBtn).
         *
         * Because this class extends JPanel:
         *   1. Once MainApp adds this panel into its JFrame and calls setVisible(true),
         *     Swing’s event loop takes over.
         *   2. Each JButton (or other component) that has an ActionListener will remain
         *     “live” as long as the panel is in the visible hierarchy.
         *   3. Whenever the user clicks a button, Swing delivers an ActionEvent to the
         *     registered listener, and our callback (e.g., submit()) is invoked.
         *
         * By registering listeners in build(), we don’t need to re‐attach them later:
         * 
         * Because as soon as the panel becomes part of the visible JFrame, Swing ensures
         * those listeners stay active and receive all future events - all thanks to the
         * ***ActionListener interface*** + ***Swing's event handling model*** that
         * takes over the Application's event loop once the JFrame is...
         * ...visible (setVisible(true)) and keeps the...
         * ...event dispatch thread (EDT) running and responsive.
         */

         /**
         * External - RegisterPanel.java, LoginPanel.java: JavaFX uses...
         * ...EventHandler<T extends Event> for all events.
         * 
         * HERE HOWEVER, Swing uses specific...
         * ...listener interfaces (ActionListener) for events.
         * 
         * Both handle similar events, but with different APIs.
         * 
         * Only action listeners are used here are logout because
         * 
         *      What you cannot do:
         *           1. You CANNOT chat without risk parameters.
         *           2. You CANNOT redo assessment because you are
         *               already in the risk assessment.
         *           3. You CANNOT register or login because
         *               you are already logged in.
         *      
         *      What you can do:
         *           1. You CAN, however, logout without risk parameters.
         * 
         */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0xeeeeee));

        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        logoutBtn = new JButton("Logout");

        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.onLogout();
            }
        });
        nav.add(logoutBtn, BorderLayout.EAST);
        nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        add(nav);

        add(centeredLabel("Breast Cancer Risk Parameters", 18f));

        ageSpinner = new JSpinner(new SpinnerNumberModel(48, 35, 85, 1));
        add(labeled("Woman's current age (35-85):", ageSpinner));

        menarchBox = new JComboBox<>(new String[] {
                "≥ 14 yrs / Unknown",
                "12 - 13 yrs",
                "7 - 11 yrs"
        });
        add(labeled("Age at first period:", menarchBox));

        liveBirthBox = new JComboBox<>(new String[] {
                "Unknown or < 20 yrs",
                "20 - 24 yrs",
                "25 - 29 yrs or No births",
                "≥ 30 yrs"
        });
        add(labeled("Age at first live birth:", liveBirthBox));

        biopsyBox = new JComboBox<>(new String[] {
                "No / Unknown",
                "Yes"
        });
        add(labeled("Ever had biopsy?", biopsyBox));

        biopsyDetails = new JPanel();
        biopsyDetails.setLayout(new BoxLayout(biopsyDetails, BoxLayout.Y_AXIS));
        biopsyDetails.setOpaque(false);
        numBiopsyBox = new JComboBox<>(new String[] {
                "0 biopsies",
                "1 biopsy or unknown count",
                "> 1 biopsy"
        });
        ihypBox = new JComboBox<>(new String[] {
                "No atypical hyperplasia",
                "Yes",
                "Unknown / Not applicable"
        });
        biopsyDetails.add(labeled("Number of biopsies:", numBiopsyBox));
        biopsyDetails.add(labeled("Atypical hyperplasia?", ihypBox));
        add(biopsyDetails);
        biopsyDetails.setVisible(false);

        biopsyBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                biopsyDetails.setVisible(biopsyBox.getSelectedIndex() == 1);
            }
        });

        relativesBox = new JComboBox<>(new String[] {
                "0 relatives / Unknown",
                "1",
                "> 1"
        });
        add(labeled("First-degree relatives with breast cancer:", relativesBox));

        raceBox = new JComboBox<>(new String[] {
                "White / Unknown / American-Indian",
                "African-American",
                "Hispanic",
                "Chinese",
                "Japanese",
                "Filipino",
                "Hawaiian",
                "Other Pacific Islander",
                "Other Asian-American"
        });
        add(labeled("Race / Ethnicity:", raceBox));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel);

        submitBtn = new JButton("Calculate Risk");
        submitBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submit();
            }
        });
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(submitBtn);
    }

    private JLabel centeredLabel(String text, float size) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, size));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return lbl;
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(280, 28));
        p.add(l, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void submit() {
        int age = ((Integer) ageSpinner.getValue()).intValue();
        if (age < 35 || age > 85) {
            errorLabel.setText("Age must be between 35 and 85.");
            return;
        }

        RiskInput input = new RiskInput();
        input.setAge(age);
        input.setMenarchAgeIndex(menarchBox.getSelectedIndex());
        input.setLiveBirthAgeIndex(liveBirthBox.getSelectedIndex());

        int biopsyIdx = biopsyBox.getSelectedIndex();
        input.setBiopsyIndex(biopsyIdx);
        if (biopsyIdx == 0) {
            input.setNumBiopsyIndex(0);
            input.setIhypIndex(99);
        } else {
            input.setNumBiopsyIndex(numBiopsyBox.getSelectedIndex());
            int ihypVal = ihypBox.getSelectedIndex();
            if (ihypVal == 2)
                ihypVal = 99;
            input.setIhypIndex(ihypVal);
        }

        input.setRelativesIndex(relativesBox.getSelectedIndex());
        input.setRaceCode(RACE_VALUES[raceBox.getSelectedIndex()]);

        controller.onSubmit(input);
    }
}