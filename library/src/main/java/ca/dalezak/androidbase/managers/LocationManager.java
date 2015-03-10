package ca.dalezak.androidbase.managers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ca.dalezak.androidbase.utils.Log;

public class LocationManager implements LocationListener {

    public interface Callback {
        public void onLocationChanged(double latitude, double longitude);
        public void onAddressChanged(String city, String state, String country);
    }

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 50; // 50 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; // 2 minutes
    private static final LocationManager instance = new LocationManager();
    private static final LinkedList<Callback> callbacks = new LinkedList<Callback>();
    private static Context context;
    private static Location location;
    private static Address address;
    private static boolean isGPSEnabled = false;
    private static boolean isNetworkEnabled = false;
    private static android.location.LocationManager locationManager;
    private static android.location.LocationManager getLocationManager() {
        if (locationManager == null) {
            locationManager = (android.location.LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    public static void initialize(Context ctx) {
        context = ctx;
    }

    public static void subscribe(Callback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
        try {
            isGPSEnabled = getLocationManager().isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
            isNetworkEnabled = getLocationManager().isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                getLocationManager().requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, instance);
                location = getLocationManager().getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
                Log.i(instance, "NETWORK_PROVIDER %s", "Enabled");
            }
            if (isGPSEnabled && location == null) {
                getLocationManager().requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, instance);
                location = getLocationManager().getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
                Log.i(instance, "GPS_PROVIDER %s", "Enabled");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        instance.onLocationChanged(location);
    }

    public static void unsubscribe(Callback callback) {
        callbacks.remove(callback);
        if (callbacks.size() == 0) {
            locationManager.removeUpdates(instance);
        }
    }

    public static double getLatitude() {
        return location != null ? location.getLatitude() : 0.0;
    }

    public static double getLongitude() {
        return location != null ? location.getLongitude() : 0.0;
    }

    public static String getCity() {
        return address != null ? address.getLocality() : null;
    }

    public static String getState() {
        return address != null ? address.getAdminArea() : null;
    }

    public static String getCountry() {
        return address != null ? address.getCountryName() : null;
    }

    public static Boolean isGPSEnabled() {
        return isGPSEnabled;
    }

    public static Boolean isNetworkEnabled() {
        return isNetworkEnabled;
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationManager.location = location;
        if (location != null) {
            Log.i(LocationManager.class, "onLocationChanged %s", location.toString());
            for (Callback callback : callbacks) {
                callback.onLocationChanged(location.getLatitude(), location.getLongitude());
            }
            new AddressTask(context).execute(location);
            locationManager.removeUpdates(this);
        }
        else {
            Log.i(LocationManager.class, "onLocationChanged NULL");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(this, "onStatusChanged %s", provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(this, "onProviderEnabled %s", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(this, "onProviderDisabled %s", provider);
    }

    class AddressTask extends AsyncTask<Location, Void, Address> {
        private Context context;

        public AddressTask(Context context) {
            this.context = context;
        }

        @Override
        protected Address doInBackground(Location... locations) {
            if (locations != null && locations.length > 0) {
                Log.i(this, "doInBackground %s", locations.toString());
                Location location = locations[0];
                if (Geocoder.isPresent()) {
                    return getAddressViaGeocoder(location.getLatitude(), location.getLongitude());
                }
                else {
                    return getAddressViaGoogleApi(location.getLatitude(), location.getLongitude());
                }
            }
            else {
                Log.i(this, "doInBackground NULL");
            }
            return null;
        }

        protected void onPostExecute(Address address) {
            LocationManager.address = address;
            if (address != null) {
                Log.i(this, "onPostExecute %s", address.toString());
                for (Callback callback : callbacks) {
                    callback.onAddressChanged(address.getLocality(), address.getAdminArea(), address.getCountryName());
                }
            }
            else {
                Log.i(this, "onPostExecute NULL");
            }
        }

        private Address getAddressViaGeocoder(double latitude, double longitude) {
            Log.i(this, "getAddressViaGeocoder %f, %f", latitude, longitude);
            Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
            Log.i(this, "Geocoder %s", geocoder.toString());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    Log.i(this, "Addresses %s", addresses.toString());
                    return addresses.get(0);
                }
                else {
                    Log.i(this, "Addresses NULL");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Address getAddressViaGoogleApi(double latitude, double longitude)  {
            Log.i(this, "getAddressViaGoogleApi %f, %f", latitude, longitude);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false", latitude, longitude);
                Log.i(this, "URL %s", url);
                HttpGet httpGet = new HttpGet(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = httpclient.execute(httpGet, responseHandler);
                JSONObject json = new JSONObject(response);
                if ("OK".equalsIgnoreCase(json.getString("status"))) {
                    JSONArray results = json.getJSONArray("results");
                    Address address = new Address(Locale.getDefault());
                    JSONObject result = results.getJSONObject(0);
                    JSONArray components = result.getJSONArray("address_components");
                    Log.i(this, components.toString());
                    for (int i = 0; i < components.length(); i++) {
                        JSONObject component = components.getJSONObject(i);
                        String type = component.getJSONArray("types").getString(0);
                        if (type.equals("street_number")) {
                            address.setAddressLine(0, component.getString("long_name"));
                        }
                        else if (type.equals("route")) {
                            address.setAddressLine(1, component.getString("long_name"));
                        }
                        else if (type.equals("locality")) {
                            address.setLocality(component.getString("long_name"));
                        }
                        else if (type.equals("postal_code")) {
                            address.setPostalCode(component.getString("long_name"));
                        }
                        else if (type.equals("administrative_area_level_1")) {
                            address.setAdminArea(component.getString("long_name"));
                        }
                        else if (type.equals("administrative_area_level_2")) {
                            address.setSubAdminArea(component.getString("long_name"));
                        }
                        else if (type.equals("country")) {
                            address.setCountryCode(component.getString("short_name"));
                            address.setCountryName(component.getString("long_name"));
                        }
                    }
                    return address;
                }
            }
            catch (ClientProtocolException e) {
                Log.e(this, "ClientProtocolException", e);
            }
            catch (JSONException e) {
                Log.e(this, "JSONException", e);
            }
            catch (IOException e) {
                Log.e(this, "IOException", e);
            }
            finally {
                if (httpclient.getConnectionManager() != null) {
                    httpclient.getConnectionManager().shutdown();
                }
            }
            return null;
        }

    }
}
