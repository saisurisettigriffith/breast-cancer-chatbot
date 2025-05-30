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

    private void build() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
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
        errorLabel.setText("Calculating...");
        submitBtn.setEnabled(false);

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

    public void resetForm() {

        errorLabel.setText(" ");

        submitBtn.setEnabled(true);

        ageSpinner.setValue(48);
        menarchBox.setSelectedIndex(0);
        liveBirthBox.setSelectedIndex(0);
        biopsyBox.setSelectedIndex(0);

        biopsyDetails.setVisible(false);
        numBiopsyBox.setSelectedIndex(0);
        ihypBox.setSelectedIndex(2);
        relativesBox.setSelectedIndex(0);
        raceBox.setSelectedIndex(0);
    }

    public void enableSubmitButton() {
        submitBtn.setEnabled(true);
    }
}