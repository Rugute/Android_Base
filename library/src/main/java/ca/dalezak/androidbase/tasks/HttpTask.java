package ca.dalezak.androidbase.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class HttpTask<M extends BaseModel> extends Task<HttpTask, M> {

    protected static final String ETAG = "ETag";
    protected static final String ACCEPT = "Accept";
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String IF_NONE_MATCH = "If-None-Match";
    protected static final String LINE_SEPARATOR = "line.separator";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String MULTIPART_FORM_DATA = "multipart/form-data";
    protected static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    protected static final String AUTHORIZATION = "Authorization";
    protected static final String BASIC = "Basic";
    protected static final String SLASH = "/";

    private Map<String, Object> headers = new HashMap<>();
    private Map<String, Object> parameters = new HashMap<>();

    protected URI uri;
    protected String username;
    protected String password;

    protected HttpTask(Context context, URI uri) {
        super(context);
        this.uri = uri;
    }

    protected HttpTask(Context context, URI uri, int message) {
        super(context, message);
        this.uri = uri;
    }

    protected HttpTask(Context context, String server, String path) {
        super(context);
        try {
            if (server.endsWith(SLASH) || path.startsWith(SLASH)) {
                this.uri = new URI(server + path);
            }
            else {
                this.uri = new URI(server + SLASH + path);
            }
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpTask(Context context, String server, String path, int message)  {
        super(context, message);
        try {
            if (server.endsWith(SLASH) || path.startsWith(SLASH)) {
                this.uri = new URI(server + path);
            }
            else {
                this.uri = new URI(server + SLASH + path);
            }
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected Context getContext() {
        return context;
    }

    protected void addHeader(String key, Object object) {
        headers.put(key, object);
    }

    protected Set<String> getHeaderKeys() {
        return headers.keySet();
    }

    protected Object getHeader(String key) {
        return headers.get(key);
    }

    protected void addParameter(String key, Object object) {
        parameters.put(key, object);
    }

    protected Set<String> getParameterKeys() {
        return parameters.keySet();
    }

    protected Object getParameter(String key) {
        return parameters.get(key);
    }

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected HttpHost getHttpHost(URI uri) {
        return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
    }

    protected HttpClient getHttpClient(URI uri) {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
        HttpConnectionParams.setSoTimeout(httpParameters, 30000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
            AuthScope authScope = new AuthScope(uri.getHost(), AuthScope.ANY_PORT);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            credentialsProvider.setCredentials(authScope, credentials);
            httpClient.setCredentialsProvider(credentialsProvider);
        }
        return httpClient;
    }

    protected HttpContext getHttpContext() {
        return new BasicHttpContext();
    }

    protected String getResponseString(HttpResponse response) throws IOException {
        InputStream inputStream = response.getEntity().getContent();
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder result = new StringBuilder();
        String line;
        String newLine = System.getProperty(LINE_SEPARATOR);
        while ((line = reader.readLine()) != null) {
            result.append(line);
            result.append(newLine);
        }
        reader.close();
        return result.toString();
    }

    protected abstract HttpRequest getHttpRequest(URI uri);

    @Override
    protected Exception doInBackground(Object... params) {
        try {
            onPrepareRequest();
            Log.i(this, "Request %s", uri);
            HttpHost httpHost = getHttpHost(uri);
            HttpClient httpClient = getHttpClient(uri);
            HttpContext httpContext = getHttpContext();
            HttpRequest httpRequest = getHttpRequest(uri);
            httpRequest.setHeader(ACCEPT, APPLICATION_JSON);
            if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
                Log.i(this, "Username %s Password %s", username, password);
                String credentials = String.format("%s:%s", username, password);
                String credentialsBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                httpRequest.setHeader(AUTHORIZATION, String.format("%s %s", BASIC, credentialsBase64));
            }
            for (String key : getHeaderKeys()) {
                Object value = getHeader(key);
                if (value != null) {
                    httpRequest.setHeader(key, value.toString());
                }
            }
            Log.i(this, "Method %s", httpRequest.getClass().getSimpleName());
            HttpResponse response = httpClient.execute(httpHost, httpRequest, httpContext);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                String reasonPhrase = response.getStatusLine().getReasonPhrase();
                Log.i(this, "Status %d %s", statusCode, reasonPhrase);
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_ACCEPTED) {
                    String responseString = getResponseString(response);
                    Log.i(this, "Response %s", responseString);
                    Object json = new JSONTokener(responseString).nextValue();
                    if (json == null) {
                        return new NullPointerException("JSON is NULL");
                    }
                    else if (json instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject)json;
                        M model = onHandleResponse(jsonObject);
                        if (callback != null) {
                            callback.onTaskProgress(this, model, 1, 1);
                        }
                    }
                    else if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray)json;
                        Log.i(this, "JSONArray %s", jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                M model = onHandleResponse(jsonObject);
                                if (callback != null) {
                                    callback.onTaskProgress(this, model, jsonArray.length(), i + 1);
                                }
                            }
                            catch (JSONException e) {
                                Log.w(this, "URISyntaxException", e);
                                return e;
                            }
                        }
                    }
                    Header etag = response.getFirstHeader(ETAG);
                    if (etag != null) {
                        setETag(uri, etag.getValue());
                    }
                    return null;
                }
                else  if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
                    Header etag = response.getFirstHeader(ETAG);
                    if (etag != null) {
                        Log.i(this, "Not Modified %s", etag.getValue());
                        setETag(uri, etag.getValue());
                    }
                    return null;
                }
                else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                    if (!Strings.isNullOrEmpty(reasonPhrase)) {
                        return new InvalidCredentialsException(reasonPhrase);
                    }
                    return new InvalidCredentialsException(context.getString(R.string.invalid_credentials));
                }
                else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    if (!Strings.isNullOrEmpty(reasonPhrase)) {
                        return new InvalidCredentialsException(reasonPhrase);
                    }
                    return new InvalidCredentialsException(context.getString(R.string.invalid_credentials));
                }
                else if (statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    if (!Strings.isNullOrEmpty(reasonPhrase)) {
                        return new HttpResponseException(statusCode, reasonPhrase);
                    }
                    return new HttpResponseException(statusCode, context.getString(R.string.server_maintenance_mode));
                }
                else if (!Strings.isNullOrEmpty(reasonPhrase)) {
                    return new HttpResponseException(statusCode, reasonPhrase);
                }
                else {
                    String responseString = getResponseString(response);
                    if (!Strings.isNullOrEmpty(responseString)) {
                        JSONObject json = new JSONObject(responseString);
                        return new HttpResponseException(statusCode, json.optString("error", reasonPhrase));
                    }
                    else {
                        return new HttpResponseException(statusCode, context.getString(R.string.unknown_exception));
                    }
                }
            }
            else {
                Log.i(this, "Response NULL");
            }
        }
        catch (JSONException ex) {
            Log.w(this, "JSONException %s", ex.getMessage());
            return ex;
        }
        catch(UnknownHostException ex) {
            Log.w(this, "UnknownHostException %s", ex.getMessage());
            return new UnknownHostException(context.getString(R.string.unknown_host_exception));
        }
        catch (HttpHostConnectException ex) {
            Log.w(this, "HttpHostConnectException %s", ex.getMessage());
            return new InvalidCredentialsException(context.getString(R.string.host_connect_exception));
        }
        catch(ConnectTimeoutException ex) {
            Log.w(this, "ConnectTimeoutException %s", ex.getMessage());
            return new ConnectTimeoutException(context.getString(R.string.connection_timeout_exception));
        }
        catch(IOException ex) {
            Log.w(this, "IOException %s", ex.getMessage());
            return ex;
        }
        return null;
    }

    protected abstract void onPrepareRequest();

    protected abstract M onHandleResponse(JSONObject json) throws JSONException;

    protected static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(HttpTask.class.getName(), Activity.MODE_PRIVATE);
    }

    protected static SharedPreferences.Editor editor(Context context) {
        return context.getSharedPreferences(HttpTask.class.getName(), Activity.MODE_PRIVATE).edit();
    }

    protected boolean hasETag(URI uri) {
        return prefs(context).getAll().size() > 0 && prefs(context).contains(uri.toString());
    }

    protected String getETag(URI uri) {
        return prefs(context).getString(uri.toString(), null);
    }

    protected void setETag(URI uri, String etag) {
        editor(context).putString(uri.toString(), etag).commit();
    }

    public static void clearETags(Context context) {
        Map<String,?> keys = prefs(context).getAll();
        for (Map.Entry<String,?> entry : keys.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("http")) {
                editor(context).remove(key).commit();
            }
        }
    }
}