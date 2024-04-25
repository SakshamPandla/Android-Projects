package com.sakshampandla.audio_recorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button bttn_recordWorking, bttn_stopWorking, bttn_playLastRecordedAudioWorking, bttn_stopPlayingRecordingWorking;
    private String AudioSavePathInDevice;
    private MediaRecorder my_mediaRecorder;
    private Random random;
    private String RandomAudioFileName = "vadhavdahbvsakhcbs";
    private MediaPlayer mp;

    private static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttn_recordWorking = findViewById(R.id.bttn_record);
        bttn_stopWorking = findViewById(R.id.bttn_stop);
        bttn_playLastRecordedAudioWorking = findViewById(R.id.bttn_play);
        bttn_stopPlayingRecordingWorking = findViewById(R.id.bttn_stopPlayingRecording);

        bttn_stopWorking.setEnabled(false);
        bttn_playLastRecordedAudioWorking.setEnabled(false);
        bttn_stopPlayingRecordingWorking.setEnabled(false);

        random = new Random();

        bttn_recordWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    MediaRecorderReady();
                    try {
                        my_mediaRecorder.prepare();
                        my_mediaRecorder.start();
                        Toast.makeText(MainActivity.this, "Recording started. File saved as: " + AudioSavePathInDevice, Toast.LENGTH_LONG).show();
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                    bttn_recordWorking.setEnabled(false);
                    bttn_stopWorking.setEnabled(true);
                } else {
                    requestPermission();
                }
            }
        });

        bttn_stopWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_mediaRecorder.stop();
                bttn_stopWorking.setEnabled(false);
                bttn_playLastRecordedAudioWorking.setEnabled(true);
                bttn_recordWorking.setEnabled(true);
                bttn_stopPlayingRecordingWorking.setEnabled(false);
                Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
            }
        });

        bttn_playLastRecordedAudioWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bttn_stopWorking.setEnabled(false);
                bttn_recordWorking.setEnabled(false);
                bttn_stopPlayingRecordingWorking.setEnabled(true);
                mp = new MediaPlayer();
                try {
                    mp.setDataSource(AudioSavePathInDevice);
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
                Toast.makeText(MainActivity.this, "Recording Playing", Toast.LENGTH_LONG).show();
            }
        });

        bttn_stopPlayingRecordingWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bttn_stopWorking.setEnabled(false);
                bttn_recordWorking.setEnabled(true);
                bttn_stopPlayingRecordingWorking.setEnabled(false);
                bttn_playLastRecordedAudioWorking.setEnabled(true);
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public void MediaRecorderReady() {
        my_mediaRecorder = new MediaRecorder();
        my_mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        my_mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        my_mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        my_mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < string; i++) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                RequestPermissionCode
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}
