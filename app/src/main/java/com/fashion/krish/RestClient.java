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
import com.fashion.krish.model.Address;
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
    private String APP_ID = "admand3";

    //String url = "http://vs.magentoprojects.net/index.php/jsonconnect/";
    //private String APP_ID = "adm1111";

    //String url = "http://10.16.16.121/coupcommerce/jsonconnect/";
    //String url = "http://10.16.16.172/coupcommerce/jsonconnect/";

    //String url = "http://sony.magentoprojects.net/index.php/jsonconnect/";
    //private String APP_ID = "adm1111";

    public static String TIMEOUT_ERROR = "timeout_error",ERROR = "error";
    public static String TIMEOUT_ERROR_MESSAGE = "It takes longer time than expected.\n" +
            "Please try again later.";
    public static String ERROR_MESSAGE = "Some error has occurred.\n Please try again later";

    final int TIMEOUT = 10;

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

    private String showTimeoutError(){

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

    public static boolean isNetworkAvailable(Activity activity, final Utility utility){
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
            HttpGet request = new HttpGet(url+"configuration/index/app_code/"+APP_ID+"/device/native/");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            JSONObject connectionObject = new JSONObject(response);
            preferences.setIsLoggedIn(connectionObject.get("logged_in").toString());
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Connection Response", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return ERROR;
        }
    }

    public String login(String userName, String password) {

        try {
            HttpPost request = new HttpPost(url+"customer/login");
            addParameters("email", userName);
            addParameters("password", password);
            addParameters("Content-Type" , "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Login Response", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String loginViaFacebook(String accessToken, String clientId) {

        try {
            HttpPost request = new HttpPost(url+"socialconnect_facebook/connect");
            addParameters("code", accessToken);
            addParameters("clientId", clientId);
            addParameters("Content-Type" , "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("FB Login Response", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String saveAddress(ArrayList<String> addressParams) {

        try {
            HttpPost request = new HttpPost(url+"customer/saveAddress");

            addParameters("firstname", addressParams.get(0));
            addParameters("lastname", addressParams.get(1));
            addParameters("company", addressParams.get(2));
            addParameters("telephone", addressParams.get(3));
            addParameters("street[]", addressParams.get(4));
            addParameters("street[]", addressParams.get(5));
            addParameters("city", addressParams.get(6));
            addParameters("country_id", addressParams.get(7));
            addParameters("region", addressParams.get(8));
            addParameters("postcode", addressParams.get(9));
            addParameters("default_shipping", addressParams.get(10));
            addParameters("fax", addressParams.get(11));

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Save Address Response", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String changeAccountInfo(ArrayList<String> infoParams) {

        try {
            HttpPost request = new HttpPost(url+"customer/edit");


            addParameters("firstname", infoParams.get(1));
            addParameters("lastname", infoParams.get(2));
            addParameters("email", infoParams.get(3));
            addParameters("change_password", infoParams.get(0));
            if(infoParams.get(0).equals("1")){
                addParameters("current_password", infoParams.get(4));
                addParameters("password", infoParams.get(5));
                addParameters("confirmation", infoParams.get(6));
            }

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Account Info Response", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getUserAccountData() {

        try {
            HttpGet request = new HttpGet(url+"customer/dashboard/");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("User Account Response", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getRecentViewedProducts() {

        try {
            HttpGet request = new HttpGet(url+"catalog/recentViewed/");

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Recent Viewed Products", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getStores(String latitude,String longitude) {

        try {
            HttpGet request = new HttpGet(url+"locations/search/latitude/"+latitude+"/longitude/"+longitude+"/");

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Store list", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String searchStores(String queryString) {

        try {
            HttpGet request = new HttpGet(url+"locations/search/querystring/"+queryString+"/");

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Searched Store list", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getCategory(String category_id){
        try {
            HttpGet request = new HttpGet(url+"catalog/category/id/"+category_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            //Log.d("Product List for Request:", request.getURI().toString());
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Sub Cat for Category:" + category_id, response);
            if(new JSONObject(response).has("status")){
                if(new JSONObject(response).get("status").toString().equals("error")){

                    return "error";
                }
            }
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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
            //Log.d("Product List for Request:", request.getURI().toString());
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

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getWishListProduct(String offset,String limit){

        try {
            HttpGet request = new HttpGet(url+"wishlist/index/offset/"+offset+"/count/"+limit);

            ResponseHandler<String> handler = new BasicResponseHandler();
            //Log.d("Product List for Request:", request.getURI().toString());
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            //Log.d("Product List for Category:" + category_id, response);
            if(new JSONObject(response).has("status")){
                if(new JSONObject(response).get("status").toString().equals("error")){

                    return "error";
                }
            }
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getFilterDataForCategory(String category_id){

        try {
            HttpGet request = new HttpGet(url+"catalog/filters/category_id/"+category_id);

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Product List Category:"+category_id, response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getUserAddresses(){

        try {
            HttpGet request = new HttpGet(url+"customer/address");

            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("User Addresses", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getUserOrders() {

        try {
            HttpGet request = new HttpGet(url+"customer/orderList");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Order List Response", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getOrderDetails(String order_id) {

        try {
            HttpGet request = new HttpGet(url+"customer/orderDetails/order_id/"+order_id);

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Order Details", response);
            return response;

        }  catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getCheckoutDetails() {

        try {
            HttpGet request = new HttpGet(url+"checkout");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Checkout Details", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String getShippingMethodList() {

        try {
            HttpGet request = new HttpGet(url+"checkout/shippingMethodsList");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Shipping Methods", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String saveShippingMethod(String shipping_method){


        try {
            HttpPost request = new HttpPost(url+"checkout/saveshippingmethod");
            addParameters("shipping_method", shipping_method);
            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Save shipping:", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String saveShippingAddress(Address address){


        try {
            HttpPost request = new HttpPost(url+"checkout/savebillingaddress");
            addParameters("billing_address_id", address.address_entity_id);
            addParameters("billing[use_for_shipping]", address.is_use_for_shipping);
            addParameters("billing[firstname]", address.address_fname);
            addParameters("billing[lastname]", address.address_lname);
            addParameters("billing[city]", address.address_city);
            addParameters("billing[Country_id]", address.address_country_id);
            addParameters("billing[region]", address.address_region);
            addParameters("billing[postcode]", address.address_zip);
            addParameters("billing[telephone]", address.address_phone);
            addParameters("billing[save_in_address_book]", address.save_in_address_book);

            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Save shipping address:", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getPaymentMethodList() {

        try {
            HttpGet request = new HttpGet(url+"checkout/paymentMethods");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Payment Methods", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String savePayment(HashMap<String,String> payment_params){


        try {
            HttpPost request = new HttpPost(url+"checkout/savePayment");

            for (String key : payment_params.keySet()) {
                addParameters(key, payment_params.get(key));
            }

            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Save Payment:", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getOrderReviews() {

        try {
            HttpGet request = new HttpGet(url+"checkout/orderReview");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Order Review", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String saveOrder(HashMap<String,String> payment_params){


        try {
            HttpPost request = new HttpPost(url+"checkout/saveOrder");

            for (String key : payment_params.keySet()) {
                addParameters(key, payment_params.get(key));
            }

            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Save Order:", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String getCartDetails() {

        try {
            HttpGet request = new HttpGet(url+"cart/index");

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Cart Details", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String updateCart(HashMap<String,String> cart_params){


        try {
            HttpPost request = new HttpPost(url+"cart/update");

            for (String key : cart_params.keySet()) {
                addParameters(key, cart_params.get(key));
            }

            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Update Cart:", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }

    }

    public String deleteCartItems(String item_id) {

        try {
            HttpGet request = new HttpGet(url+"cart/delete/item_id/"+item_id);

            //request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();
            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Delete Item", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
        }
    }

    public String applyCoupon(String couponCode) {

        try {
            HttpPost request = new HttpPost(url+"cart/coupon/");
            addParameters("coupon_code", couponCode);
            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("Coupon code response:" + "", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return "error";
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return "error";
        }
    }

    public String addToCart(String data){
        String content=null;

        try {
            HttpPost request = new HttpPost(url+"cart/add");
            addParameters("product", "373");
            addParameters("Content-Type", "application/json");

            request.setEntity(new UrlEncodedFormEntity(headers));
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = client.execute(request, handler);
            cookieStore=client.getCookieStore();saveCookies();

            client.getConnectionManager().shutdown();
            Log.d("CMS Page id:" + "", response);
            return response;

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return TIMEOUT_ERROR;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return ERROR;
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

        } catch (SocketTimeoutException timeout) {
            timeout.printStackTrace();
            showTimeoutError();
            Log.d("Error", timeout.toString());
            return "error";
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("Error", e.toString());
            return "error";
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
