package de.zkm.opencodes.hackathon.scenescribe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.IOException;

interface OnResponseCallback {
    void receiveText(String text);
}

// TODOs:
// 1. Resize image direclty in app

public class MainActivity extends Activity  {
    public TTSService tts;
    private Camera mCamera = null;
    private APIConnector api = new APIConnector();
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestPermissions();

        this.tts = new TTSService(this);

        trySetupPreview();

        //btn to close the application
        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        //btn to take picture
        ImageButton imgTakePicture = (ImageButton)findViewById(R.id.imgTakePicture);
        imgTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhoto();
            }
        });
    }

    private void trySetupPreview() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            if (mPreview == null) {
                setupPreview();
            } else {
                // Try restart preview
            }
        }
    }

    private void setupPreview() {
        mPreview = new CameraPreview(this);
        ((FrameLayout)findViewById(R.id.camera_preview)).addView(mPreview);
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhoto();
            }
        });

    }

    private void sendPhoto() {
        // Little hack because disabling frame onclick event does not seem to work => double click causes crash
        if (!((ImageButton)findViewById(R.id.imgTakePicture)).isEnabled()) {
            return;
        }

        ((ImageButton)findViewById(R.id.imgTakePicture)).setEnabled(false);
        ((FrameLayout)findViewById(R.id.camera_preview)).setEnabled(false);
        ((TextView)findViewById(R.id.text_description)).setText("Please wait...");
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        mPreview.mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    api.upload("http://13.93.105.66:9999/image", data, new OnResponseCallback() {
                        @Override
                        public void receiveText(final String text) {
                            tts.speak(text);
                            mPreview.mCamera.startPreview();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView)findViewById(R.id.text_description)).setText(text);
                                    ((ImageButton)findViewById(R.id.imgTakePicture)).setEnabled(true);
                                    ((FrameLayout)findViewById(R.id.camera_preview)).setEnabled(true);
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openCamera() {
        if(mCamera != null) return;

        try{
            mCamera = Camera.open(0);//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
            return;
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private String[] requiredPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE
    };

    private boolean isPermissionMissing() {
        for (String permission : requiredPermissions) {
            if (!checkPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        if (!isPermissionMissing()) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int res : grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions();
                return;
            }
        }
        trySetupPreview();
    }


    @Override
    public void onDestroy() {
        tts.shutdown();
        super.onDestroy();
    }
}
