package com.lazycoder.remotemysqlconnect.helper;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class HttpJsonParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    HttpURLConnection urlConnection = null;

    //function get json from url
    //by making HTTP POST AND GET   method
    public JSONObject makeHttpRequest(String url, String method, Map<String,String>params){
        try {
            Uri.Builder builder = new Uri.Builder();
            URL urlObj;
            String encodeParams = "";
            if (params !=null){
                for (Map.Entry<String,String>entry:params.entrySet()){
                    builder.appendQueryParameter(entry.getKey(),entry.getValue());
                }

            }
            if (builder.build().getEncodedQuery()!= null){
                encodeParams = builder.build().getEncodedQuery();
            }
            if ("GET".equals(method)){
                url = url+"?"+encodeParams;
                urlObj = new URL(url);
                urlConnection = (HttpURLConnection)urlObj.openConnection();
                urlConnection.setRequestMethod(method);
            }else
            {
                urlObj = new URL(url);
                urlConnection = (HttpURLConnection)urlObj.openConnection();
                urlConnection.setRequestMethod(method);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(encodeParams.getBytes().length));
                urlConnection.getOutputStream().write(encodeParams.getBytes());
            }
            urlConnection.connect();
            is = urlConnection.getInputStream();
            BufferedReader reader= new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            json = sb.toString();
            jObj = new JSONObject(json);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            Log.e("JSON parser ","Error parsing data"+e.toString());
        }catch (Exception e){
            Log.e("Exception ","Error parsing data"+e.toString());

        }

        //JSon string return
        return jObj;
    }
}
