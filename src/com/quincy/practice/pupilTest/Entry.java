package com.quincy.practice.pupilTest;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Entry extends Application {
    //以下是界面相关的变量与常量
    private static final int WINDOW_HEIGHT = 200;
    private static final int WINDOW_WIDTH = 350;
    private final Image IMAGE_RIGHT = new Image(Entry.class.getResource("right.png").toExternalForm());
    private final Image IMAGE_WRONG = new Image(Entry.class.getResource("wrong.png").toExternalForm());
    public static final String FONT_PATH = Entry.class.getResource("font.ttf").toExternalForm();
    private final Media SOUND_RIGHT = new Media(Entry.class.getResource("right.wav").toExternalForm());
    private final Media SOUND_WRONG = new Media(Entry.class.getResource("wrong.wav").toExternalForm());
    private final MediaPlayer PLAYER_RIGHT = new MediaPlayer(SOUND_RIGHT);
    private final MediaPlayer PLAYER_WRONG = new MediaPlayer(SOUND_WRONG);
    private static Scene welcomeScene;
    private static Scene gameScene;
    public static Stage primaryStage;
    private ImageView image_yesOrNo;
    private Button button_submit;
    private TextField answerField;
    private Label label_message;
    private Label label_quest;
    //界面常/变量结束
    private final int QUEST_NUM=10;
    public static int score;//记录用户得分的变量
    private int quest_num;//记录题号的变量
    private int chance;//记录剩余答题机会的变量
    private long startTime;//记录用户开始答题时间的变量
    private Quest currentQuest;//记录当前题目的变量
    Timer timer = new Timer();//计时器

    //程序入口
    @Override
    public void start(Stage primaryStage) throws Exception {
        Entry.primaryStage = primaryStage;//将primaryStage共享，方便其他模块调用
        initWelcomeScene();
        primaryStage.setScene(welcomeScene);
        Image icon = new Image(Entry.class.getResource("icon.png").toExternalForm());
        primaryStage.getIcons().add(icon);//主窗口图标
        primaryStage.setTitle("小学生数学测验系统");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e->{
            System.out.println(11);
        });
        primaryStage.show();
    }

    //初始化欢迎界面
    private void initWelcomeScene() {
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(WINDOW_WIDTH);
        root.setPrefHeight(WINDOW_HEIGHT);
        welcomeScene = new Scene(root);
        Image background = new Image(Entry.class.getResource("background.png").toExternalForm());
        root.setBackground(new Background(new BackgroundImage(background, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(WINDOW_WIDTH, WINDOW_HEIGHT, false,
                        false, false, false))));//背景图片

        //窗口大标题
        Label title = new Label("小学生数学测试系统");
        title.setTextFill(Paint.valueOf("#FF1493"));
        title.setFont(Font.loadFont(FONT_PATH, 35));
        AnchorPane.setLeftAnchor(title, 20.0);
        AnchorPane.setTopAnchor(title, 20.0);
        //窗口大标题结束

        //窗口副标题
        Label subtitle = new Label("作者：计算机211张健");
        subtitle.setTextFill(Paint.valueOf("#4169E1"));
        subtitle.setFont(Font.font("微软雅黑", FontWeight.BOLD, 18));
        AnchorPane.setLeftAnchor(subtitle, 90.0);
        AnchorPane.setTopAnchor(subtitle, 90.0);
        //窗口副标题结束

        //按钮组
        HBox buttonBox = new HBox();
        buttonBox.setStyle("-fx-font-size:14px;");//设置按钮文字大小为14像素
        Button button_start = new Button("开始");
        button_start.setPrefWidth(80);
        button_start.setPrefHeight(30);
        Button button_rank = new Button("排行榜");
        button_rank.setPrefWidth(80);
        button_rank.setPrefHeight(30);
        Button button_exit = new Button("退出");
        button_exit.setPrefWidth(80);
        button_exit.setPrefHeight(30);
        buttonBox.getChildren().addAll(button_start, button_rank, button_exit);
        buttonBox.setSpacing(20);
        AnchorPane.setTopAnchor(buttonBox, 140.0);
        AnchorPane.setLeftAnchor(buttonBox, 35.0);
        //按钮组结束

        //按钮事件
        button_start.setOnAction(e -> {
            startTime = System.currentTimeMillis();
            initGameScene();
            primaryStage.setScene(gameScene);
        });
        button_rank.setOnAction(e-> {
            try {
                new Rank().showRank(false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        button_exit.setOnAction(e -> {
            Platform.exit();
        });
        root.getChildren().addAll(title, subtitle, buttonBox);
    }

    //初始化游戏界面
    private void initGameScene() {
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(WINDOW_WIDTH);
        root.setPrefHeight(WINDOW_HEIGHT);
        gameScene = new Scene(root);
        Image background = new Image(Entry.class.getResource("background.png").toExternalForm());
        root.setBackground(new Background(new BackgroundImage(background, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(WINDOW_WIDTH, WINDOW_HEIGHT, false,
                        false, false, false))));//背景图片
        //信息显示区
        label_message = new Label();
        AnchorPane.setTopAnchor(label_message, 3.0);
        AnchorPane.setLeftAnchor(label_message, 0.0);
        AnchorPane.setRightAnchor(label_message, 0.0);
        label_message.setAlignment(Pos.CENTER);
        label_message.setTextFill(Paint.valueOf("purple"));
        label_message.setFont(Font.font("微软雅黑", FontWeight.BOLD, 20.0));
        //信息显示区结束

        //题目区
        label_quest = new Label();
        label_quest.setTextFill(Paint.valueOf("#00ff00"));
        label_quest.getStylesheets().add(Entry.class.getResource("style.css").toExternalForm());
        AnchorPane.setTopAnchor(label_quest, 55.0);
        AnchorPane.setLeftAnchor(label_quest, 0.0);
        AnchorPane.setRightAnchor(label_quest, 0.0);
        label_quest.setAlignment(Pos.CENTER);
        label_quest.setFont(Font.loadFont(Entry.class.getResource("font.ttf").toExternalForm(), 50));
        //题目区结束

        //答案输入区
        answerField = new TextField();
        answerField.setPrefColumnCount(4);
        answerField.setTextFormatter(new TextFormatter<Object>(change -> {
            if (change.getText().matches("\\d*"))
                return change;
            return null;
        }));//设置用户只能输入数字
        answerField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                button_submit.fire();
            }
        });//设置回车键为快捷键
        AnchorPane.setBottomAnchor(answerField, 20.0);
        AnchorPane.setRightAnchor(answerField, 80.0);
        //答案输入区结束

        //正误判断图标（默认隐藏）
        image_yesOrNo = new ImageView();
        AnchorPane.setRightAnchor(image_yesOrNo, 70.0);
        AnchorPane.setBottomAnchor(image_yesOrNo, 13.0);
        image_yesOrNo.setOpacity(0.0);
        //正误判断图标结束

        //提交按钮
        button_submit = new Button();
        AnchorPane.setRightAnchor(button_submit, 12.0);
        AnchorPane.setBottomAnchor(button_submit, 20.0);
        button_submit.setOnAction(e -> {
            int input;
            try {
                input = Integer.parseInt(answerField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText("您的输入有误！");
                alert.setContentText("请在输入框中输入您的答案。");
                alert.showAndWait();
                return;
            }
            if (input == currentQuest.getCorrectAnswer()) {
                int bonus;
                if (chance == 3) {
                    bonus = 10;
                } else if (chance == 2) {
                    bonus = 7;
                } else {
                    bonus = 5;
                }
                playSound(true);
                label_quest.setFont(Font.loadFont(FONT_PATH, 30));
                label_quest.setText("回答正确！获得 " + bonus + " 分！");
                score += bonus;
                showYesOrNoImage(true);
                shiftQuest();
            } else {
                playSound(false);
                chance--;
                updateChance();
                if (chance == 0) {
                    label_quest.setFont(Font.loadFont(FONT_PATH, 30));
                    label_quest.setText("很遗憾，正确答案：\n" + currentQuest + " = " + currentQuest.getCorrectAnswer());
                    showYesOrNoImage(false);
                    shiftQuest();
                } else {
                    showYesOrNoImage(false);
                    TimerTask recover = new TimerTask() {//隐藏图标，不切换题目，将操作权还给用户。
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                hideYesOrNoImage();
                            });
                        }
                    };
                    timer.schedule(recover, 1500);
                }
            }
        });
        //提交按钮结束
        root.getChildren().addAll(label_message, answerField, label_quest, button_submit, image_yesOrNo);
        initGameProcess();
    }

    //初始化游戏逻辑，对各种变量进行初始化
    public void initGameProcess() {//初始化游戏进程
        score = 0;
        quest_num = 0;
        generateNewQuest();
    }

    //程序尝试生成新题目（Quest对象）
    private void generateNewQuest() {//试图生成新题目
        if (quest_num == QUEST_NUM) {
            teminate();//游戏结束
            return;
        }
        label_quest.setFont(Font.loadFont(FONT_PATH, 50));
        quest_num++;
        label_message.setText("当前第 " + quest_num + " / 10 题  累计得分：" + score);
        Quest quest = new Quest();
        currentQuest = quest;
        label_quest.setText(quest.toString());
        chance = 3;
        updateChance();
    }

    //游戏结束，终止游戏，弹出姓名输入对话框
    private void teminate() {
        long endTime = System.currentTimeMillis();
        new ScoreUploader(score, endTime - startTime).showNameInputDialog();
    }

    //在UI中更新答题机会数，在用户回答错误时调用
    private void updateChance() {
        button_submit.setText("提交(" + chance + ")");
    }

    //展示正误图标
    private void showYesOrNoImage(boolean correct) {//展示判断结果，同时禁止用户再次提交
        image_yesOrNo.setImage(correct ? IMAGE_RIGHT : IMAGE_WRONG);
        image_yesOrNo.setOpacity(1.0);
        button_submit.setDisable(true);
    }

    //隐藏正误图标
    private void hideYesOrNoImage() {//隐藏图片，同时允许用户继续提交
        image_yesOrNo.setOpacity(0.0);
        button_submit.setDisable(false);
    }

    //切换到下一道题目
    private void shiftQuest() {//延迟1.5秒后进入下一题
        TimerTask task = new TimerTask() {//延迟1.5秒后的动作
            @Override
            public void run() {
                Platform.runLater(() -> {
                    answerField.clear();
                    hideYesOrNoImage();
                    generateNewQuest();
                });
            }
        };
        timer.schedule(task, 1500);
    }

    //播放判断正误的声音
    private void playSound(boolean correct) {
        if (correct) {
            PLAYER_RIGHT.stop();//重置播放器
            PLAYER_RIGHT.setVolume(0.5);
            PLAYER_RIGHT.play();
        } else {
            PLAYER_WRONG.stop();
            PLAYER_WRONG.setVolume(0.1);
            PLAYER_WRONG.play();
        }
    }

    //重置程序
    public static void reboot(){
        primaryStage.setScene(welcomeScene);
    }

    public static void main(String[] args) {
        Application.launch(Entry.class, args);
    }
}