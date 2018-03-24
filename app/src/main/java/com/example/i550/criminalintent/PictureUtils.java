package com.example.i550.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by ETU on 14.03.2018.
 */

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;        //чтение размеров жпг на диске
        BitmapFactory.decodeFile(path,options);
        float scrWidht = options.outWidth;
        float scrHeight = options.outHeight;
        //вычисление К масштабирования
        int inSampleSize = 1;
        if(scrHeight>destHeight || scrWidht>destWidth){
            float hScale = scrHeight/destHeight;
            float wScale = scrWidht/destWidth;
            inSampleSize = Math.round(hScale>wScale?hScale:wScale);
        }
        options=new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        //чтение и создание наконец
        return  BitmapFactory.decodeFile(path,options);
    }
//костыль из книги - проверяет экран и масштабирует жпг до его размера
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return  getScaledBitmap(path, size.x,size.y);
    }
}
