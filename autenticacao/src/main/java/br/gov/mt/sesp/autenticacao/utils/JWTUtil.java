package br.gov.mt.sesp.autenticacao.utils;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JWTUtil {

    public static JSONObject getPayload(String token) throws JSONException {
        if (token != null && token.startsWith("Bearer")) {
            token = token.split(" ")[1].trim();
        }

        String payload = token.split("\\.", 0)[1];
        byte[] bytes = Base64.getUrlDecoder().decode(payload);

        return new JSONObject(new String(bytes, StandardCharsets.UTF_8));
    }
}
