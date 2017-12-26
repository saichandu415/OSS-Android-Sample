package sample.alibabacloud.photosharing.util;

import android.content.Context;
import android.graphics.Typeface;

import sample.alibabacloud.photosharing.R;

/**
 * Created by saisarathchandra on 25/12/17.
 */

public class CustomFont {

    private Context mContext;

    public CustomFont(Context mContext) {
        this.mContext = mContext;
    }

    public Typeface getBungeeRegular(){
        return Typeface.createFromAsset(mContext.getAssets(),mContext.getString(R.string.bungee_regular));
    }

    public Typeface getBreeSerifRegular(){
        return Typeface.createFromAsset(mContext.getAssets(),mContext.getString(R.string.breeserif));
    }
}
