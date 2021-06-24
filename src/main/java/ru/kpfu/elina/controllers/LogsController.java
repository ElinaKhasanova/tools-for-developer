package ru.kpfu.elina.controllers;

import ru.kpfu.elina.services.LogsService;
import ru.kpfu.elina.utils.CipherXml;
import ru.kpfu.elina.utils.Elements;
import ru.kpfu.elina.utils.XmlGenerator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class LogsController implements Initializable {

    @FXML
    private Label limitLabel;
    @FXML
    private TextField limitField;
    @FXML
    private CheckBox printConsoleCheckBox;
    @FXML
    private Label outputFileLabel;
    @FXML
    private Button browseButton;
    @FXML
    private TextField outputFileField;
    @FXML
    private Button logGenerateButton;
    @FXML
    private Button logStopButton;
    @FXML
    private Button logClearButton;

    @FXML
    private Label logMessage;
    @FXML
    private TextArea logsResult;

    private PrintWriter writer;
    private boolean running = false;
    private String dirPathXml = "C:\\Users\\elina\\Desktop\\dev-tools-master\\dev-tools-master\\src\\main\\java\\ru\\kpfu\\elina\\messages";
    private String dirPathCipher = "C:\\Users\\elina\\Desktop\\dev-tools-master\\dev-tools-master\\src\\main\\java\\ru\\kpfu\\elina\\ciphers";
    private File messageFile;
    private File cipherFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        HBox.setMargin(limitLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(limitField, new Insets(10, 5, 5, 0));
        HBox.setMargin(printConsoleCheckBox, new Insets(15, 5, 5, 10));

        HBox.setMargin(outputFileLabel, new Insets(15, 5, 5, 0));
        HBox.setMargin(browseButton, new Insets(10, 0, 5, 0));
        HBox.setMargin(outputFileField, new Insets(10, 5, 5, 0));
        HBox.setMargin(logGenerateButton, new Insets(10, 5, 5, 0));
        HBox.setMargin(logStopButton, new Insets(10, 5, 5, 0));
        HBox.setMargin(logClearButton, new Insets(10, 5, 5, 0));
        HBox.setMargin(logMessage, new Insets(15, 5, 5, 0));

        logsResult.setPrefRowCount(128);
        logStopButton.setDisable(true);
        logMessage.setTextFill(Color.RED);
    }

    @FXML
    private void handleSend(ActionEvent actionEvent) {
        System.out.println("Отправлено в MQ");
        //для обработки отправки
    }

    @FXML
    private void handleGenerate(ActionEvent actionEvent) {
        reset();
        if (!validate()) return;
        setRunning(true);
        CompletableFuture.supplyAsync(() -> {
            Iterator<String> iter = LogsService.logStream(
                    "Apache Common", 100, 1000,
                    Integer.parseInt(limitField.getText())).iterator();

            for (int i = 0; i <  Integer.parseInt(limitField.getText()); i++) {

                File dir = new File(dirPathXml);
                File dir2 = new File(dirPathCipher);
                messageFile = new File(dir.getAbsolutePath() + "/" + i + ".xml");
                cipherFile = new File(dir2.getAbsolutePath() + "/" + i + ".txt");

                try {
                    messageFile.createNewFile();
                    cipherFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                XmlGenerator xmlGenerator = new XmlGenerator();
                xmlGenerator.generateXml(i);
                ArrayList<String> lines = xmlGenerator.getLines();

                CipherXml cipherXml = new CipherXml();
                cipherXml.cipherXml(dir.getAbsolutePath() + "/" + i + ".xml",
                        dir2.getAbsolutePath() + "/" + i + ".txt");

                int j = 0;
                while (j < lines.size()) {
                    write(lines.get(j));
                    j++;
                    if (!running) break;
                }
            }
            return null;
        }).thenAccept(r -> {
            setRunning(false);
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                setRunning(false);
                logMessage.setText(e.getMessage());
            });
            return null;
        }).whenComplete((i, t) -> closeFile());
    }

    private void write(String line) {
        if (printConsoleCheckBox.isSelected()) {
            Platform.runLater(() -> {
                logsResult.appendText(line);
                logsResult.appendText("\n");
            });
        }
        if (writer != null) {
            writer.println(line);
            writer.flush();
        }
    }

    private void closeFile() {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    private void setRunning(boolean b) {
        logGenerateButton.setDisable(b);
        logStopButton.setDisable(!b);
        running = b;
    }

    @FXML
    private void handleClear(ActionEvent actionEvent) {
        reset();
        logsResult.setText("");
    }

    @FXML
    private void handleStop(ActionEvent actionEvent) {
        logStopButton.setDisable(true);
        running = false;
    }

    @FXML
    private void handleBrowse(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        File f = chooser.showDialog(null);
        if (f != null)
            outputFileField.setText(f.getPath());
    }

    private boolean validate() {
        if (!isNumericPositive(limitField.getText())) {
            limitField.setBorder(Elements.alertBorder);
            return false;
        } else {
            String fileName = outputFileField.getText();
            if (!fileName.isEmpty()) {
                try {
                    writer = new PrintWriter(new FileWriter(fileName));
                } catch (Exception e) {
                    outputFileField.setBorder(Elements.alertBorder);
                    return false;
                }
            }
        }
        return true;
    }

    private void reset() {
        limitField.setBorder(Border.EMPTY);
        outputFileField.setBorder(Border.EMPTY);
        logMessage.setText("");
    }

    private boolean isNumericPositive(String s) {
        return StringUtils.isNumeric(s) && Integer.parseInt(s) >= 0;
    }
}
