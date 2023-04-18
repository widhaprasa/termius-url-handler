package com.github.widhaprasa.termius;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        Intent intent = buildIntentFromUri(uri);

        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }

    protected Intent buildIntentFromUri(Uri uri) {

        if (uri == null) {
            return null;
        }

        // Parse host and port
        String host = uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            port = 22;
        }

        // Parse query
        String query = uri.getQuery();
        Map<String, String> queryMap = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] token = query.split("&");
            for (String t : token) {
                int i = t.indexOf("=");
                try {
                    queryMap.put(URLDecoder.decode(t.substring(0, i), StandardCharsets.UTF_8.displayName()),
                            URLDecoder.decode(t.substring(i + 1), StandardCharsets.UTF_8.displayName()));
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }

        // Parse username and password
        String username = "root";
        String password = null;
        if (queryMap.containsKey("u")) {
            String b = queryMap.get("u");
            try {
                username = new String(Base64.decode(b, Base64.DEFAULT));
            } catch (IllegalArgumentException ignored) {
            }

            if (queryMap.containsKey("p")) {
                b = queryMap.get("p");
                try {
                    password = new String(Base64.decode(b, Base64.DEFAULT));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri termiusUri = Uri.parse("ssh://" + username + '@' + host + ':' + port);
        if (password != null) {
            intent.putExtra("com.serverauditor.password", password);
        }
        intent.setData(termiusUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
