package com.interviewassistant;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioRecorder1 {

    private TargetDataLine line;

    public void startRecording() throws LineUnavailableException {


        AudioFormat format =
                new AudioFormat(
                        16000,
                        16,
                        1,
                        true,
                        true
                );

        DataLine.Info info =
                new DataLine.Info(
                        TargetDataLine.class,
                        format
                );

        line =
                (TargetDataLine)
                        AudioSystem.getLine(info);

        line.open(format);
        line.start();

        Thread recordingThread =
                new Thread(() -> {

                    AudioInputStream ais =
                            new AudioInputStream(line);

                    File file =
                            new File("audio.wav");

                    try {
                        AudioSystem.write(
                                ais,
                                AudioFileFormat.Type.WAVE,
                                file
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

        recordingThread.start();
    }

    public void stopRecording() {

        if (line != null) {
            line.stop();
            line.close();
        }
    }


    public String transcribeAudio() {

        try {

            ProcessBuilder pb =
                    new ProcessBuilder(
                            "whisper",
                            "audio.wav",
                            "--model",
                            "small",
                            "--language",
                            "English"
                    );

            pb.redirectErrorStream(true);

            Process process = pb.start();

            process.waitFor();
            System.out.println("Whisper finished");

            System.out.println("Reading transcript file...");

            String transcript =
                    Files.readString(
                            Paths.get("audio.txt")
                    );

            return transcript;

        } catch (Exception e) {

            e.printStackTrace();

            return "Transcription Failed";

        }
    }
}


