package es.ugr.swad.swadroid.gui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for Font Awesome icons
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class FontManager {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    private static Map<String, Typeface> TYPEFACE = new HashMap<String, Typeface>();

    public static Typeface getTypeface(Context context, String font) {
        Typeface typeface = TYPEFACE.get(font);

        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), font);
            TYPEFACE.put(font, typeface);
        }

        return typeface;
    }

    public static void markAsIconContainer(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                markAsIconContainer(child, typeface);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        }
    }

}
