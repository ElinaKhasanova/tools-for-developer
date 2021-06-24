package ru.kpfu.elina.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MqController implements Initializable {
    @FXML
    private Label textMq;
    @FXML
    private Button sendButton;
    @FXML
    private CheckBox txtCheckBox;
    @FXML
    private CheckBox datCheckBox;
    @FXML
    private CheckBox objCheckBox;
    @FXML
    private Label textFormat;
    @FXML
    private Label inputDir;
    @FXML
    private Button browseButtonMq;
    @FXML
    private TextField outputFileFieldMq;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox.setMargin(textFormat, new Insets(10, 5, 5, 0));
        HBox.setMargin(txtCheckBox, new Insets(10, 5, 5, 0));
        HBox.setMargin(datCheckBox, new Insets(10, 5, 5, 0));
        HBox.setMargin(objCheckBox, new Insets(10, 5, 5, 0));

        HBox.setMargin(inputDir, new Insets(10, 5, 5, 0));
        HBox.setMargin(browseButtonMq, new Insets(10, 5, 5, 0));
        HBox.setMargin(outputFileFieldMq, new Insets(10, 5, 5, 0));

        HBox.setMargin(textMq, new Insets(15, 5, 5, 0));
        HBox.setMargin(sendButton, new Insets(10, 0, 5, 0));
    }

    @FXML
    private void handleSend(ActionEvent actionEvent) {

        try {
            Process proc = Runtime.getRuntime().exec("cmd.exe /c start C:\\Jmeter\\apache-jmeter-5.4.1\\apache-jmeter-5.4.1\\bin\\ApacheJMeter.jar");
            proc.waitFor();
            proc.destroy();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleBrowseMq(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        File f = chooser.showDialog(null);
        if (f != null)
            outputFileFieldMq.setText(f.getPath());
    }

}
