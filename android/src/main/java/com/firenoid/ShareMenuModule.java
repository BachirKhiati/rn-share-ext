package com.firenoid;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.firenoid.ShareMenuPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class ShareMenuModule extends ReactContextBaseJavaModule {

  private ReactContext mReactContext;

  public ShareMenuModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mReactContext = reactContext;
  }

  @Override
  public String getName() {
    return "ReactNativeShareExtension";
  }

  protected void onNewIntent(Intent intent) {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }

    mActivity.setIntent(intent);
  }  

  @ReactMethod
  public void getSharedText(Callback successCallback) {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }
    
    Intent intent = mActivity.getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {

        String input = intent.getStringExtra(Intent.EXTRA_TEXT);
        successCallback.invoke(input);
      } else if (type.startsWith("image/") || type.startsWith("video/")) {
        Integer imageHeight = null, imageWidth = null;
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;
          InputStream in = null;
          try {
            in = mReactContext.getContentResolver().openInputStream(
                    imageUri);
            BitmapFactory.decodeStream(in, null, options);
            imageHeight = options.outHeight;
            imageWidth = options.outWidth;
            WritableMap map = new WritableNativeMap();
            map.putString("uri", imageUri.toString());
            map.putInt("width", imageWidth);
            map.putInt("height", imageHeight);
            successCallback.invoke(map);
          } catch (FileNotFoundException e) {
            successCallback.invoke("null");
            e.printStackTrace();
          }

        }
      } else {
        Toast.makeText(mReactContext, "Type is not support", Toast.LENGTH_SHORT).show();
      }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        if (type.startsWith("image/") || type.startsWith("video/")) {
          ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
          if (imageUris != null) {
            String completeString = new String();
            for (Uri uri: imageUris) {
              completeString += uri.toString() + ",";
            }
            successCallback.invoke(completeString);
          }
        } else {
          Toast.makeText(mReactContext, "Type is not support", Toast.LENGTH_SHORT).show();
        }
    }
  }

  @ReactMethod
  public void clearSharedText() {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }

    Intent intent = mActivity.getIntent();
    String type = intent.getType();
    if ("text/plain".equals(type)) {
      intent.removeExtra(Intent.EXTRA_TEXT);
    } else if (type.startsWith("image/") || type.startsWith("video/")) {
      intent.removeExtra(Intent.EXTRA_STREAM);
    }
  }

  private void getImageWidthAndHeight(Uri uri){
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
    int imageHeight = options.outHeight;
    int imageWidth = options.outWidth;

  }
}
