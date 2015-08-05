package com.fashion.krish;

/**
 * Created by amit.thakkar on 7/6/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.fashion.krish.fragment.ProductListFragment;
import com.fashion.krish.model.ProductDetails;
import com.fashion.krish.utility.AppPreferences;
import com.fashion.krish.utility.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestClient {

    DefaultHttpClient client;
    protected String response;
    ArrayList<NameValuePair> headers;
    Activity activity;
    private AppPreferences preferences;
    String url = "http://coupcommerce.magentoprojects.net/jsonconnect/";
    //String url = "http://10.16.16.121/coupcommerce/jsonconnect/";


    final int TIMEOUT = 60;
    public static org.apache.http.client.CookieStore cookieStore;

    public RestClient(Activity activity) {
        // TODO Auto-generated constructor stub
        preferences = new AppPreferences(activity);

        if(cookieStore == null)
        {
            cookieStore = new BasicCookieStore();
            BasicClientCookie cookie = new BasicClientCookie(preferences.getCookieName(), preferences.getCookieValue());
            cookie.setDomain(preferences.getCookieDomain());
            cookieStore.addCookie(cookie);
        }

        client = new DefaultHttpClient();
        client.setCookieStore(cookieStore);

        HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);
        response = "";
        headers = new ArrayList<NameValuePair>();

        this.activity = activity;
    }

    public void addParameters(String headerName, String headerValue) {
        headers.add(new BasicNameValuePair(headerName, headerValue));
    }

    public String performLoginCall(String userName, String password) {

        try {
            HttpPost request = new HttpPost(url+"Authentication/Login");
            addParameters("UserEmail", userName);
            addParameters("Password", password);
            addParameters("Content-Type" , "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Loginresponse", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return showTimeoutError();
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public String resetPasswordCall(String email) {

        try {
            HttpPost request = new HttpPost(url+"/Authentication/ForgottenPassword");/*?Key=1&Value="+email);
*/
            addParameters("Key", "1");
            addParameters("Value", email);
            addParameters("Content-Type" , "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("forgotPassword", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    private String showTimeoutError()
    {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("StatusCode", -1);
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNetworkAvailable(Activity activity, final Utility utility)
    {
        try {

            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            if(!(haveConnectedWifi || haveConnectedMobile))
            {
                if(utility!=null)
                {
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub

                        }
                    });

                }
                //Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                //showErrorDialog("Error",activity.getString(R.string.no_internet), activity);
            }
            return haveConnectedWifi || haveConnectedMobile;

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public String createConnection() {

        try {
            HttpGet request = new HttpGet(url+"configuration/index/app_code/admand3/");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            JSONObject connectionObject = new JSONObject(response);
            preferences.setIsLoggedIn(connectionObject.get("is_loggined").toString());
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Connection Response", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public String login(String userName, String password) {

        try {
            HttpPost request = new HttpPost(url+"customer/login");
            addParameters("username", userName);
            addParameters("password", password);
            addParameters("Content-Type" , "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Login Response", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return showTimeoutError();
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public String register(ArrayList<String> registerParams) {

        try {
            HttpPost request = new HttpPost(url+"customer/save");

            addParameters("email", registerParams.get(2));
            addParameters("password", registerParams.get(3));
            addParameters("confirmation", registerParams.get(4));
            addParameters("firstname", registerParams.get(0));
            addParameters("lastname", registerParams.get(1));
            addParameters("checkout_page_registration", registerParams.get(5));
            addParameters("is_activated", registerParams.get(6));

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Register Response", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return showTimeoutError();
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public String getHomeScreenData() {

        try {
            HttpGet request = new HttpGet(url);

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("HomeScreen Response", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public String getCMSPage(String id){
        String content=null;

        try {
            HttpPost request = new HttpPost(url+"cms/page/");
            addParameters("id", id);
            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("CMS Page id:" + id, response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return showTimeoutError();
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String getProductForCategory(String category_id,String offset,String limit){
        String filterParam = "";
        String sortParam = "";
        if(ProductListFragment.defaultSort.length() != 0){
            sortParam = "order_"+ProductListFragment.defaultSort+"/asc/";
        }
        int i =1;
        for (HashMap.Entry<String,String> entry : ProductListFragment.filterValuesMap.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            if(i==ProductListFragment.filterValuesMap.size() && i !=1 )
                filterParam = filterParam + key +"/"+ value + "/" ;
            else if (i == 1)
                filterParam =  key +"/"+ value + "/";
            else
                filterParam = filterParam + key +"/"+ value +"/" ;

            i++;
        }
        try {
            HttpGet request = new HttpGet(url+"catalog/category/id/"+category_id+"/"+sortParam+
                    filterParam+"offset/"+offset+"/count/"+limit);

            ResponseHandler<String> handler = new BasicResponseHandler();
            Log.d("Product List for Request:", request.getURI().toString());
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product List for Category:" + category_id, response);
            if(new JSONObject(response).has("status")){
                if(new JSONObject(response).get("status").toString().equals("error")){

                    return "error";
                }
            }
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String getFilterDataForCategory(String category_id){

        try {
            HttpGet request = new HttpGet(url+"catalog/filters/category_id/"+category_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product List for Category:"+category_id, response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String getProductDetails(String product_id){

        try {
            HttpGet request = new HttpGet(url+"catalog/product/id/"+product_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product details for Product:"+product_id, response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String getProductGallery(String product_id){

        try {
            HttpGet request = new HttpGet(url+"catalog/productgallery/id/"+product_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product Gallery Details for Product:"+product_id, response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String getProductOptionsConfig(String product_id){

        try {
            HttpGet request = new HttpGet(url+"catalog/productOptionsConfig/id/"+product_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product options config details for Product:"+product_id, response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String getProductReviews(String product_id){

        try {
            HttpGet request = new HttpGet(url+"catalog/productreviews/id/"+product_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product Reviews for Product:"+product_id, response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public String logout() {

        try {
            HttpGet request = new HttpGet(url+"customer/logout");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Logout Response", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public String getCartInfo() {

        try {
            HttpGet request = new HttpGet(url+"cart/info");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Logout Response", response);
            return response;

        } catch (ConnectTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return null;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    private void saveCookies(){
        if(cookieStore!=null)
        {
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                if(url.contains(cookie.getDomain()))
                {
                    preferences.setCookies(cookie.getName(), cookie.getValue(), cookie.getDomain());
                }
            }
        }
    }




}
