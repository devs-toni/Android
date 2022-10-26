package com.example.android;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class HttpConnection extends Thread{
    private Context context;
    private String filename;

    public HttpConnection (Context context, String filename){
        this.context = context;
        this.filename = filename;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        String response = null;
        try {
            response = httpPostRequest(context , filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("La respuesta del servidor es: " + response);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String httpPostRequest(Context context,  String audio) throws IOException {
        String url = "http://192.168.0.10:8080/bikers/Mobile";
        File audiofile = new File(audio);

        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        String charset = "UTF-8";
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        
        byte[] bytesAudio = new byte[0];
        FileInputStream inputStream = new FileInputStream(audiofile);
        inputStream.read(bytesAudio);
        byte[] bytes = Files.readAllBytes(Paths.get(audiofile.getAbsolutePath()));
        String response = "";

        try {
            //ABRIR CONEXIÓN
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            //ENVIO
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), charset), true);
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + audiofile.getName() + "\"").append(CRLF);
            writer.append("Content-Length: " + audiofile.length()).append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(audiofile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();

            Files.copy(audiofile.toPath(), connection.getOutputStream());
            connection.getOutputStream().flush();

            //RESPUESTA
            int responseCode = connection.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            response = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //CERRAR CONEXIÓN
            try {
                reader.close();
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
            }
        }
        return response;
    }
}
