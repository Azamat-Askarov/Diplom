package sample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ButtonPanel extends JPanel {
    private JTextField networkLengthField;
    private JTextField kchopLengthField;
    private JTextField cableLengthField;
    private JTextField amplifierValueField;
    private JTextField lossKoeffField;
    private JTextField channelsNumField;
    private Main mainFrame; // Main frame uchun

    public ButtonPanel(Main mainFrame) {
        this.mainFrame = mainFrame; // Main frame'ni olish
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        addParameterFields(gbc);
        addSimulateButton(gbc);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addParameterFields(GridBagConstraints gbc) {
        String[] labels = {
                "Liniya traktining uzunligi (km)",
                "Kirish chiqish oraliq punktigacha masofa (km)",
                "Kabelning qurilish uzunligi (km)",
                "Kuchaytirgichning kuchaytirish sathi (dB)",
                "Tolaning so'nish koeffitsiyenti (db/km)",
                "Optik kanallar soni"
        };
        // tugmalarga kiritilgan qiymatlarni o'lashtirish
        JTextField[] fields = {
                networkLengthField = new JTextField(),
                kchopLengthField = new JTextField(),
                cableLengthField = new JTextField(),
                amplifierValueField = new JTextField(),
                lossKoeffField = new JTextField(),
                channelsNumField = new JTextField()
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            add(fields[i], gbc);
        }
        // "Optik signalning boshlang'ich uzunligi" uchun kiritilishi kerak bo'lgan qiymatlar ro'yxati
        String[] firstSignalLengthSuggestion = {"25", "26", "27", "28", "29", "30"};
        JComboBox<String> suggestedValuesComboBox = new JComboBox<>(firstSignalLengthSuggestion);
        suggestedValuesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedValue = (String) suggestedValuesComboBox.getSelectedItem();
                amplifierValueField.setText(selectedValue);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 3; // "Kuchaytirgichning kuchaytirish qobiliyati" nomli maydon
        add(suggestedValuesComboBox, gbc);

        // "Tolaning so'nish koeffitsiyenti" uchun kiritilishi kerak bo'lgan qiymatlar ro'yxati
        String[] suggestedValues = {"0.19","0.2", "0.21", "0.22", "0.23", "0.24", "0.25"};
        JComboBox<String> suggestedValuesComboBox2 = new JComboBox<>(suggestedValues);
        suggestedValuesComboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedValue = (String) suggestedValuesComboBox2.getSelectedItem();
                lossKoeffField.setText(selectedValue);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 4;
        add(suggestedValuesComboBox2, gbc);

        // "Kuchaytirish masofasi" uchun kiritilishi kerak bo'lgan qiymatlar ro'yxati
        String[] networkLengthSuggestion = {"360","400","500","600","640"};
        JComboBox<String> suggestedValuesComboBox3 = new JComboBox<>(networkLengthSuggestion);
        suggestedValuesComboBox3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedValue = (String) suggestedValuesComboBox3.getSelectedItem();
                networkLengthField.setText(selectedValue);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(suggestedValuesComboBox3, gbc);

        // "Kabel qurillish uzunligi" uchun kiritilishi kerak bo'lgan qiymatlar ro'yxati
        String[] cableLengthSuggestion = {"2","4","6","8"};
        JComboBox<String> suggestedValuesComboBox4 = new JComboBox<>(cableLengthSuggestion);
        suggestedValuesComboBox4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedValue = (String) suggestedValuesComboBox4.getSelectedItem();
                cableLengthField.setText(selectedValue);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(suggestedValuesComboBox4, gbc);

    }

    private void addSimulateButton(GridBagConstraints gbc) {
        JButton simulateButton = new JButton("Hisoblash");
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(simulateButton, gbc);

        simulateButton.addActionListener(e -> performSimulation());
    }

    private void performSimulation() {
        try {
            int networkLength = Integer.parseInt(networkLengthField.getText());
            int kchopLength = Integer.parseInt(kchopLengthField.getText());
            double cableLength = Double.parseDouble(cableLengthField.getText());
            int amplifierValue = Integer.parseInt(amplifierValueField.getText());
            double lossKoeff = Double.parseDouble(lossKoeffField.getText()); // O'zgartirilgan nom
            int channelsNum = Integer.parseInt(channelsNumField.getText());

            simulateOpticalNetwork(networkLength, kchopLength, cableLength, amplifierValue, lossKoeff, channelsNum);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Iltimos, barcha maydonlarni to'g'ri to'ldiring", "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simulateOpticalNetwork(int networkLength, int kchopLength, double cableLength,
                                        int amplifierValue, double lossKoeff, int channelsNum) {
        double alfaEkv = lossKoeff + (0.03 / cableLength);  // so'nish koeff
        double amplifierMaxLength = (amplifierValue - 1) / alfaEkv;  // kuchaytirgichlarning max masofasi
        double multiplexerPower = 20 - (10 * Math.log10(channelsNum)); // multipleksordan chiqayotgan signal sathi
        double demultiplexerPower = multiplexerPower - 12;  // demultipleksorga kirayotgan signal sathi

        List<Integer> amplifierList = new ArrayList<>();

        int kuchaytirgichlarSoni1 = (int) (kchopLength / amplifierMaxLength);
        int ortacha1;
        if (kchopLength - kuchaytirgichlarSoni1 * (int) amplifierMaxLength <= amplifierMaxLength / 2) {
            kuchaytirgichlarSoni1++;
            ortacha1 = kchopLength / kuchaytirgichlarSoni1;
            for (int i = 0; i < kuchaytirgichlarSoni1 - 1; i++) {
                amplifierList.add(ortacha1);
            }
            amplifierList.add((kchopLength - (kuchaytirgichlarSoni1 - 1) * ortacha1));
        } else {
            for (int i = 0; i < kuchaytirgichlarSoni1; i++) {
                amplifierList.add((int) amplifierMaxLength);
            }
            amplifierList.add((kchopLength - kuchaytirgichlarSoni1 * (int) amplifierMaxLength));
            kuchaytirgichlarSoni1++;
        }

        int kuchaytirgichlarSoni2 = (int) ((networkLength - kchopLength) / amplifierMaxLength);
        int ortacha2;
        if (networkLength - kchopLength - kuchaytirgichlarSoni2 * (int) amplifierMaxLength <= amplifierMaxLength / 2) {
            kuchaytirgichlarSoni2++;
            ortacha2 = (networkLength - kchopLength) / kuchaytirgichlarSoni2;
            for (int i = 0; i < kuchaytirgichlarSoni2 - 1; i++) {
                amplifierList.add(ortacha2);
            }
            amplifierList.add((networkLength - kchopLength - (kuchaytirgichlarSoni2 - 1) * ortacha2));
        } else {
            for (int i = 0; i < kuchaytirgichlarSoni2; i++) {
                amplifierList.add((int) amplifierMaxLength);
            }
            amplifierList.add((networkLength - kchopLength - kuchaytirgichlarSoni2 * (int) amplifierMaxLength));
            kuchaytirgichlarSoni2++;
        }

        int amplifierNum = amplifierList.size();
        List<Double> shovqinSathiList = new ArrayList<>();
        for (int i = 0; i < amplifierNum; i++) {
            shovqinSathiList.add(multiplexerPower-((amplifierList.get(i) * alfaEkv)+1));
        }
        List<Double> shovqinQuvvatiList = new ArrayList<>();
        for (int i = 0; i < amplifierNum; i++) {
            shovqinQuvvatiList.add(Math.pow(10, shovqinSathiList.get(i) / 10)/Math.pow(10,-6));
        }
        List<Double>shovqinHimoyaList = new ArrayList<>();
        for (int i = 0; i <amplifierNum; i++) {
            shovqinHimoyaList.add(shovqinSathiList.get(i)+52);
        }


        // O'zgaruvchilarni asosiy oynaga chiqarish
        String result = String.format(
                " So'nish koeffitsiyentining ekvivalent qiymati:  %.2f dB/km\n" +
                        " Kuchaytirgichlarning maksimal kuchaytirish masofasi:  %.2f km\n" +
                        " Multipleksordan chiqayotgan signal sathi:  %.2f dB\n" +
                        " Demultipleksor qabul qilayotgan signal sathi:  %.2f dB\n" +
                        " Jami kuchaytirgichlar soni: %d ta\n\n",
                alfaEkv, amplifierMaxLength, multiplexerPower, demultiplexerPower, amplifierNum
        );
        result += " Kuchaytirgichlarning kuchaytirish masofasi (km)\n";
        for (int i = 0; i < amplifierNum; i++) {
            result += " " + (i + 1) + ") " + amplifierList.get(i) + "\n";
        }
        result += " Kuchaytirgichlarning shovqin sathlari (dBq)\n";
        for (int i = 0; i < amplifierNum; i++) {
            // result += " " + (i + 1) + ") " + shovqinSathiList.get(i) + "\n";
            result += String.format(
                    " " + (i + 1) + ") %.2f\n",
                    shovqinSathiList.get(i)
            );
        }
        result += " Kuchaytirgichlarning shovqin quvvati (nVt)\n";
        for (int i = 0; i < amplifierNum; i++) {
            // result += " " + (i + 1) + ") " + shovqinQuvvatiList.get(i) + "\n";
            result += String.format(
                    " " + (i + 1) + ") %.2f\n",
                    shovqinQuvvatiList.get(i)
            );
        }
        result+=" Kuchaytirish uchastkalaridagi shovqindan himoyalanganlik (dB)\n";
        for (int i = 0; i <amplifierNum; i++) {
            result += String.format(
                    " " + (i + 1) + ") %.2f\n",
                    shovqinHimoyaList.get(i)
            );
        }
        mainFrame.displaySimulationResults(networkLength, kchopLength, result, amplifierList, kuchaytirgichlarSoni1 + 1, shovqinSathiList, shovqinQuvvatiList,channelsNum,multiplexerPower); // Natijalarni asosiy oynada ko'rsatish
    }
}
