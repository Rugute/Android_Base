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
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.dalezak.androidbase.R;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class HttpTask<M extends BaseModel> extends BaseTask<HttpTask, M> {

    protected static final String SLASH = "/";
    protected static final String ETAG = "ETag";
    protected static final String BASIC = "Basic";
    protected static final String UTF_8 = "UTF-8";
    protected static final String ACCEPT = "Accept";
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String IF_NONE_MATCH = "If-None-Match";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String AUTHORIZATION = "Authorization";

    private Map<String, Object> headers = new HashMap<>();
    private Map<String, Object> parameters = new HashMap<>();

    protected URI uri;
    protected String username;
    protected String password;

    protected HttpTask(Context context, URI uri) {
        this(context, uri, R.string.loading_, false);
    }

    protected HttpTask(Context context, URI uri, boolean loading) {
        this(context, uri, R.string.loading_, loading);
    }

    protected HttpTask(Context context, URI uri, int message) {
        this(context, uri, message, false);
    }

    protected HttpTask(Context context, URI uri, int message, boolean loading) {
        super(context, message, loading);
        this.uri = uri;
    }

    protected HttpTask(Context context, String server, String path) {
        this(context, server, path, R.string.loading_, false);
    }

    protected HttpTask(Context context, String server, String path, boolean loading) {
        this(context, server, path, R.string.loading_, loading);
    }

    protected HttpTask(Context context, String server, String path, int message)  {
        this(context, server, path, message, false);
    }

    protected HttpTask(Context context, String server, String path, int message, boolean loading) {
        super(context, message, loading);
        try {
            if (server.endsWith(SLASH) && path.startsWith(SLASH)) {
                this.uri = new URI(server + path.substring(1));
            }
            else if (server.endsWith(SLASH) || path.startsWith(SLASH)) {
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

    protected URI getURI() {
        return uri;
    }

    protected Map<String, Object> getHeaders() {
        return headers;
    }

    protected Map<String, Object> getParameters() {
        return parameters;
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
        if (!Strings.isNullOrEmpty(getUsername()) && !Strings.isNullOrEmpty(getPassword())) {
            AuthScope authScope = new AuthScope(uri.getHost(), AuthScope.ANY_PORT);
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(getUsername(), getPassword());
            credentialsProvider.setCredentials(authScope, credentials);
            httpClient.setCredentialsProvider(credentialsProvider);
        }
        return httpClient;
    }

    protected HttpContext getHttpContext() {
        return new BasicHttpContext();
    }

    protected abstract HttpRequest getHttpRequest(URI uri);

    @Override
    protected Exception doInBackground(Object... params) {
        try {
            onPrepareRequest();
            HttpHost httpHost = getHttpHost(uri);
            HttpClient httpClient = getHttpClient(uri);
            HttpContext httpContext = getHttpContext();
            HttpRequest httpRequest = getHttpRequest(uri);
            httpRequest.setHeader(ACCEPT, APPLICATION_JSON);
            if (!Strings.isNullOrEmpty(getUsername()) && !Strings.isNullOrEmpty(getPassword())) {
                String credentials = String.format("%s:%s", getUsername(), getPassword());
                String credentialsBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                httpRequest.setHeader(AUTHORIZATION, String.format("%s %s", BASIC, credentialsBase64));
            }
            for (String key : getHeaderKeys()) {
                Object value = getHeader(key);
                if (value != null) {
                    httpRequest.setHeader(key, value.toString());
                }
            }
            Log.i(this, "URI %s", httpRequest.getRequestLine().getUri());
            Log.i(this, "Method %s", httpRequest.getRequestLine().getMethod());
            for (Header header : httpRequest.getAllHeaders()) {
                Log.i(this, "Header %s=%s", header.getName(), header.getValue());
            }
            HttpResponse response = httpClient.execute(httpHost, httpRequest, httpContext);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                String reasonPhrase = response.getStatusLine().getReasonPhrase();
                Log.i(this, "Status %d %s", statusCode, reasonPhrase);
                if (statusCode == HttpStatus.SC_OK ||
                    statusCode == HttpStatus.SC_CREATED ||
                    statusCode == HttpStatus.SC_ACCEPTED) {
                    String responseString = EntityUtils.toString(response.getEntity());
                    if (Strings.isNullOrEmpty(responseString)) {
                        Log.i(this, "Response EMPTY");
                        M model = onHandleResponse(new JSONObject());
                        publishProgress(new Update(model, 1, 1));
                    }
                    else {
                        Log.i(this, "Response %s", responseString);
                        Object json = new JSONTokener(responseString).nextValue();
                        if (json == null) {
                            return new NullPointerException("JSON is NULL");
                        }
                        else if (json instanceof JSONObject) {
                            JSONObject jsonObject = (JSONObject)json;
                            M model = onHandleResponse(jsonObject);
                            publishProgress(new Update(model, 1, 1));
                        }
                        else if (json instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray)json;
                            Log.i(this, "JSONArray %s", jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    M model = onHandleResponse(jsonObject);
                                    publishProgress(new Update(model, jsonArray.length(), i + 1));
                                }
                                catch (JSONException e) {
                                    Log.w(this, "URISyntaxException", e);
                                    return e;
                                }
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
                    return new InvalidCredentialsException(getContext().getString(R.string.invalid_credentials));
                }
                else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    if (!Strings.isNullOrEmpty(reasonPhrase)) {
                        return new InvalidCredentialsException(reasonPhrase);
                    }
                    return new InvalidCredentialsException(getContext().getString(R.string.invalid_credentials));
                }
                else if (statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    if (!Strings.isNullOrEmpty(reasonPhrase)) {
                        return new HttpResponseException(statusCode, reasonPhrase);
                    }
                    return new HttpResponseException(statusCode, getContext().getString(R.string.server_maintenance_mode));
                }
                else if (!Strings.isNullOrEmpty(reasonPhrase)) {
                    return new HttpResponseException(statusCode, reasonPhrase);
                }
                else {
                    String responseString = EntityUtils.toString(response.getEntity());
                    if (!Strings.isNullOrEmpty(responseString)) {
                        JSONObject json = new JSONObject(responseString);
                        return new HttpResponseException(statusCode, json.optString("error", reasonPhrase));
                    }
                    else {
                        return new HttpResponseException(statusCode, getContext().getString(R.string.unknown_exception));
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
            return new UnknownHostException(getContext().getString(R.string.unknown_host_exception));
        }
        catch (HttpHostConnectException ex) {
            Log.w(this, "HttpHostConnectException %s", ex.getMessage());
            return new InvalidCredentialsException(getContext().getString(R.string.host_connect_exception));
        }
        catch(ConnectTimeoutException ex) {
            Log.w(this, "ConnectTimeoutException %s", ex.getMessage());
            return new ConnectTimeoutException(getContext().getString(R.string.connection_timeout_exception));
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
        return prefs(getContext()).getAll().size() > 0 && prefs(getContext()).contains(uri.toString());
    }

    protected String getETag(URI uri) {
        return prefs(getContext()).getString(uri.toString(), null);
    }

    protected void setETag(URI uri, String etag) {
        editor(getContext()).putString(uri.toString(), etag).commit();
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