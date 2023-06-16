package com.quincy.practice.pupilTest;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Rank {
    private final TableView<PlayerMessage> list;
    private final Stage stage;
    private final HBox hBox;

    //构造方法，进行界面初始化
    public Rank() throws IOException {
        stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(e->{
            if(!Entry.primaryStage.isShowing()){
                System.exit(0);
            }
        });
        AnchorPane pane = new AnchorPane();
        pane.setPrefHeight(400);
        pane.setPrefWidth(300);
        stage.setScene(new Scene(pane));
        stage.setTitle("排行榜");
        //表格
        list = new TableView<>();
        list.setPrefHeight(320);
        list.setPrefWidth(301);
        list.setBorder(new Border(new BorderStroke(Paint.valueOf("blue"),
                BorderStrokeStyle.DASHED,CornerRadii.EMPTY,new BorderWidths(1))));
        AnchorPane.setLeftAnchor(list, 0.0);
        AnchorPane.setTopAnchor(list, 0.0);
        TableColumn<PlayerMessage, String> column_time_submit = new TableColumn<>();
        TableColumn<PlayerMessage, String> column_username = new TableColumn<>();
        TableColumn<PlayerMessage, String> column_score = new TableColumn<>();
        TableColumn<PlayerMessage, String> column_time_consumption = new TableColumn<>();

        column_score.setSortType(TableColumn.SortType.DESCENDING);
        column_time_consumption.setSortType(TableColumn.SortType.ASCENDING);
        column_username.setSortable(false);
        column_score.setComparator((o1, o2) -> Integer.parseInt(o1)-Integer.parseInt(o2));
        column_time_consumption.setComparator((o1,o2)-> (int) (10*(Double.parseDouble(o1)-Double.parseDouble(o2))));
        list.getSortOrder().addAll(column_score,column_time_consumption);


        column_time_submit.setText("时间");
        column_username.setText("姓名");
        column_score.setText("得分");
        column_time_consumption.setText("用时/秒");

        column_time_submit.setStyle("-fx-alignment: center;");
        column_username.setStyle("-fx-alignment: center;");
        column_score.setStyle("-fx-alignment: center;");
        column_time_consumption.setStyle("-fx-alignment: center;");

        column_time_submit.setPrefWidth(125);
        column_username.setPrefWidth(70);
        column_score.setPrefWidth(40);
        column_time_consumption.setPrefWidth(60);

        column_time_submit.setCellValueFactory(p -> p.getValue().time_submit);
        column_username.setCellValueFactory(p -> p.getValue().username);
        column_score.setCellValueFactory(p -> p.getValue().score);
        column_time_consumption.setCellValueFactory(p -> p.getValue().time_consumption);

        list.getColumns().addAll(column_time_submit, column_username, column_score, column_time_consumption);
        //表格结束

        //按钮组
        hBox = new HBox();
        AnchorPane.setLeftAnchor(hBox,46.0);
        AnchorPane.setBottomAnchor(hBox,28.0);
        Button button_replay=new Button("重新开始");
        Button button_exit=new Button("退出");
        button_replay.setPrefWidth(80);
        button_replay.setPrefHeight(30);
        button_replay.setFont(Font.font(15));
        button_exit.setPrefWidth(80);
        button_exit.setPrefHeight(30);
        button_exit.setFont(Font.font(15));
        hBox.getChildren().addAll(button_replay,button_exit);
        hBox.setSpacing(50);
        //按钮组结束

        //按钮事件
        button_replay.setOnAction(e->{
            Entry.reboot();
            Entry.primaryStage.show();
            stage.hide();
        });
        button_exit.setOnAction(e-> System.exit(0));
        //按钮事件结束
        pane.getChildren().addAll(list, hBox);
        messageProcess();
    }

    //展示排行榜窗口
    public void showRank(boolean isGameOver){
        if(isGameOver){
            hBox.setOpacity(1.0);
            list.setPrefHeight(320);
        }else{
            hBox.setOpacity(0.0);
            list.setPrefHeight(400);
        }
        list.sort();
        stage.show();
    }

    //加载文件
    private String load() throws IOException {
        File file = new File(System.getProperty("user.dir"), "rank.db");
        FileReader reader = new FileReader(file);
        char[] buf = new char[1];
        StringBuilder builder = new StringBuilder();
        while (reader.read(buf) != -1) {
            builder.append(buf);
        }
        reader.close();
        return builder.toString();
    }

    //处理文件信息，将文件储存信息加入列表
    private void messageProcess() throws IOException {
        String content = load();
        if (content.isEmpty()) {
            return;
        }
        String[] content_split = content.split("]");
        for (String msg : content_split) {
            String[] data = msg.split("\\|");
            list.getItems().add(new PlayerMessage(data[0], data[1], data[2], data[3]));
        }
    }
}
//表格信息类
class PlayerMessage {
    SimpleStringProperty time_submit;
    SimpleStringProperty username;
    SimpleStringProperty score;
    SimpleStringProperty time_consumption;

    public PlayerMessage(String time_submit, String username, String score, String time_consumption) {
        this.time_submit = new SimpleStringProperty(time_submit);
        this.username = new SimpleStringProperty(username);
        this.score = new SimpleStringProperty(score);
        this.time_consumption = new SimpleStringProperty(time_consumption);
    }
}