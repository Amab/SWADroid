package es.ugr.swad.swadroid.gui;

import android.os.Build;
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
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);

        view.loadDataWithBaseURL("http://bar/", "<script type='text/x-mathjax-config'>"
                + "MathJax.Hub.Config({ "
                + "showMathMenu: false, "
                + "jax: ['input/TeX','output/HTML-CSS'], "
                + "extensions: ['tex2jax.js','toMathML.js'], "
                + "TeX: { extensions: ['AMSmath.js','AMSsymbols.js',"
                + "'noErrors.js','noUndefined.js'] }, "
                + "tex2jax: {"
                + "      inlineMath: [ ['$','$'], [\"\\\\(\",\"\\\\)\"] ],"
                + "      processEscapes: true"
                + "    }"
                + "});</script>"
                + "<script type='text/javascript' "
                //+ "src='file:///android_asset/MathJax/MathJax.js'"                              //Local MathJax
                + "src='https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS_HTML'"   //Remote CDN MathJax
                + "></script>"
                + "<script type='text/javascript'>getLiteralMML = function() {"
                + "math=MathJax.Hub.getAllJax('math')[0];"
                // below, toMathML() reruns literal MathML string
                + "mml=math.root.toMathML(''); return mml;"
                + "}; getEscapedMML = function() {"
                + "math=MathJax.Hub.getAllJax('math')[0];"
                // below, toMathMLquote() applies &-escaping to MathML string input
                + "mml=math.root.toMathMLquote(getLiteralMML()); return mml;}"
                + "</script>"
                + "<span id='math'></span><pre><span id='mmlout'></span></pre>", "text/html", "utf-8", "");

        return view;
    }

    public static WebViewClient getMathJaxExpression(final String expression) {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.startsWith("http://bar"))
                    return;

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    view.loadUrl("javascript:document.getElementById('math').innerHTML='"
                            + doubleEscapeTeX(expression) + "';");
                    view.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
                } else {
                    view.evaluateJavascript("javascript:document.getElementById('math').innerHTML='"
                            + doubleEscapeTeX(expression) + "';", null);
                    view.evaluateJavascript("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);", null);
                }
            }
        };
    }

    private static String doubleEscapeTeX(String s) {
        String t="";
        for (int i=0; i < s.length(); i++) {
            if (s.charAt(i) == '\'') t += '\\';
            if (s.charAt(i) != '\n') t += s.charAt(i);
            if (s.charAt(i) == '\\') t += "\\";
        }
        return t;
    }
}
