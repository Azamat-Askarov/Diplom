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
    private JTextField firstSignalSizeField;
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
                "Optik signalning to'lqin uzunligi (nm)",
                "Tolaning so'nish koeffitsiyenti (db/km)",
                "Optik kanallar soni"
        };
        // tugmalarga kiritilgan qiymatlarni o'lashtirish
        JTextField[] fields = {
                networkLengthField = new JTextField(),
                kchopLengthField = new JTextField(),
                cableLengthField = new JTextField(),
                firstSignalSizeField = new JTextField(),
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
        String[] firstSignalLengthSuggestion = {"1530", "1535", "1540", "1545", "1550", "1555", "1560"};
        JComboBox<String> suggestedValuesComboBox = new JComboBox<>(firstSignalLengthSuggestion);
        suggestedValuesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedValue = (String) suggestedValuesComboBox.getSelectedItem();
                firstSignalSizeField.setText(selectedValue);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 3; // "Optik signalning boshlang'ich uzunligi" nomli maydon
        add(suggestedValuesComboBox, gbc);

        // "Tolaning so'nish koeffitsiyenti" uchun kiritilishi kerak bo'lgan qiymatlar ro'yxati
        String[] suggestedValues = {"0.2", "0.21", "0.22", "0.23", "0.24", "0.25", "0.26"};
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
        String[] networkLengthSuggestion = {"250", "300", "350", "400", "450", "500", "550", "600", "640"};
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
            int firstSignalSize = Integer.parseInt(firstSignalSizeField.getText());
            double lossKoeff = Double.parseDouble(lossKoeffField.getText()); // O'zgartirilgan nom
            int channelsNum = Integer.parseInt(channelsNumField.getText());

            simulateOpticalNetwork(networkLength, kchopLength, cableLength, firstSignalSize, lossKoeff, channelsNum);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Iltimos, barcha maydonlarni to'g'ri to'ldiring", "Xato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simulateOpticalNetwork(int networkLength, int kchopLength, double cableLength,
                                        int firstSignalSize, double lossKoeff, int channelsNum) {
        double alfaEkv = lossKoeff + (0.03 / cableLength);  // so'nish koeff
        double amplifierMaxLength = 29 / alfaEkv;  // kuchaytirgichlarning max masofasi
        double amplifierMinLength = 24 / alfaEkv;  // kuchaytirgichlarning min masofasi
        double multiplexerPower = 20 - 10 * Math.log10(channelsNum); // multipleksordan chiqayotgan signal sathi
        double demultiplexerPower = multiplexerPower - 12;  // demultipleksorga kirayotgan signal sathi

        int ortacha = (int) (amplifierMaxLength + amplifierMinLength) / 2;
        int kuchaytirgichlarSoni1 = kchopLength / ortacha;

        List<Integer> amplifierList = new ArrayList<>();

        for (int i = 0; i < kuchaytirgichlarSoni1; i++) {
            amplifierList.add(ortacha);
        }
        if (kchopLength - kuchaytirgichlarSoni1 * ortacha != 0) {
            amplifierList.add((int) (kchopLength - kuchaytirgichlarSoni1 * ortacha));
        }
        int kuchaytirgichlarSoni2 = (int) ((networkLength - kchopLength) / ortacha);

        if ((networkLength - kchopLength - kuchaytirgichlarSoni2 * ortacha) <= ortacha) {
            kuchaytirgichlarSoni2++;
            ortacha = (networkLength - kchopLength) / kuchaytirgichlarSoni2;
            for (int i = 0; i < kuchaytirgichlarSoni2 - 1; i++) {
                amplifierList.add(ortacha);
            }
            amplifierList.add((networkLength - kchopLength - (kuchaytirgichlarSoni2 - 1) * ortacha));
        } else {
            for (int i = 0; i < kuchaytirgichlarSoni2; i++) {
                amplifierList.add(ortacha);
            }
            amplifierList.add((int) (networkLength - kchopLength - kuchaytirgichlarSoni2 * ortacha));
        }
        int amplifierNum = amplifierList.size();
        List<Double> shovqinSathiList = new ArrayList<>();
        for (int i = 0; i < amplifierNum; i++) {
            shovqinSathiList.add(amplifierList.get(i) * alfaEkv - 63);
        }
        List<Integer> shovqinQuvvatiList = new ArrayList<>();
        for (int i = 0; i < amplifierNum; i++) {
            shovqinQuvvatiList.add((int) Math.floor(Math.pow(10, shovqinSathiList.get(i) / 10) / Math.pow(10, -6)));
        }
        // O'zgaruvchilarni asosiy oynaga chiqarish
        String result = String.format(
                " So'nish koeffitsiyentining ekvivalent qiymati:  %.2f dB/km\n" +
                        " Kuchaytirgichlarning maksimal kuchaytirish masofasi:  %.2f - %.2f km\n" +
                        " Multipleksordan chiqayotgan signal sathi:  %.2f dB\n" +
                        " Demultipleksor qabul qilayotgan signal sathi:  %.2f dB\n" +
                        " Jami kuchaytirgichlar soni: %d ta\n\n",
                alfaEkv, amplifierMinLength, amplifierMaxLength, multiplexerPower, demultiplexerPower, amplifierNum
        );
        result += " Kuchaytirgichlarning kuchaytirish masofasi (km)\n";
        for (int i = 0; i < amplifierNum; i++) {
            result += " " + (i + 1) + ") " + amplifierList.get(i) + "\n";
        }
        result += " Kuchaytirgichlarning shovqin sathlari (dBq)\n";
        for (int i = 0; i < amplifierNum; i++) {
            result += " " + (i + 1) + ") " + shovqinSathiList.get(i) + "\n";
        }
        result += " Kuchaytirgichlarning shovqin quvvati (nVt)\n";
        for (int i = 0; i < amplifierNum; i++) {
            result += " " + (i + 1) + ") " + shovqinQuvvatiList.get(i) + "\n";
        }
        mainFrame.displaySimulationResults(networkLength, kchopLength, result, amplifierList, shovqinSathiList, shovqinQuvvatiList); // Natijalarni asosiy oynada ko'rsatish
    }
}
