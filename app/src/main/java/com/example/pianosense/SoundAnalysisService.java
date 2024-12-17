package com.example.pianosense;

import android.content.Context;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.filters.LowPassFS;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class SoundAnalysisService {
    private static final String TAG = "SoundAnalysisService";
    private final Context context;
    FFmpeg fFmpeg;
    // Notaların frekansları
    private static final Map<String, Double> noteFrequencies = new HashMap<>();

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
    }

    public List<NoteInfo> analyzeWavFile(String wavFilePath,boolean isOriginal) {
        List<NoteInfo> noteInfoList = new ArrayList<>();
        try {
            // PCM dosyası oluştur
            File pcmFile = new File(context.getCacheDir(), "temp_audio.pcm");
            String pcmFilePath = pcmFile.getAbsolutePath();

            // WAV -> PCM dönüşümünü senkronize yap
            int result = FFmpeg.execute(new String[]{
                    "-y", "-i", wavFilePath, "-f", "s16le", "-ac", "1", "-ar", "44100", pcmFilePath
            });

            if (result == Config.RETURN_CODE_SUCCESS) {
                Log.d(TAG, "PCM conversion successful");
                analyzePcmFile(pcmFilePath, noteInfoList,isOriginal);
            } else {
                Log.e(TAG, "PCM conversion failed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing WAV file", e);
        }
        return noteInfoList;
    }



    //çalışan hali
    private void analyzePcmFile(String pcmFilePath, List<?> noteInfoList, boolean isOriginal) {
        try {
            FileInputStream fileInputStream = new FileInputStream(pcmFilePath);

            TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
                    44100,  // Örnekleme hızı
                    16,     // Bit derinliği
                    1,      // Kanal sayısı (mono)
                    true,   // İşaretli mi
                    false   // Big-endian değil
            );

            UniversalAudioInputStream audioInputStream = new UniversalAudioInputStream(fileInputStream, format);
            AudioDispatcher dispatcher = new AudioDispatcher(audioInputStream, 2048, 1024);


            // Zaman ve nota karşılaştırma için Wrapper sınıfı
            final AtomicReference<Double> lastTimestamp = new AtomicReference<>(-1.0);
            final AtomicReference<String> lastNote = new AtomicReference<>("");
            final double MIN_TIME_DIFF = 0.3; // Minimum zaman farkı (örneğin 0.3 saniye)

            dispatcher.addAudioProcessor(new PitchProcessor(
                    PitchProcessor.PitchEstimationAlgorithm.YIN,
                    44100f,
                    1024,
                    (pitchDetectionResult, audioEvent) -> {
                        float pitchInHz = pitchDetectionResult.getPitch();
                        if (pitchInHz > 0) {
                            String closestNote = findClosestNoteJava(pitchInHz);
                            double timestamp = audioEvent.getTimeStamp();
                            double formattedTimestamp = Math.round(timestamp * 100.0) / 100.0;



                            // Zaman farkı ve nota kontrolü
                            if ((lastTimestamp.get() == -1 || (timestamp - lastTimestamp.get()) >= MIN_TIME_DIFF) &&
                                    !closestNote.equals(lastNote.get())) {
                                if (isOriginal) {
                                    OriginalNoteInfo note = new OriginalNoteInfo(closestNote, formattedTimestamp);
                                    ((List<OriginalNoteInfo>) noteInfoList).add(note);
                                    //Log.d("SoundAnalysis", "Original Note Detected: " + note.toString());
                                } else {
                                    RecordedNoteInfo note = new RecordedNoteInfo(closestNote, formattedTimestamp);
                                    ((List<RecordedNoteInfo>) noteInfoList).add(note);
                                    Log.d("SoundAnalysis Analyze Pcmfile", "Recorded Note Detected: " + note.toString());
                                }

                                // Zaman damgasını ve notayı güncelle
                                lastTimestamp.set(timestamp);
                                lastNote.set(closestNote);
                            }
                        }
                    }
            ));

            dispatcher.run();
        } catch (Exception e) {
            Log.e("SoundAnalysisService", "PCM dosya analizi sırasında hata oluştu", e);
        }
    }






    public boolean convertWavToPcm(String inputWavPath, String outputPcmPath) {
        // FFmpeg komutu
        String[] command = {
                "-y",
                "-i", inputWavPath,
                "-f", "s16le",
                "-ac", "1",
                "-ar", "44100",
                outputPcmPath
        };

        // senkron FFmpeg çalıştırma
        FFmpeg.execute(command);


        return true; // Dönüşümün senkron olduğu için sadece işlem başlatıldığına dair bilgi döner
    }



    public List<ComparisonResult> comparePcmFiles(String originalPcmPath, String recordedPcmPath) {
        List<OriginalNoteInfo> originalNotes = new ArrayList<>();
        List<RecordedNoteInfo> recordedNotes = new ArrayList<>();
        List<ComparisonResult> comparisonResults = new ArrayList<>();

        try {
            // Orijinal PCM dosyasını analiz et
            analyzePcmFile(originalPcmPath, originalNotes, true);
            Log.d("ComparisonCheck", "Original Notes Size: " + originalNotes.size());
            for (OriginalNoteInfo note : originalNotes) {
                Log.d("ComparisonCheck", "Original Note: " + note.toString());
            }

            // Kaydedilen PCM dosyasını analiz et
            analyzePcmFile(recordedPcmPath, recordedNotes, false);
            Log.d("ComparisonCheck", "Recorded Notes Size: " + recordedNotes.size());
            for (RecordedNoteInfo note : recordedNotes) {
                Log.d("ComparisonCheck", "Recorded Note: " + note.toString());
            }

            // Notaları karşılaştır
            double TOLERANCE = 1; // 50 ms tolerans

            for (OriginalNoteInfo originalNote : originalNotes) {
                RecordedNoteInfo closestMatch = null;
                double smallestTimeDifference = Double.MAX_VALUE;

                for (RecordedNoteInfo recordedNote : recordedNotes) {
                    double timeDifference = Math.abs(originalNote.getTimestamp() - recordedNote.getTimestamp());
                    if (timeDifference < smallestTimeDifference && timeDifference <= TOLERANCE) {
                        smallestTimeDifference = timeDifference;
                        closestMatch = recordedNote;
                    }
                }

                // En yakın eşleşme yoksa "No Match" ekle
                if (closestMatch != null) {
                    comparisonResults.add(new ComparisonResult(
                            new NoteInfo(originalNote.getNote(), null, originalNote.getTimestamp(), false),
                            new NoteInfo(null, closestMatch.getNote(), closestMatch.getTimestamp(), false),
                            originalNote.getNote().equals(closestMatch.getNote())
                    ));
                    Log.d("ComparisonCheck", "Match Found: Original - " + originalNote.getNote() +
                            ", Recorded - " + closestMatch.getNote() +
                            ", Is Correct: " + originalNote.getNote().equals(closestMatch.getNote()));
                } else {
                    comparisonResults.add(new ComparisonResult(
                            new NoteInfo(originalNote.getNote(), null, originalNote.getTimestamp(), false),
                            new NoteInfo(null, "No Match", 0.0, false),
                            false
                    ));
                    Log.d("ComparisonCheck", "No match found for original note: " + originalNote.getNote());
                }
            }
        } catch (Exception e) {
            Log.e("SoundAnalysisService", "PCM dosyaları karşılaştırılırken hata oluştu", e);
        }

        // Tüm karşılaştırma sonuçlarını logla
        for (ComparisonResult result : comparisonResults) {
            Log.d("ComparisonResult", "Original Note: " + result.getOriginalNote() +
                    ", Recorded Note: " + result.getRecordedNote() +
                    ", Time: " + String.format("%.2f", result.getOriginalNote().getTimestamp()) +
                    ", Status: " + (result.isCorrect() ? "Correct" : "Wrong"));
        }

        return comparisonResults;
    }







    //////////

    public static String findClosestNoteJava(float frequency) {
        String closestNote = "";
        double smallestDifference = Double.MAX_VALUE;

        // Tolerans (eşik değeri) belirliyoruz
        final double TOLERANCE = 2.0; // Hz cinsinden, ayarlanabilir

        // Frekanslara göre en yakın notayı bulma
        for (Map.Entry<String, Double> entry : noteFrequencies.entrySet()) {
            double noteFrequency = entry.getValue();
            String note = entry.getKey();

            // Mutlak farkı hesapla
            double difference = Math.abs(noteFrequency - frequency);

            // Eğer fark küçükse ve eşik değerini geçmiyorsa
            if (difference < smallestDifference && difference <= TOLERANCE) {
                smallestDifference = difference;
                closestNote = note;
            }
        }

        // Eğer hala uygun bir nota bulunmadıysa, küçük toleransla en yakınını seç
        if (closestNote.isEmpty()) {
            for (Map.Entry<String, Double> entry : noteFrequencies.entrySet()) {
                double noteFrequency = entry.getValue();
                String note = entry.getKey();

                double difference = Math.abs(noteFrequency - frequency);
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    closestNote = note;
                }
            }
        }

        return closestNote;
    }



}
