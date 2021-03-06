package ru.kpfu.elina.controllers;

import ru.kpfu.elina.services.EpochService;
import ru.kpfu.elina.utils.Elements;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class EpochController implements Initializable {

    @FXML
    private Label currentEpochLabel;
    @FXML
    private TextField currentEpoch;
    @FXML
    private Button currentEpochRefreshButton;
    @FXML
    private TextField tsToHumanField;
    @FXML
    private Button tsToHumanButton;
    @FXML
    private Button millisToTimeButton;
    @FXML
    private TextArea tsToHumanResult;
    @FXML
    private Button humanToTsButton;
    @FXML
    private TextArea humanToTsResult;
    @FXML
    private TextField epochYear;
    @FXML
    private TextField epochMonth;
    @FXML
    private TextField epochDay;
    @FXML
    private TextField epochHour;
    @FXML
    private TextField epochMinute;
    @FXML
    private TextField epochSecond;
    @FXML
    private ComboBox<String> timeZoneComboBox;
    private int timeZoneComboBoxIndex;

    @FXML
    private void handleRefreshEpoch(final ActionEvent event) {
        currentEpoch.setText(Long.toString(System.currentTimeMillis()));
    }

    @FXML
    private void handleTsToHumanEpoch(final ActionEvent event) {
        tsToHumanField.setBorder(Border.EMPTY);
        try {
            LocalDateTime dt = EpochService.tsToLocalDateTime(tsToHumanField.getText());
            String result = EpochService.toHumanEpoch(dt);
            tsToHumanResult.setText(result);
        } catch (Exception e) {
            tsToHumanField.setBorder(Elements.alertBorder);
            tsToHumanResult.setText("");
        }
    }

    @FXML
    private void handleMillisToTime(final ActionEvent actionEvent) {
        tsToHumanField.setBorder(Border.EMPTY);
        try {
            long millis = Long.parseLong(tsToHumanField.getText());
            String result = DurationFormatUtils.formatDurationWords(millis, true, true);
            tsToHumanResult.setText(result);
        } catch (Exception e) {
            tsToHumanField.setBorder(Elements.alertBorder);
            tsToHumanResult.setText("");
        }
    }

    @FXML
    private void handleHumanToTsEpoch(final ActionEvent event) {
        resetBorders();
        try {
            int year = EpochService.validate(epochYear, 1970, Integer.MAX_VALUE);
            int month = EpochService.validate(epochMonth, 1, 12);
            int day = EpochService.validate(epochDay, 1, 31);
            int hour = EpochService.validate(epochHour, 0, 24);
            int minute = EpochService.validate(epochMinute, 0, 59);
            int second = EpochService.validate(epochSecond, 0, 59);
            String timeZone = timeZoneComboBox.getSelectionModel().getSelectedItem();
            String result = EpochService.toTsEpoch(year, month, day, hour, minute, second, timeZone);
            humanToTsResult.setText(result);
        } catch (Exception e) {
            humanToTsResult.setText("");
        }
    }

    @FXML
    private void handleTimeZoneSearch(KeyEvent keyEvent) {
        String key = keyEvent.getText();
        if (key.length() == 0) return;
        int i = 0;
        for (String item : timeZoneComboBox.getItems()) {
            if (item.toLowerCase().startsWith(key) && i > timeZoneComboBoxIndex) {
                timeZoneComboBox.setValue(item);
                timeZoneComboBoxIndex = i;
                return;
            }
            i++;
        }
        timeZoneComboBoxIndex = 0;
    }

    private void resetBorders() {
        epochYear.setBorder(Border.EMPTY);
        epochMonth.setBorder(Border.EMPTY);
        epochDay.setBorder(Border.EMPTY);
        epochHour.setBorder(Border.EMPTY);
        epochMinute.setBorder(Border.EMPTY);
        epochSecond.setBorder(Border.EMPTY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HBox.setMargin(currentEpochLabel, new Insets(15, 5, 10, 0));
        HBox.setMargin(currentEpoch, new Insets(10, 5, 10, 0));
        HBox.setMargin(currentEpochRefreshButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(tsToHumanField, new Insets(10, 5, 10, 0));
        HBox.setMargin(tsToHumanButton, new Insets(10, 5, 10, 0));
        HBox.setMargin(millisToTimeButton, new Insets(10, 5, 10, 0));

        GridPane.setMargin(epochYear, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochMonth, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochDay, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochHour, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochMinute, new Insets(10, 5, 0, 0));
        GridPane.setMargin(epochSecond, new Insets(10, 5, 0, 0));
        GridPane.setMargin(humanToTsButton, new Insets(10, 5, 0, 0));
        GridPane.setMargin(timeZoneComboBox, new Insets(10, 5, 0, 0));

        long now = System.currentTimeMillis();
        currentEpoch.setText(Long.toString(now));
        tsToHumanField.setText(Long.toString(now));

        LocalDateTime date = LocalDateTime.now();
        epochYear.setText(String.valueOf(date.getYear()));
        epochMonth.setText(String.valueOf(date.getMonthValue()));
        epochDay.setText(String.valueOf(date.getDayOfMonth()));
        epochHour.setText(String.valueOf(date.getHour()));
        epochMinute.setText(String.valueOf(date.getMinute()));
        epochSecond.setText(String.valueOf(date.getSecond()));

        timeZoneComboBox.getItems().setAll(TimeZone.getAvailableIDs());
        timeZoneComboBox.setValue("UTC");
        timeZoneComboBoxIndex = 0;
    }
}
