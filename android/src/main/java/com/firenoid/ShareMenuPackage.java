package com.firenoid;

import android.app.Activity;

import java.util.*;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import com.firenoid.ShareMenuModule;

public class ShareMenuPackage implements ReactPackage {
  public ShareMenuPackage() {
    super();
  }

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();
    modules.add(new ShareMenuModule(reactContext));
    return modules;
  }

   public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}
