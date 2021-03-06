/* Copyright © 2019 Mastercard. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 =============================================================================*/

package com.mastercard.commerce;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mastercard.mp.checkout.CheckoutResponseConstants;
import com.mastercard.mp.checkout.MasterpassError;
import com.mastercard.mp.checkout.MasterpassMerchant;
import java.net.URI;
import java.net.URISyntaxException;

import static com.mastercard.commerce.CommerceWebSdk.COMMERCE_STATUS;
import static com.mastercard.commerce.CommerceWebSdk.COMMERCE_TRANSACTION_ID;

/**
 * Activity used to initiate WebView with the SRCi URL. This Activity will handle the callback to
 * the application when SRCi loads {@code callbackUrl}.
 */
public final class WebCheckoutActivity extends AppCompatActivity {
  public static final String CHECKOUT_URL_EXTRA = "CHECKOUT_URL_EXTRA";
  private static final String INTENT_SCHEME = "intent";
  private static final String QUERY_PARAM_MASTERPASS_TRANSACTION_ID = "oauth_token";
  private static final String QUERY_PARAM_MASTERPASS_STATUS = "mpstatus";
  private static final String STATUS_CANCEL = "cancel";
  private static final String STATUS_SUCCESS = "success";
  private static final String TAG = WebCheckoutActivity.class.getSimpleName();
  private ProgressDialog progressdialog;
  private BroadcastReceiver receiver;
  private Snackbar snackBar;
  private WebView srciWebView;
  private WebView dcfWebView;

  @SuppressLint("SetJavaScriptEnabled") @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_view);

    showProgressDialog();

    String url = getIntent().getStringExtra(CHECKOUT_URL_EXTRA);

    Log.d(TAG, "URL to load: " + url);

    WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
    srciWebView = findViewById(R.id.webview);
    srciWebView.getSettings().setJavaScriptEnabled(true);
    srciWebView.getSettings().setDomStorageEnabled(true);
    srciWebView.getSettings().setSupportMultipleWindows(true);
    srciWebView.getSettings().setBuiltInZoomControls(true);
    srciWebView.getSettings().setDisplayZoomControls(false);
    srciWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

    //This webViewClient will override an intent loading action to startActivity
    srciWebView.setWebViewClient(new WebViewClient() {
      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
      }

      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return WebCheckoutActivity.this.shouldOverrideUrlLoading(url);
      }

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        progressdialog.dismiss();
        super.onPageStarted(view, url, favicon);
      }

      @Override
      public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
        if (BuildConfig.DEBUG) {
          handler.proceed();
        } else {
          handler.cancel();
        }
      }
    });

    srciWebView.setWebChromeClient(new WebChromeClient() {

      @SuppressLint("SetJavaScriptEnabled") @Override
      public boolean onCreateWindow(final WebView view, boolean isDialog, boolean isUserGesture,
          Message resultMsg) {
        showProgressDialog();
        WebView.HitTestResult result = view.getHitTestResult();

        if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
          //If the user has selected an anchor link, open in browser
          Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getExtra()));
          startActivity(browserIntent);

          return false;
        }

        dcfWebView = new WebView(WebCheckoutActivity.this);
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        dcfWebView.getSettings().setJavaScriptEnabled(true);
        dcfWebView.getSettings().setSupportZoom(false);
        dcfWebView.getSettings().setBuiltInZoomControls(true);
        dcfWebView.getSettings().setDisplayZoomControls(false);
        dcfWebView.getSettings().setSupportMultipleWindows(true);
        dcfWebView.setLayoutParams(
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          CookieManager.getInstance().setAcceptThirdPartyCookies(dcfWebView, true);
        }
        view.addView(dcfWebView);

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(dcfWebView);
        resultMsg.sendToTarget();

        dcfWebView.setWebViewClient(new WebViewClient() {
          @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return WebCheckoutActivity.this.shouldOverrideUrlLoading(url);
          }

          @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
          }

          @Override public void onPageFinished(WebView view, String url) {
            dcfWebView.setBackgroundColor(Color.WHITE);
            super.onPageFinished(view, url);
            progressdialog.dismiss();
          }

          @Override
          public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
            if (BuildConfig.DEBUG) {
              handler.proceed();
            } else {
              handler.cancel();
            }
          }
        });

        dcfWebView.setWebChromeClient(new WebChromeClient() {

          @Override public void onCloseWindow(WebView window) {
            Log.d(TAG, "onCloseWindow dcf webview --------------------");
            view.removeView(dcfWebView);
            dcfWebView.destroy();
          }

          public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
              Message resultMsg) {

            WebView.HitTestResult result = view.getHitTestResult();

            if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
              //If the user has selected an anchor link, open in browser
              Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getExtra()));
              startActivity(browserIntent);

              return false;
            }
            return true;
          }


        });

        return true;
      }

      @Override public void onCloseWindow(WebView window) {
        Log.d(TAG, "Closing window... maybe?");
      }
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      CookieManager.getInstance().setAcceptThirdPartyCookies(srciWebView, true);
    }
    srciWebView.resumeTimers();
    srciWebView.loadUrl(url);
    receiver = getReceiver();
  }

  @Override protected void onStart() {
    super.onStart();
    IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(receiver, filter);
  }

  @Override protected void onStop() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      CookieManager.getInstance().flush();
    } else {
      CookieSyncManager.getInstance().sync();
    }
    unregisterReceiver(receiver);
    super.onStop();
  }

  @Override protected void onDestroy() {
    destroySrciDCFWebviews();
    super.onDestroy();
  }

  private boolean shouldOverrideUrlLoading(String url) {
    String urlScheme = URI.create(url).getScheme();

    Log.d(TAG, "Navigating to: " + url);

    if (urlScheme.equals(INTENT_SCHEME)) {
      handleIntent(url);

      return true;
    } else {

      return false;
    }
  }

  private void handleIntent(String intentUriString) {
    try {
      Intent intent = Intent.parseUri(intentUriString, Intent.URI_INTENT_SCHEME);
      String intentApplicationPackage = intent.getPackage();
      String currentApplicationPackage = getApplication().getApplicationInfo().packageName;
      Uri intentUri = Uri.parse(intentUriString);
      String transactionId = intentUri.getQueryParameter(QUERY_PARAM_MASTERPASS_TRANSACTION_ID);
      String status = intentUri.getQueryParameter(QUERY_PARAM_MASTERPASS_STATUS);

      if (MasterpassMerchant.getCheckoutCallback() != null) {
        //We're in a v7 flow
        handleMasterpassCallback(status, transactionId);
      } else if (STATUS_CANCEL.equals(status)) {
        finish();
      } else if (intentApplicationPackage != null &&
          intentApplicationPackage.equals(currentApplicationPackage)) {
        intent.putExtra(COMMERCE_TRANSACTION_ID, transactionId);
        intent.putExtra(COMMERCE_STATUS, status);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
      } else {
        throw new IllegalStateException(
            "The Intent is not valid for this application: " + intentUriString);
      }
    } catch (URISyntaxException e) {
      Log.e(TAG,
          "Unable to parse Intent URI. You must provide a valid Intent URI or checkout will never work.",
          e);

      throw new IllegalStateException(e);
    }
  }

  private void handleMasterpassCallback(String status, String transactionId) {
    Bundle responseBundle = new Bundle();

    if (STATUS_SUCCESS.equals(status) && transactionId != null) {
      responseBundle.putString(CheckoutResponseConstants.TRANSACTION_ID, transactionId);

      MasterpassMerchant.getCheckoutCallback().onCheckoutComplete(responseBundle);
    } else if (MasterpassMerchant.isMerchantInitiated() && STATUS_CANCEL.equals(status)) {
      MasterpassMerchant.getCheckoutCallback().onCheckoutError(new MasterpassError(
          MasterpassError.ERROR_CODE_CANCEL_WALLET, "User selected cancel wallet"));
    }

    finish();
  }

  private void showProgressDialog() {
    progressdialog = new ProgressDialog(this);
    progressdialog.setMessage(getResources().getString(R.string.loading_web_view));
    progressdialog.setCancelable(true);
    progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressdialog.show();
  }

  private void destroySrciDCFWebviews(){
    // Make sure you remove the WebView from its parent view before doing anything.
    ViewGroup webviewContainer = findViewById(R.id.webview_container);
    webviewContainer.removeAllViews();

    destroyWebView(srciWebView);
    destroyWebView(dcfWebView);
  }

  private void destroyWebView(WebView mWebView) {

    if (mWebView == null) {
      return;
    }

    mWebView.clearHistory();

    // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
    // Probably not a great idea to pass true if you have other WebViews still alive.
    mWebView.clearCache(true);

    // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
    mWebView.loadUrl("about:blank");

    mWebView.onPause();
    mWebView.removeAllViews();
    mWebView.destroyDrawingCache();

    // NOTE: This pauses JavaScript execution for ALL WebViews,
    // do not use if you have other WebViews still alive.
    // If you create another WebView after calling this,
    // make sure to call mWebView.resumeTimers().
    mWebView.pauseTimers();

    // NOTE: This can occasionally cause a segfault below API 17 (4.2)
    mWebView.destroy();

    // Null out the reference so that you don't end up re-using it.
    mWebView = null;
  }

  private BroadcastReceiver getReceiver() {
    return new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {

        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null
            && networkInfo.isConnected()) {
          if (snackBar != null) {
            snackBar.dismiss();
          }
        } else {
          snackBar = Snackbar.make(srciWebView, getString(R.string.error_dialog_connectivity_title),
              Snackbar.LENGTH_INDEFINITE);
          View snackBarView = snackBar.getView();
          TextView snackBarText =
              snackBarView.findViewById(android.support.design.R.id.snackbar_text);
          snackBarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
          snackBarView.setBackgroundColor(
              ContextCompat.getColor(WebCheckoutActivity.this, R.color.color_snackbar_error));
          snackBar.show();
        }
      }
    };
  }
}

