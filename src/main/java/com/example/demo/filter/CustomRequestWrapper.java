package com.example.demo.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRequestWrapper extends HttpServletRequestWrapper {

    private static final int BUFFER_SIZE = 128;

    private String requestBody;

    public CustomRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        JsonObject jsonObject = null;
        Gson gsonBuilder = new GsonBuilder().create();
        if (isJsonArray(stringBuilder.toString())) {
            JsonArray jsonArray = new Gson().fromJson(stringBuilder.toString(), JsonArray.class);
            requestBody = "[";
            for (int n = 0; n < jsonArray.size(); n++) {
                jsonObject = (JsonObject) jsonArray.get(n);
                requestBody += gsonBuilder.toJson(jsonObject);
                requestBody += ",";
                sanitizeRequest(jsonObject);
            }
            requestBody = requestBody.substring(0, requestBody.length() - 1);
            requestBody += "]";
        } else {
            jsonObject = new Gson().fromJson(stringBuilder.toString(), JsonObject.class);
            requestBody = gsonBuilder.toJson(jsonObject);
           System.err.println("sanitisation check:"+stringBuilder);
            //sanitizeRequest(jsonObject);
        }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener listener) {
            }
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBody() {
        return this.requestBody;
    }

    private void sanitizeRequest(JsonObject requestBody) {
        JsonObject requestClone = null;
        if (Objects.nonNull(requestBody)) {
            requestClone = requestBody;
            if (requestClone.has("paymentMethod")) {
                JsonObject jsonObject1 = requestBody.getAsJsonObject("paymentMethod");
                if (Objects.nonNull(jsonObject1) && jsonObject1.has("creditCard")) {
                    JsonObject jsonObject = jsonObject1.getAsJsonObject("creditCard");
                    if (Objects.nonNull(jsonObject) && jsonObject.has("cardNumber")) {
                        jsonObject.remove("cardNumber");
                    }
                    if (jsonObject.has("cvvNumber")) {
                        jsonObject.remove("cvvNumber");
                    }
                    requestClone.add("creditCard", jsonObject);
                }
            }
        }
        Gson gsonBuilder = new GsonBuilder().create();
        log.debug("Incoming Request in filter {}", gsonBuilder.toJson(requestClone));
    }

    private boolean isJsonArray(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return element.isJsonArray();
        } catch (JsonSyntaxException e) {
            log.warn("Unable to parse request");
            return false;
        }
    }
}
