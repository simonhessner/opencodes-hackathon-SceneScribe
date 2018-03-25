package de.zkm.opencodes.hackathon.scenescribe;

import java.io.File;
import java.io.IOException;

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

    }

    public void upload(final String url, final File file, final OnResponseCallback callback) throws IOException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", file.getName(),
                                    RequestBody.create(MediaType.parse("text/plain"), file))
                            .build();
                    Request request = new Request.Builder().url(url).post(formBody).build();
                    Response response = client.newCall(request).execute();

                    String caption = response.body().string().split("<S>")[0];

                    System.out.println("Response received:" + caption);
                    callback.receiveText(caption);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.receiveText("Internet problems, try again!");
                }
            }
        });

        thread.start();

    }
}
