package com.interviewassistant;

import javafx.scene.control.TextArea;
import java.util.ArrayList;
import javafx.stage.FileChooser;
import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.ProgressBar;


public class Main extends Application {
    private File selectedResume;
    private Scene mainScene;

    int score = 0;
    @Override
    public void start(Stage stage) {

        ArrayList<String> answers =
                new ArrayList<>();


        Label title = new Label("AI Interview Assistant");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        Label errorLabel = new Label();

        errorLabel.setStyle(
                "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
        );
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Full Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");

        Button uploadResume = new Button("Upload Resume");
        Button startInterview = new Button("Start Interview");


        uploadResume.setPrefWidth(200);
        startInterview.setPrefWidth(200);

        uploadResume.setOnAction(e -> {

            FileChooser chooser = new FileChooser();

            chooser.setTitle("Select Resume");

            File file = chooser.showOpenDialog(stage);

            if (file != null) {

                selectedResume = file;

                uploadResume.setText(file.getName());

            }

        });

        uploadResume.setStyle(
                "-fx-background-radius: 10;" +
                        "-fx-font-size: 14px;"
        );

        startInterview.setStyle(
                "-fx-background-radius: 10;" +
                        "-fx-font-size: 14px;"
        );

        startInterview.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if(name.isEmpty()) {
                errorLabel.setText("Please enter your name");
                return;
            }

            if(email.isEmpty()) {
                errorLabel.setText("Please enter your email");
                return;
            }

            if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                errorLabel.setText("Invalid email address");
                return;
            }

            if(selectedResume == null) {
                errorLabel.setText("Please upload a resume");
                return;
            }

            errorLabel.setText("");
            Label interviewError = new Label();
            interviewError.setStyle(
                    "-fx-text-fill: red;" +
                            "-fx-font-size: 14px;"
            );


            Label question = new Label(
                    "Tell me about yourself."
            );

            Label statusLabel = new Label("Not Recording");

            statusLabel.setStyle(
                    "-fx-text-fill: white;"
            );
            TextArea transcriptArea = new TextArea();

            transcriptArea.setEditable(false);

            transcriptArea.setPromptText(
                    "Transcript will appear here..."
            );

            transcriptArea.setPrefHeight(200);
            transcriptArea.setPrefWidth(500);
            Button startRecording =
                    new Button("Start Recording");

            Button stopRecording =
                    new Button("Stop Recording");

            AudioRecorder1 recorder =
                    new AudioRecorder1();

            startRecording.setOnAction(e2 -> {

                try {

                    recorder.startRecording();

                    statusLabel.setText(
                            "🔴 Recording..."
                    );

                }
                catch (Exception ex) {

                    ex.printStackTrace();

                }

            });

            stopRecording.setOnAction(e2 -> {

                recorder.stopRecording();

                statusLabel.setText("✅ Recording Finished");

                String transcript =
                        recorder.transcribeAudio();

                transcriptArea.setText(
                        transcript
                );

            });

            question.setStyle(
                    "-fx-font-size: 20px;" +
                            "-fx-text-fill: white;"
            );

            VBox interviewScreen = new VBox(10);

            interviewScreen.setAlignment(Pos.CENTER);

            interviewScreen.setStyle(
                    "-fx-background-color: #1E1E1E;"
            );


            statusLabel.setStyle(
                    "-fx-text-fill: white;"
            );

            Button nextQuestion =
                    new Button("Next Question");
            String[] questions = {
                    "Tell me about yourself.",
                    "What are your strengths?",
                    "Why should we hire you?",
                    "Tell me about a challenge you faced."
            };
            final int[] currentQuestion = {0};

            Label progressLabel = new Label();

            ProgressBar progressBar = new ProgressBar();

            progressBar.setPrefWidth(500);

            progressBar.setProgress(
                    1.0 / questions.length
            );

            progressBar.setMinWidth(500);
            progressBar.setPrefWidth(500);

            progressBar.setStyle(
                    "-fx-accent: #00BFFF;"
            );


            progressLabel.setStyle(
                    "-fx-text-fill: #00BFFF;" +
                            "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;"
            );
            progressLabel.setText(
                    "Question 1 of " + questions.length
            );


            Label timerLabel = new Label("05:00");

            timerLabel.setStyle(
                    "-fx-text-fill: yellow;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;"
            );
            final int[] secondsRemaining = {300};

            final Timeline[] timer = new Timeline[1];

            timer[0] = new Timeline(
                    new KeyFrame(
                            Duration.seconds(1),
                            event -> {

                                secondsRemaining[0]--;

                                int minutes =
                                        secondsRemaining[0] / 60;

                                int seconds =
                                        secondsRemaining[0] % 60;

                                timerLabel.setText(
                                        String.format(
                                                "%02d:%02d",
                                                minutes,
                                                seconds
                                        )
                                );

                                if(secondsRemaining[0] <= 0) {

                                    timer[0].stop();

                                    timerLabel.setText(
                                            "Time Up!"
                                    );
                                }

                            }
                    )
            );

            timer[0].setCycleCount(
                    Timeline.INDEFINITE
            );

            timer[0].play();


            nextQuestion.setOnAction(e2 -> {

                if(transcriptArea.getText().trim().isEmpty()) {

                    interviewError.setText(
                            "Please answer the current question first."
                    );

                    return;
                }

                interviewError.setText("");

                answers.add(
                        transcriptArea.getText()
                );

                currentQuestion[0]++;

                if(currentQuestion[0] < questions.length) {

                    question.setText(
                            questions[currentQuestion[0]]
                    );

                    progressLabel.setText(
                            "Question "
                                    + (currentQuestion[0] + 1)
                                    + " of "
                                    + questions.length
                    );

                    progressBar.setProgress(
                            (double) (currentQuestion[0] + 1)
                                    / questions.length
                    );

                    transcriptArea.clear();
                }

                else {

                    int score = 0;

                    for(String answer : answers) {

                        if(answer.length() >= 100) {

                            score += 3;

                        }
                        else if(answer.length() >= 50) {

                            score += 2;

                        }
                        else if(answer.length() >= 20) {

                            score += 1;

                        }

                    }

                    int maxScore = questions.length * 3;
                    double percentage =
                            ((double) score / maxScore) * 100;

                    String performance;

                    if(percentage >= 85) {

                        performance = "Excellent";

                    }
                    else if(percentage >= 70) {

                        performance = "Good";

                    }
                    else if(percentage >= 50) {

                        performance = "Average";

                    }
                    else {

                        performance = "Needs Improvement";

                    }

                    StringBuilder feedback =
                            new StringBuilder();

                    for(String answer : answers) {

                        if(answer.length() < 20) {

                            feedback.append(
                                    "Answer too short.\n"
                            );

                        }
                        else if(answer.length() < 50) {

                            feedback.append(
                                    "Could provide more detail.\n"
                            );

                        }
                        else {

                            feedback.append(
                                    "Good answer.\n"
                            );

                        }

                    }

                    Label scoreLabel =
                            new Label(
                                    "Score: "
                                            + score
                                            + " / "
                                            + maxScore
                            );

                    Label performanceLabel =
                            new Label(
                                    "Performance: "
                                            + performance
                            );

                    if(performance.equals("Excellent")) {
                        performanceLabel.setStyle(
                                "-fx-font-size: 18px;" +
                                        "-fx-text-fill: green;"
                        );
                    }
                    else if(performance.equals("Good")) {
                        performanceLabel.setStyle(
                                "-fx-font-size: 18px;" +
                                        "-fx-text-fill: orange;"
                        );
                    }
                    else {
                        performanceLabel.setStyle(
                                "-fx-font-size: 18px;" +
                                        "-fx-text-fill: red;"
                        );
                    }

                    scoreLabel.setStyle(
                            "-fx-font-size: 20px;" +
                                    "-fx-text-fill: gold;" +
                                    "-fx-font-weight: bold;"
                    );

                    performanceLabel.setStyle(
                            "-fx-font-size: 18px;" +
                                    "-fx-text-fill: lightgreen;"
                    );

                    Label completed =
                            new Label("Interview Complete");

                    TextArea summary =
                            new TextArea();

                    summary.setPrefHeight(300);

                    Button restartButton =
                            new Button("Restart Interview");
                    restartButton.setStyle(
                            "-fx-font-size: 16px;" +
                                    "-fx-background-radius: 10;"
                    );
                    restartButton.setOnAction(event -> {

                        answers.clear();

                        stage.setScene(mainScene);

                    });

                    StringBuilder report =
                            new StringBuilder();


                    for (int i = 0; i < answers.size(); i++) {

                        report.append(
                                "Question "
                                        + (i + 1)
                                        + "\n"
                        );

                        report.append(
                                answers.get(i)
                                        + "\n\n"
                        );
                    }

                    Label feedbackTitle =
                            new Label("Feedback");

                    feedbackTitle.setStyle(
                            "-fx-font-size: 20px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-text-fill: #1E90FF;"
                    );

                    TextArea feedbackArea =
                            new TextArea();

                    feedbackArea.setEditable(false);

                    feedbackArea.setText(
                            feedback.toString()
                    );

                    feedbackArea.setPrefWidth(500);

                    feedbackArea.setPrefHeight(150);

                    summary.setText(
                            report.toString()
                    );

                    try {

                        FileWriter writer =
                                new FileWriter(
                                        name.replace(" ", "_")
                                                + "_Interview_Report.txt"
                                );

                        writer.write(
                                "Candidate Name: "
                                        + name
                                        + "\n\n"
                                        + report.toString()
                        );

                        writer.close();

                    }
                    catch (IOException ex) {

                        ex.printStackTrace();

                    }

                    VBox finishedScreen =
                            new VBox(20);

                    finishedScreen.setAlignment(Pos.CENTER);

                    finishedScreen.getChildren().addAll(
                            completed,
                            scoreLabel,
                            performanceLabel,
                            feedbackTitle,
                            feedbackArea,
                            summary,
                            restartButton
                    );

                    completed.setStyle(
                            "-fx-font-size: 28px;" +
                                    "-fx-text-fill: green;" +
                                    "-fx-font-weight: bold;"
                    );

                    Button restart =
                            new Button("Restart Interview");

                    summary.setPrefWidth(500);
                    summary.setPrefHeight(300);

                    Scene finalScene =
                            new Scene(
                                    finishedScreen,
                                    700,
                                    500
                            );
                    timer[0].stop();
                    stage.setScene(finalScene);
                }

            });


            interviewScreen.getChildren().addAll(
                    progressLabel,
                    progressBar,
                    timerLabel,
                    question,
                    statusLabel,
                    startRecording,
                    stopRecording,
                    transcriptArea,
                    interviewError,
                    nextQuestion
            );

            Scene interviewScene =
                    new Scene(interviewScreen, 900, 700);

            stage.setScene(interviewScene);

        });

        VBox root = new VBox(20);


        root.setAlignment(Pos.CENTER);

        root.setStyle(
                "-fx-background-color: #1E1E1E;" +
                        "-fx-padding: 40;"
        );

        root.getChildren().addAll(
                title,
                nameField,
                emailField,
                errorLabel,
                uploadResume,
                startInterview
        );

        mainScene = new Scene(root, 700, 500);

        stage.setTitle("AI Interview Assistant");
        stage.setScene(mainScene);
        stage.show();
    }
    }


