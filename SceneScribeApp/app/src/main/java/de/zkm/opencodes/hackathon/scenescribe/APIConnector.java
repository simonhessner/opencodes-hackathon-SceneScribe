package de.zkm.opencodes.hackathon.scenescribe;


import android.content.res.Resources;
import android.support.v4.content.res.ResourcesCompat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by simon on 24.03.18.
 */

public class APIConnector {
    OkHttpClient client = new OkHttpClient();

    public APIConnector() {
        File file = new File(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_launcher_background, null).);
        try {
            this.upload("http://localhost:5000", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Response response = this.client.newCall(request).execute();
    }
}
