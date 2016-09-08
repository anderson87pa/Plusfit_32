package com.pucgo.plusfit.communication;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class CustomRequestHeaders extends Request {

    private Response.Listener listener;
    public String jsonBody;
    public DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(60000, 0, 0f);
    private HashMap<String, String> headers;

    public CustomRequestHeaders(int method, String url,
                                Response.Listener responseListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.listener = responseListener;
        this.jsonBody = "";
        setRetryPolicy(defaultRetryPolicy);
    }

    public CustomRequestHeaders(int method, String url, String jsonBody,
                                Response.Listener responseListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.listener = responseListener;
        this.jsonBody = jsonBody;
        setRetryPolicy(defaultRetryPolicy);
    }

    public CustomRequestHeaders(int method, String url, Response.Listener responseListener, Response.ErrorListener listener, HashMap<String, String> headers) {
        super(method, url, listener);
        this.listener = responseListener;
        this.jsonBody = "";
        setRetryPolicy(defaultRetryPolicy);
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headersPadrao = new HashMap<>();
        if (headers == null) {
//            headersPadrao.put(Urls.APITT_CUSTOMER, Urls.APITT_CUSTOMER_VALUE);
//            headersPadrao.put(Urls.APITT_PRIVATE, Urls.APITT_PRIVATE_VALUE);
//            headersPadrao.put(Urls.APITT_HASH, hash);
//            headersPadrao.put(Constantes.VERSAO_APP, WayTaxi.getAppContext().getString(R.string.nome_da_versao));
        } else {
            return headers;
        }
        return headersPadrao;
    }

    public byte[] getBody() throws AuthFailureError {
        return jsonBody.getBytes();
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            if (networkResponse.data.length > 10000)
                setShouldCache(false);

            String jsonString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            JSONObject jsonObject = new JSONObject(jsonString);

            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Object response) {
        listener.onResponse(response);
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}