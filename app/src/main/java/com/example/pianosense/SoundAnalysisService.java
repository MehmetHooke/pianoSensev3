package com.example.pianosense;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.Config;
import androidx.core.app.ActivityCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundAnalysisService {
    private static final String TAG = "SoundAnalysisService";
    private final File ffmpegFile;
    private final Context context;

    // Notaların frekansları
    private static final Map<String, Double> noteFrequencies = new HashMap<>();
    private String lastDetectedNote = "";

    static {
        // Notalar ve frekanslarını eşleştirme
        noteFrequencies.put("Do1", 32.7);
        noteFrequencies.put("Do#1", 34.65);
        noteFrequencies.put("Re1", 36.71);
        noteFrequencies.put("Re#1", 38.89);
        noteFrequencies.put("Mi1", 41.2);
        noteFrequencies.put("Fa1", 43.65);
        noteFrequencies.put("Fa#1", 46.25);
        noteFrequencies.put("Sol1", 49.0);
        noteFrequencies.put("Sol#1", 51.91);
        noteFrequencies.put("La1", 55.0);
        noteFrequencies.put("La#1", 58.27);
        noteFrequencies.put("Si1", 61.74);
        noteFrequencies.put("Do2", 65.41);
        noteFrequencies.put("Do#2", 69.3);
        noteFrequencies.put("Re2", 73.42);
        noteFrequencies.put("Re#2", 77.78);
        noteFrequencies.put("Mi2", 82.41);
        noteFrequencies.put("Fa2", 87.31);
        noteFrequencies.put("Fa#2", 92.5);
        noteFrequencies.put("Sol2", 98.0);
        noteFrequencies.put("Sol#2", 103.83);
        noteFrequencies.put("La2", 110.0);
        noteFrequencies.put("La#2", 116.54);
        noteFrequencies.put("Si2", 123.47);

        noteFrequencies.put("Do3", 130.81);
        noteFrequencies.put("Do#3", 138.59);
        noteFrequencies.put("Re3", 146.83);
        noteFrequencies.put("Re#3", 155.56);
        noteFrequencies.put("Mi3", 164.81);
        noteFrequencies.put("Fa3", 174.61);
        noteFrequencies.put("Fa#3", 185.0);
        noteFrequencies.put("Sol3", 196.0);
        noteFrequencies.put("Sol#3", 207.65);
        noteFrequencies.put("La3", 220.0);
        noteFrequencies.put("La#3", 233.08);
        noteFrequencies.put("Si3", 246.94);

        noteFrequencies.put("Do4", 261.63);
        noteFrequencies.put("Do#4", 277.18);
        noteFrequencies.put("Re4", 293.66);
        noteFrequencies.put("Re#4", 311.13);
        noteFrequencies.put("Mi4", 329.63);
        noteFrequencies.put("Fa4", 349.23);
        noteFrequencies.put("Fa#4", 369.99);
        noteFrequencies.put("Sol4", 392.0);
        noteFrequencies.put("Sol#4", 415.3);
        noteFrequencies.put("La4", 440.0);
        noteFrequencies.put("La#4", 466.16);
        noteFrequencies.put("Si4", 493.88);

        noteFrequencies.put("Do5", 523.25);
        noteFrequencies.put("Do#5", 554.37);
        noteFrequencies.put("Re5", 587.33);
        noteFrequencies.put("Re#5", 622.25);
        noteFrequencies.put("Mi5", 659.25);
        noteFrequencies.put("Fa5", 698.46);
        noteFrequencies.put("Fa#5", 739.99);
        noteFrequencies.put("Sol5", 783.99);
        noteFrequencies.put("Sol#5", 830.61);
        noteFrequencies.put("La5", 880.0);
        noteFrequencies.put("La#5", 932.33);
        noteFrequencies.put("Si5", 987.77);

        noteFrequencies.put("Do6", 1046.5);
        noteFrequencies.put("Do#6", 1108.73);
        noteFrequencies.put("Re6", 1174.66);
        noteFrequencies.put("Re#6", 1244.51);
        noteFrequencies.put("Mi6", 1318.51);
        noteFrequencies.put("Fa6", 1396.91);
        noteFrequencies.put("Fa#6", 1479.98);
        noteFrequencies.put("Sol6", 1567.98);
        noteFrequencies.put("Sol#6", 1661.22);
        noteFrequencies.put("La6", 1760.0);
        noteFrequencies.put("La#6", 1864.66);
        noteFrequencies.put("Si6", 1975.53);

        noteFrequencies.put("Do7", 2093.0);
        noteFrequencies.put("Do#7", 2217.46);
        noteFrequencies.put("Re7", 2349.32);
        noteFrequencies.put("Re#7", 2489.02);
        noteFrequencies.put("Mi7", 2637.02);
        noteFrequencies.put("Fa7", 2793.83);
        noteFrequencies.put("Fa#7", 2959.96);
        noteFrequencies.put("Sol7", 3135.96);
        noteFrequencies.put("Sol#7", 3322.44);
        noteFrequencies.put("La7", 3520.0);
        noteFrequencies.put("La#7", 3729.31);
        noteFrequencies.put("Si7", 3951.07);
    }

    public SoundAnalysisService(Context context) {
        this.context = context;
        ffmpegFile = new File(context.getCacheDir(), "ffmpeg");
        try {
            loadFFmpegBinary();
            Log.d(TAG, "FFmpeg binary başarıyla yüklendi ve çalıştırılabilir hale getirildi.");
        } catch (IOException e) {
            Log.e(TAG, "FFmpeg binary yüklenirken hata: " + e.getMessage());
        }
    }


    public void analyzeAudio(String wavFilePath) {
        try {
            // AudioDispatcher doğrudan WAV dosyasını işleyebilir
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(wavFilePath, 44100, 1024, 512);

            PitchDetectionHandler pdh = (result, e) -> {
                float pitchInHz = result.getPitch();

                if (pitchInHz > 0) {
                    String closestNote = findClosestNote(pitchInHz);
                    if (!closestNote.equals(lastDetectedNote)) {
                        lastDetectedNote = closestNote;
                        Log.d(TAG, "Algılanan Nota: " + closestNote + " Frekans: " + pitchInHz + " Hz");
                    }
                }
            };

            dispatcher.addAudioProcessor(new PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.YIN,
                    44100,
                    1024,
                    pdh
            ));

            new Thread(dispatcher::run).start();

        } catch (Exception e) {
            Log.e(TAG, "WAV dosyası analiz edilirken hata oluştu: " + e.getMessage());
        }
    }


    //kullanım dışı
    private void analyzePcmFile(String pcmFilePath) {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(pcmFilePath, 44100, 1024, 512);

            PitchDetectionHandler pdh = (result, e) -> {
                float pitchInHz = result.getPitch();

                if (pitchInHz > 0) {
                    String closestNote = findClosestNote(pitchInHz);
                    if (!closestNote.equals(lastDetectedNote)) {
                        lastDetectedNote = closestNote;
                        Log.d(TAG, "Algılanan Nota: " + closestNote + " Frekans: " + pitchInHz + " Hz");
                    }
                }
            };

            dispatcher.addAudioProcessor(new PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.YIN,
                    44100,
                    1024,
                    pdh
            ));

            new Thread(dispatcher::run).start();

        } catch (Exception e) {
            Log.e(TAG, "PCM dosyası analiz edilirken hata oluştu: " + e.getMessage());
        }
    }


    //tanımlanan ile static olan girilen nota arasında farkı en az olan notayı döndüren fonksiyondur
    public String findClosestNoteJava(float frequency) {
        String closestNote = "";
        double smallestDifference = Double.MAX_VALUE;

        for (Map.Entry<String, Double> entry : noteFrequencies.entrySet()) {
            double noteFrequency = entry.getValue();
            String note = entry.getKey();

            double difference = Math.abs(noteFrequency - frequency);
            if (difference < smallestDifference) {
                smallestDifference = difference;
                closestNote = note;
            }
        }

        return closestNote;
    }



    private void loadFFmpegBinary() throws IOException {
        File ffmpegFile = new File(context.getCacheDir(), "ffmpeg");

        if (!ffmpegFile.exists()) {
            try (InputStream is = context.getAssets().open("ffmpeg");
                 FileOutputStream fos = new FileOutputStream(ffmpegFile)) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
        }

        // ffmpeg dosyasını çalıştırılabilir yap
        if (!ffmpegFile.setExecutable(true)) {
            throw new IOException("FFmpeg dosyasına çalıştırma izni verilemedi.");
        }
    }


    private void ensureFFmpegPermissions() throws IOException {
        if (!ffmpegFile.exists()) {
            try (InputStream is = context.getAssets().open("ffmpeg");
                 FileOutputStream fos = new FileOutputStream(ffmpegFile)) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
        }

        // ffmpeg dosyasını çalıştırılabilir yap
        ffmpegFile.setExecutable(true);
        Runtime.getRuntime().exec("chmod 777 " + ffmpegFile.getAbsolutePath());
    }



    private String findClosestNote(double frequency) {
        String closestNote = "";
        double smallestDifference = Double.MAX_VALUE;

        for (Map.Entry<String, Double> entry : noteFrequencies.entrySet()) {
            double difference = Math.abs(entry.getValue() - frequency);
            if (difference < smallestDifference) {
                smallestDifference = difference;
                closestNote = entry.getKey();
            }
        }

        return closestNote;
    }
}
