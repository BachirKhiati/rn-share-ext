package com.firenoid;

import com.facebook.react.bridge.Arguments;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

  public void getSharedText(final Callback successCb, final Callback failureCb) {
    Activity mActivity = getCurrentActivity();


    if(mActivity == null) {
      successCb.invoke(false);
      return ; }
    
    Intent intent = mActivity.getIntent();
    String action = intent.getAction();
    String type = intent.getType();




    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        String input = intent.getStringExtra(Intent.EXTRA_TEXT);
            successCb.invoke(input);
      } else if (type.startsWith("image/") || type.startsWith("video/")) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
          String tempPhoto = getPathFromInputStreamUri(mReactContext, imageUri, type);
          Uri photo = Uri.fromFile(new File(tempPhoto));
            WritableMap map = new WritableNativeMap();
            map.putString("uri", photo.toString());
            successCb.invoke(map);

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
              String tempPhoto = getPathFromInputStreamUri(mReactContext, uri, type);
              Uri photo = Uri.fromFile(new File(tempPhoto));
              completeString += photo.toString() + ",";
            }
            successCb.invoke(completeString);
          }
        } else {
          Toast.makeText(mReactContext, "Type is not support", Toast.LENGTH_SHORT).show();
        }
    }else{
      successCb.invoke(false);
    }
  }

  @ReactMethod
  public void clearSharedText() {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }
    Intent intent = mActivity.getIntent();
    if(intent == null) { return; }
    intent.replaceExtras(new Bundle());
    intent.setAction("");
    intent.setData(null);
    intent.setFlags(0);
  }

  private void getImageWidthAndHeight(Uri uri){
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
    int imageHeight = options.outHeight;
    int imageWidth = options.outWidth;

  }

  public static String getPathFromInputStreamUri(Context context, Uri uri, String type) {
    InputStream inputStream = null;
    String filePath = null;

    if (uri.getAuthority() != null) {
      try {
        inputStream = context.getContentResolver().openInputStream(uri);
        File photoFile = createTemporalFileFrom(inputStream, context, type);

        filePath = photoFile.getPath();

      } catch (FileNotFoundException e) {
      //  Logger.printStackTrace(e);
      } catch (IOException e) {
      //  Logger.printStackTrace(e);
      } finally {
        try {
          if (inputStream != null) {
            inputStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return filePath;
  }

  private static File createTemporalFileFrom(InputStream inputStream, Context ctx, String type) throws IOException {
    File targetFile = null;

    if (inputStream != null) {
      int read;
      byte[] buffer = new byte[8 * 1024];
      if (type.startsWith("image/")) {
        targetFile = new File(ctx.getCacheDir(), System.currentTimeMillis()+"tempPicture.jpeg");
      }else if (type.startsWith("video/")) {
        targetFile = new File(ctx.getCacheDir(), System.currentTimeMillis()+"tempvideo.mp4");
      }

      OutputStream outputStream = new FileOutputStream(targetFile);

      while ((read = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, read);
      }
      outputStream.flush();

      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return targetFile;
  }

}
