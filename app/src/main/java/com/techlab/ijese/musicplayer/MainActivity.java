package com.techlab.ijese.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener { // implement View.OnclickListener
    private MediaPlayer mediaPlayer;
    private TextView songName, artistName, leftTime, rightTime;
    private Button previousButton, playButton, nextButton;
    private SeekBar seekBar;
    private ImageView artistImage;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(dateFormat.format(new Date(currentPosition))); // Taking the current position and cast it as date object to display as leftTime
                rightTime.setText(dateFormat.format(new Date(duration - currentPosition)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    // Use a method to setup UI kits, to reduce redundant code when using OnClickListener
     public void setUpUI() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.you_i_see);

         songName = (TextView) findViewById(R.id.songNameTextViewID);
         artistName = (TextView) findViewById(R.id.artistNameTextViewID);
         leftTime = (TextView) findViewById(R.id.leftTimeID);
         rightTime = (TextView) findViewById(R.id.rightTimeID);
         previousButton = (Button) findViewById(R.id.prevButtonID);
         playButton = (Button) findViewById(R.id.playButtonID);
         nextButton = (Button) findViewById(R.id.nextButtonID);
         seekBar = (SeekBar) findViewById(R.id.seekBarID);
         artistImage = (ImageView) findViewById(R.id.artistImageViewID);

         // Set buttons' OnClickListeners
         previousButton.setOnClickListener(this);
         playButton.setOnClickListener(this);
         nextButton.setOnClickListener(this);
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prevButtonID:
                backMusic();
                break;

            case R.id.playButtonID:
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
                break;

            case R.id.nextButtonID:
                forwardMusic();
                break;
        }
    }
    // Pause method
    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
    // Play method
    public void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }
    // Previous or rewind method
    public void backMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
        }
    }
    // Next or forward
    public void forwardMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
        }
    }

    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                // Update time texts
                                leftTime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                .format(new Date(mediaPlayer.getCurrentPosition()))));

                                rightTime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                .format(new Date(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition()))));
                            }
                        });
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    // Clean up and return memory to avoid slowing down device
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
