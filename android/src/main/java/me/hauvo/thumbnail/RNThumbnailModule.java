
package me.hauvo.thumbnail;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.media.MediaMetadataRetriever;
import 	android.graphics.Matrix;

import java.util.UUID;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;


public class RNThumbnailModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNThumbnailModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNThumbnail";
  }
  @ReactMethod
  public void getSaveDir(String filePath, ReadableMap options, Promise promise) {
   
     
    // saveToDir: '.app_thumbs', // "/storage/emulated/0/.app_thumbs/",
    // default "/storage/emulated/0/thumb/"
    // uniqueNames: false, // default same names

    boolean uniqueNames = false;
    if (options.hasKey("uniqueNames")) {
      uniqueNames = options.getBoolean("uniqueNames");
    }

    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/thumb";
  
    filePath = filePath.replace("file://","");
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(filePath);
    Bitmap image = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

    try {

      File original = new File(filePath);

      if (options.hasKey("saveToDir")) {
        fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + options.getString("saveToDir");
        Log.v(fullPath, "saveToDir");
      
      }

      File dir = new File(fullPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      OutputStream fOut = null;


      String fileName = original.getName() + ".jpeg";
      if (options.hasKey("filename")) {
        fileName = options.getString("filename") + ".jpg";
       
      }
     

      File file = new File(fullPath, fileName);
      file.createNewFile();
      fOut = new FileOutputStream(file);

      // 100 means no compression, the lower you go, the stronger the compression
      image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
      fOut.flush();
      fOut.close();

      // MediaStore.Images.Media.insertImage(reactContext.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

      WritableMap map = Arguments.createMap();
      map.putString("name",fileName);
      map.putString("path", "file://" + fullPath + '/' + fileName);
      map.putDouble("width", image.getWidth());
      map.putDouble("height", image.getHeight());

      promise.resolve(map);

    } catch (Exception e) {
      Log.e("E_RNThumnail_ERROR", e.getMessage());
      promise.reject("E_RNThumnail_ERROR", e);
    }
  }
  


  @ReactMethod
  public void get(String filePath, Promise promise) {
    filePath = filePath.replace("file://","");
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(filePath);
    Bitmap image = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

    String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/thumb";

    try {
      File dir = new File(fullPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      OutputStream fOut = null;
      // String fileName = "thumb-" + UUID.randomUUID().toString() + ".jpeg";
      String fileName = "thumb-" + UUID.randomUUID().toString() + ".jpeg";
      File file = new File(fullPath, fileName);
      file.createNewFile();
      fOut = new FileOutputStream(file);

      // 100 means no compression, the lower you go, the stronger the compression
      image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
      fOut.flush();
      fOut.close();

      // MediaStore.Images.Media.insertImage(reactContext.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

      WritableMap map = Arguments.createMap();

      map.putString("path", "file://" + fullPath + '/' + fileName);
      map.putDouble("width", image.getWidth());
      map.putDouble("height", image.getHeight());

      promise.resolve(map);

    } catch (Exception e) {
      Log.e("E_RNThumnail_ERROR", e.getMessage());
      promise.reject("E_RNThumnail_ERROR", e);
    }
  }
  
}
