/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.gui;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Class for create WebViews
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class WebViewFactory {
    public static WebView getMathJaxWebView(WebView view) {
        WebSettings settings = view.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);

        view.loadDataWithBaseURL("http://mathjax/",
        "<script src=\"file:///android_asset/mathjax/conf.js\"> </script>"
            //Local MathJax
            + "<script src=\"file:///android_asset/mathjax/tex-chtml.js\"> </script>"
            //Remote CDN MathJax
            //+ "<script src=='https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js'> </script>"
            + "<span id='math'></span><pre><span id='mmlout'></span></pre>", "text/html", "utf-8", "");

        return view;
    }

    public static WebViewClient getMathJaxExpression(final String expression) {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (url.startsWith("http://mathjax")) {
                    view.evaluateJavascript("javascript:document.getElementById('math').innerHTML='"
                            + doubleEscapeTeX(expression) + "';", null);
                    view.evaluateJavascript("javascript:MathJax.startup.promise.then(() => MathJax.typesetPromise());", null);
                }
            }
        };
    }

    private static String doubleEscapeTeX(String s) {
        StringBuilder t= new StringBuilder();
        for (int i=0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t.append('\\');
            if (s.charAt(i) != '\n') t.append(s.charAt(i));
            if (s.charAt(i) == '\\') t.append("\\");
        }
        return t.toString();
    }
}
