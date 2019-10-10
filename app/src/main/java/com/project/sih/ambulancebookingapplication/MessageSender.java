package com.project.sih.ambulancebookingapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by allanbett on 12/12/18.
 */

class MessageSender {
    private static MessageSender instance = null;
    
    // put your Legacy Server Key from Firebase inside the double quotes.
    private static final String LEGACY_SERVER_KEY = "AAAAlWpcRUM:APA91bFC73bM0e6mPdJqr7Gn9SujuGw47zpz8CnCzTM9JPhz8rzZzJRVV06AcJv-kvgMqY5SiuvYbQnCz4BSkqpKC4Av4Cz1P2LN869YtbqcYBA5lvc1kUhZ76Qe7j2sQ0ChTU_biAq2";

    private MessageSender() {

    }

    public static MessageSender getInstance() {
        if(instance == null) {
            synchronized (MessageSender.class) {
                if(instance == null)
                    instance = new MessageSender();
            }
        }

        return instance;
    }

    public boolean sendMessage(String regToken, String body) {
        return sendMessage(regToken, body, null);
    }

    public boolean sendMessage(String regToken, String body, String title) {
        MyTask myTask = new MyTask(regToken, body, title);
        myTask.execute();

        return true;
    }

    private static class MyTask extends AsyncTask<Void, Void, Void> {
        String regToken, body, title;

        MyTask(String regToken, String body, String title) {
            this.regToken = regToken;
            this.body = body;
            this.title = title;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json=new JSONObject();
                JSONObject dataJson=new JSONObject();
                dataJson.put("body", body);
                dataJson.put("title", title);
                json.put("notification",dataJson);
                json.put("to", regToken);
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .header("Authorization","key="+LEGACY_SERVER_KEY)
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                String finalResponse = response.body().string();
            }catch (Exception e){
                Log.d("EXCEPTION_IN_SEND",e+"");
            }
            return null;
        }
    }
}
