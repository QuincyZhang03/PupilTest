package com.quincy.practice.pupilTest;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ScoreUploader {
    private String username;
    private int score;
    private long timeConsumption;

    //构造方法
    public ScoreUploader(int score, long timeConsumption) {
        this.score = score;
        this.timeConsumption = timeConsumption;
    }

    //显示姓名输入框
    public void showNameInputDialog() {
        Entry.primaryStage.hide();
        Stage inputStage = new Stage(StageStyle.UNDECORATED);
        AnchorPane pane = new AnchorPane();
        pane.setPrefWidth(150);
        pane.setPrefHeight(180);

        //提示文字
        Label label = new Label("您的得分是：" + score + "\n"+getGrade(score)+"\n请输入您的姓名：");
        label.setTextFill(Paint.valueOf("blue"));
        label.setFont(Font.loadFont(Entry.FONT_PATH,20));
        AnchorPane.setLeftAnchor(label, 10.0);
        AnchorPane.setTopAnchor(label, 10.0);
        //提示文字结束

        //姓名输入框
        TextField field_name = new TextField();
        Button button_submit = new Button("提交成绩");//提交按钮声明
        field_name.setPrefColumnCount(10);
        AnchorPane.setLeftAnchor(field_name, 12.0);
        AnchorPane.setTopAnchor(field_name, 110.0);
        //姓名输入框结束

        //提交按钮
        field_name.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER))
                button_submit.fire();
        });
        AnchorPane.setLeftAnchor(button_submit, 55.0);
        AnchorPane.setBottomAnchor(button_submit, 10.0);

        field_name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                button_submit.setDisable(newValue.equals(""));
            }
        });//根据输入框内容决定提交按钮是否可用
        button_submit.setOnAction(e -> {
            username = field_name.getText();
            inputStage.hide();
            try {
                saveToDataBase();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        //提交按钮结束
        pane.getChildren().addAll(label, field_name, button_submit);
        pane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResource("background.png").toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(150, 180, false, false, false
                        , true))));
        Scene scene = new Scene(pane);
        inputStage.setScene(scene);
        inputStage.setTitle("游戏结束");
        inputStage.getIcons().addAll(Entry.primaryStage.getIcons());
        inputStage.show();
    }

    //将数据保存到文件中
    private void saveToDataBase() throws IOException {
        File file = new File(System.getProperty("user.dir"), "rank.db");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file, true);
        writer.append(getCurrentTimeString() + "|" + username + "|" + score + "|" + (timeConsumption / 100 / 10.0) + "]");
        writer.close();
        new Rank().showRank(true);
    }

    //工具方法，将时间戳转化为时间文本
    private String getCurrentTimeString() {
        Date date = new Date();
        return (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + " "
                + twoDigitFormation(date.getHours()) + ":" + twoDigitFormation(date.getMinutes()) +
                ":" + twoDigitFormation(date.getSeconds());
    }

    //工具方法，将个位数补零
    private String twoDigitFormation(int input) {
        return (input < 10 ? "0" : "") + input;
    }

    //工具方法，将分数转化为题目要求的等级
    private String getGrade(int score){
        if(score>=90){
            return "SMART!";
        }else if(score>=80){
            return "GOOD!";
        } else if (score>=70) {
            return "OK";
        } else if (score>=60) {
            return "PASS";
        }
        return "TRY AGAIN...";
    }
}