/*
*
* WebApp Library for Android
*
* Copyright 2014 Bernat Arlandis i Mañó
*
* This package is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License version 2 as
* published by the Free Software Foundation.
*
* This package is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*/

package com.bernatarlandis.android.webapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.CookieSyncManager;
import android.util.Log;

public abstract class WebAppActivity extends Activity {

    protected SharedPreferences prefs;

    protected WebView webView;

    /** Returns Home URL. */
    protected abstract String getHomeUrl();

    /** Saves current URL in persistent settings. */
    protected void saveUrl(String url) {
        if (url.isEmpty() || !isInternalUrl(url)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("url", url);
        editor.commit();
    }

    /** Restores current URL from persistent settings. */
    protected String restoreUrl() {
        String url = prefs.getString("url", "");
        if (url.isEmpty() || !isInternalUrl(url)) {
            url = getHomeUrl();
        }
        return url;
    }

    /** Returns the User Agent String to be used, null means leave the default. */
    protected String getUserAgentString() {
        return null;
    }

    /** Returns whether the URL belongs to the WebApp. */
    protected boolean isInternalUrl(String url) {
        String urlHost = Uri.parse(url).getHost();
        String ownHost = Uri.parse(getHomeUrl()).getHost();
        return urlHost.equals(ownHost);
    }

    /** Creates WebViewClient. */
    protected WebViewClient newWebViewClient() {
        return new DefaultWebViewClient();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("WebApp", "onCreate: " + savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        prefs = getPreferences(MODE_PRIVATE);
        webView = ((WebView)findViewById(R.id.webView));
        webView.setWebViewClient(newWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        String userAgentString = getUserAgentString();
        if (userAgentString != null) {
            webView.getSettings().setUserAgentString(userAgentString);
        }
        String url;
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
            url = savedInstanceState.getString("url");
        } else {
            url = restoreUrl();
        }
        webView.loadUrl(url);
    }

    /** Called to retrieve per-instance state from an activity before being killed. */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of the WebView
        webView.saveState(outState);
        outState.putString("url", webView.getUrl());
    }

    /** Called when the system is about to start resuming a previous activity. */
    @Override
    protected void onPause() {
        super.onPause();
        saveUrl(webView.getUrl());
        CookieSyncManager.getInstance().sync();
    }

    protected class DefaultWebViewClient extends WebViewClient {

        /** Called before loading a new URL. */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isInternalUrl(url)) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        /** Called when a resource fails to load. */
        @Override
        public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
            view.loadData("", "text/html", "utf-8");
            final WebAppActivity context = WebAppActivity.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.load_error);
            builder.setPositiveButton(R.string.load_retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    view.loadUrl(context.getHomeUrl());
                }
            });
            builder.setNegativeButton(R.string.load_exit, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    context.finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }
}
