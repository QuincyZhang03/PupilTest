package com.quincy.practice.pupilTest;

import java.util.Random;

public class Quest {
    private int num1, num2, correctAnswer;//两个操作数和正确答案
    private boolean isAddition;//true为加法，false为减法

    public Quest() {
        Random random = new Random();
        do {
            num1 = random.nextInt(49) + 1;
            num2 = random.nextInt(49) + 1;
            isAddition = random.nextInt(2) == 1;
            correctAnswer = isAddition ? num1 + num2 : num1 - num2;
        } while (correctAnswer > 50 || correctAnswer < 0);
    }//构造方法

    //供外界调用的接口方法，获取题目正确答案
    public int getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public String toString() {
        return num1 + (isAddition ? " + " : " - ") + num2;
    }
}