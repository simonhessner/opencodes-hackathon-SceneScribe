package de.zkm.opencodes.hackathon.scenescribe;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Simon on 24.03.18.
 */

public class TTSService implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean ready = false;
    private OnInitCallBack onInitCallBack;

    public TTSService(Activity activity, OnInitCallBack onInitCallBack) {
        this.tts = new TextToSpeech(activity.getBaseContext(), this);
        this.onInitCallBack = onInitCallBack;
    }

    public interface OnInitCallBack {
        void run();
    }

    @Override
    public void onInit(int status) {

        if (status == android.speech.tts.TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                this.ready = true;
                onInitCallBack.run();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    public void speak(String text) {
        if (this.ready) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, "test");
        } else {
            Log.e("TTS", "Not initialized");
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
