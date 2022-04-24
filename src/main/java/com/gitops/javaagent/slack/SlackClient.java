package com.gitops.javaagent.slack;

import com.gitops.javaagent.model.Alert;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SlackClient {
    public static OkHttpClient client = new OkHttpClient.Builder().build();

    public static void send(Alert alert) {
        String content = SlackTemplate.content(alert);
        Request.Builder builder = new Request.Builder();
        builder.url("https://hooks.slack.com/services/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");
        byte[] body = content.getBytes(UTF_8);
        builder.method("POST", RequestBody.create(body, MediaType.get("application/json")));
        try {
            Response response = client.newCall(builder.build()).execute();
//            System.out.println(response.body().string());
        } catch (IOException e) {

        }
    }

    public static void main(String[] args) {
        Alert alert = new Alert();
        alert.app = "app";
        alert.action = "action";
        alert.errorCode = "errorCode";
        alert.errorMessage = "errorMessage";
        alert.traceId = "traceId";
        alert.spanId = "spanId";
        alert.createdTime = LocalDateTime.now();
        SlackClient.send(alert);
    }
}
