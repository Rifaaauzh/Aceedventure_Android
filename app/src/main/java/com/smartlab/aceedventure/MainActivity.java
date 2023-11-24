package com.smartlab.aceedventure;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {

    private String systemUrl = "https://teacher.aceedventure.com/teacher/index_tc.wp";
    WebView myWebView;
    CookieManager cookieManager;

    WebView noWebView;
    ProgressDialog progressDialog;
    private static ValueCallback<Uri[]> mUploadMessageArr;
    private String notytoken;

    Button newpagebutton;
    private boolean mIsWebViewVisible = false;

    private boolean isConnected = true;
    private static String urlRedirection ="https://teacher.aceedventure.com/teacher/index_tc.wp";
    private String temp;
    private static String cookies;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_NAME = "loginPrefs";
    private static final String KEY_LOGIN_FLAG = "login_flag";
    private static final String KEY_COOKIES = "cookies";
    private boolean loginHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askNotificationPermission();
        // Get the Firebase token and store it as a String variable
        getFirebaseToken(new FirebaseTokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                notytoken = token;
                Log.d("Firebase T", "Firebase token: " + notytoken);
            }
        });

        setContentView(R.layout.activity_main);
        isConnected = checkInternetConnection(this);

        if (isConnected == true) {
            getSupportActionBar().hide();
            progressDialog = new ProgressDialog(MainActivity.this); //replace CONTEXT with YOUR_ACTIVITY_NAME.CLASS`
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading..."); //you can set your custom message here
            progressDialog.show();
            myWebView = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUserAgentString(new WebView(this).getSettings().getUserAgentString());
            webSettings.setLoadWithOverviewMode(false)
            webSettings.setAllowFileAccess(true);
            webSettings.setDomStorageEnabled(true);
            myWebView.setWebViewClient(new WebViewClient()); //we would be overriding WebViewClient() with custom methods
            //CookieManager.getInstance().removeAllCookies(null);
            cookieManager= CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(myWebView, true);
            myWebView.setWebChromeClient(new chromeView()); //we would be overriding WebChromeClient() with custom methods.
            sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
            boolean isLoginHandled = sharedPreferences.getBoolean(KEY_LOGIN_FLAG, false);
            Log.d("MyApp", "isLoginHandled: " + isLoginHandled);

            myWebView.loadUrl(systemUrl);
           /*if (isLoginHandled){
                loginHandled = true;
                cookies = sharedPreferences.getString(KEY_COOKIES, "");
                Log.d("Myapp", "new redirection   " + urlRedirection + cookies);
                myWebView.loadUrl(urlRedirection+ cookies);
               //myWebView.loadUrl(urlRedirection);
              //  performGetRequest();
            }
            else {
               myWebView.loadUrl(systemUrl);
               Log.d("Myapp", "no token   " + systemUrl + cookies);

           }*/

            myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
            myWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    progressDialog.dismiss();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

            });
        } else{
            getSupportActionBar().hide();
            noWebView = (WebView) findViewById(R.id.webview);
            noWebView.getSettings().setJavaScriptEnabled(true);
            noWebView.loadUrl("file:///android_asset/index.html");
        }
    }

    private void openExternalWebView(String url){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("url"));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.android.chrome");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
            // Try with the default browser
            i.setPackage(null);
            startActivity(i);
        }
    }
    public void openInternalWebview(String url){
        //   newpagebutton = (Button) findViewById(R.id.button);
        newpagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent internalIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                internalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myWebView.setWebChromeClient(new WebChromeClient());
                myWebView.setWebViewClient(new WebViewClient());
                myWebView.loadUrl(url);
                ((RelativeLayout) findViewById(R.id.layout)).removeView(newpagebutton);
            }
        });
    }

    class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            Log.d("MyApp",url);
            Log.d("MyApp","kuki" + cookies);
           /*if(url.equals("https://teacher.aceedventure.com/teacher/portal/tc_main.wp")){
                cookieManager = CookieManager.getInstance();
                cookies = cookieManager.getCookie("https://teacher.aceedventure.com/teacher/index_tc.wp");
                //myWebView.loadUrl(urlRedirection+ cookies);
               //myWebView.loadUrl(urlRedirection);
            }
            cookieManager = CookieManager.getInstance();
            cookies = cookieManager.getCookie("https://teacher.aceedventure.com/teacher/index_tc.wp");*/

            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressDialog.show(); //showing the progress bar once the page has started loading
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // hide the progress bar once the page has loaded
            progressDialog.dismiss();
            //cookies = cookieManager.getCookie(systemUrl);
            Log.d("Myapp", "Saved Cookies for " + url + ": " + cookies);
            Log.d("Myapp", "onPageFinished called with URL: " + url);
            //https://teacher.aceedventure.com/teacher/portal/tc_main.wp
           /*if (url.equals(systemUrl)) {
                    CookieSyncManager.createInstance(MainActivity.this);
                    cookieManager = CookieManager.getInstance();
                    cookies = cookieManager.getCookie(systemUrl);
                    cookieManager.setCookie(systemUrl, cookies);
                    //CookieSyncManager.createInstance(MainActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_COOKIES, cookies);
                    editor.putBoolean(KEY_LOGIN_FLAG, true);
                    editor.apply();
                    Log.d("Myapp", "onPageFinished: " + cookies + "saved");
            }*/



        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            myWebView.loadData("","text/html","utf-8"); // replace the default error page with plan content
            progressDialog.dismiss(); // hide the progress bar on error in loading
        }


    }

    public  class chromeView extends WebChromeClient{
        @SuppressLint("NewApi")
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            return MainActivity.this.startFileChooserIntent(valueCallback, fileChooserParams.createIntent());
        }
    }

    @SuppressLint({"NewApi", "RestrictedApi"})
    public boolean startFileChooserIntent(ValueCallback<Uri[]> valueCallback, Intent intent) {
        if (mUploadMessageArr != null) {
            mUploadMessageArr.onReceiveValue(null);
            mUploadMessageArr = null;
        }
        mUploadMessageArr = valueCallback;
        try {
            startActivityForResult(intent, 1001, new Bundle());
            return true;
        } catch (Throwable valueCallback2) {
            valueCallback2.printStackTrace();
            if (mUploadMessageArr != null) {
                mUploadMessageArr.onReceiveValue(null);
                mUploadMessageArr = null;
            }
            return Boolean.parseBoolean(null);
        }
    }
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1001 && Build.VERSION.SDK_INT >= 21) {
            mUploadMessageArr.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
            mUploadMessageArr = null;
        }
    }
    @Override
    public void onBackPressed() {
        if (mIsWebViewVisible) {
            // if the WebView is visible, go back to the previous page if possible
            if (myWebView.canGoBack()) {
                myWebView.goBack();
            } else {
                // if there is no previous page, hide the WebView and reset the flag
                myWebView.setVisibility(View.GONE);
                mIsWebViewVisible = false;
            }
        } else {
            // if the WebView is not visible, show an AlertDialog to confirm exit
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Exit")
                    .setMessage("Are you sure you want to quit?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // User clicked "Yes" button, finish the activity to quit
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)

                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else

        {
            return false;
        }

    }
    private void noInternet() {
        Objects.requireNonNull(getSupportActionBar()).hide();

        WebView myWebView = (WebView) findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/no_internet.html");

    }
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    public void getFirebaseToken(FirebaseTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            Log.d("tokennn:", token);
                            //  Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                            callback.onTokenReceived(token);
                        } else {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        }
                    }
                });
    }
    public interface FirebaseTokenCallback {
        void onTokenReceived(String token);
    }


    public class MyJavascriptInterface {
        Context mContext;
        private CookieManager cookieManager ;
        private String loginInfo = "";

        MyJavascriptInterface(Context c) {
            mContext = c;
            this.cookieManager = cookieManager;
        }

        @JavascriptInterface
        public String getFirebaseToken() {
            return notytoken;
        }

        @JavascriptInterface
        public void invokeExternalChromeWebview(String url) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            browserIntent.setPackage("com.android.chrome");
            try {
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {
                // Chrome is probably not installed
                // Try with the default browser
                browserIntent.setPackage(null);
                startActivity(browserIntent);
            }
        }

        @JavascriptInterface
        public void invokeInternalWebview(String url){
            Intent internalIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            internalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myWebView.setWebChromeClient(new WebChromeClient());
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.loadUrl(url);
        }

        @JavascriptInterface
        public void showToast(String message){
            Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void clearCookies() {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            Log.d("Myapp", "clear : " + cookies);

        }
        @JavascriptInterface
        public void storeLoginInfo(String username, String password) {
            loginInfo = "Username: " + username + ", Password: " + password;
        }


        @JavascriptInterface
        public String getLoginInfo() {
            return loginInfo;
        }


        @JavascriptInterface
        public void openGallery() {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        @JavascriptInterface
        public void executeURL(String url) {
            //  showToast("Redirect url to .. " + url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        @JavascriptInterface
        public void openThisUrl(String url){
            Intent internalIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            internalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myWebView.setWebChromeClient(new WebChromeClient());
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.loadUrl(url);

        }
    }

}