package br.gov.mt.sesp.autenticacao.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HTTPUtil {

    public enum HTTPMethod {
        GET,
        POST;
    }

    public enum ContentType {
        FORM("application/x-www-form-urlencoded"),
        JSON("application/json");

        String valor;

        private ContentType(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }

    public static String enviarRequisicao(String url, HTTPMethod method, ContentType contentType, String parametros, String token) throws IOException {
        HttpURLConnection connection = null;
        StringBuilder resposta = new StringBuilder();

        try {
            URL urlConexao = new URL(url);
            connection = (HttpURLConnection) urlConexao.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod(method.toString());
            connection.setRequestProperty("User-Agent", "Java client");
            connection.setRequestProperty("Content-Type", contentType.getValor());

            if (token != null && !token.isEmpty()) {
                if (!token.startsWith("Bearer")) {
                    token = "Bearer " + token;
                }

                connection.setRequestProperty("Authorization", token);
            }

            if (parametros != null && !parametros.isEmpty()) {
                try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
                    byte[] postData = parametros.getBytes(StandardCharsets.UTF_8);
                    dos.write(postData);
                }
            }

            try (var br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                resposta = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    resposta.append(line);
                    resposta.append(System.lineSeparator());
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return resposta.toString();
    }
}
