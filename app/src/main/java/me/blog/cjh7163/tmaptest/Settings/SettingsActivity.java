package me.blog.cjh7163.tmaptest.Settings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import me.blog.cjh7163.tmaptest.R;

public class SettingsActivity extends AppCompatActivity {
    private SeekBar slideOpacity;
    private SeekBar slideRed, slideGreen, slideBlue;
    private Preference preference;
    private ImageView ivColor;

    private CheckBox chkStabilize, chkCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preference = Preference.getInstance();

        chkStabilize = (CheckBox)findViewById(R.id.chkStabilize);
        chkStabilize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preference.isStabilize = isChecked;
            }
        });
        chkStabilize.setChecked(preference.isStabilize);

        chkCorrect = (CheckBox)findViewById(R.id.chkCorrect);
        chkCorrect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preference.isCorrect = isChecked;
            }
        });
        chkCorrect.setChecked(preference.isCorrect);

        slideOpacity = (SeekBar)findViewById(R.id.slideOpacity);
        slideOpacity.setOnSeekBarChangeListener(onSeekBarChangeListener);
        slideOpacity.setProgress((int)((preference.directionMarkOpacity - 0.1f) / 0.009f));

        slideRed = (SeekBar)findViewById(R.id.slideRed);
        slideRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
        slideRed.setProgress((int)(preference.color.R * 255));

        slideGreen = (SeekBar)findViewById(R.id.slideGreen);
        slideGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
        slideGreen.setProgress((int)(preference.color.G * 255));

        slideBlue = (SeekBar)findViewById(R.id.slideBlue);
        slideBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        slideBlue.setProgress((int)(preference.color.B * 255));

        ivColor = (ImageView)findViewById(R.id.ivColor);


        updateColor();
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                switch (seekBar.getId()) {
                    case R.id.slideOpacity: {
                        preference.directionMarkOpacity = (progress * 0.009f) + 0.10f;
                        break;
                    }
                    case R.id.slideRed: {
                        preference.color.R = progress / 255.0f;
                        break;
                    }
                    case R.id.slideGreen: {
                        preference.color.G = progress / 255.0f;
                        break;
                    }
                    case R.id.slideBlue: {
                        preference.color.B = progress / 255.0f;
                        break;
                    }
                    default:
                        return;
                }
                updateColor();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void updateColor() {
        ivColor.setBackgroundColor(
                Color.argb(
                        slideOpacity.getProgress(),
                        slideRed.getProgress(),
                        slideGreen.getProgress(),
                        slideBlue.getProgress()
                )
        );
    }
}
