package com.reactnative.openalpr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.openalpr.OpenALPR;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OpenALPRModule extends ReactContextBaseJavaModule {

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";
    private static final String TAKE_PHOTO_ERROR_MSG = "Can't take the photo";
    private static final int REQUEST_TAKE_PHOTO = 1;

    private String mAndroidDataDir;
    private OpenALPR mOpenALPRInstance;
    private String mOpenAlprConfFile;
    private File mCurrentPhoto;
    private Callback mSuccessCallback;
    private Callback mErorCallback;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_TAKE_PHOTO) {

                if (resultCode == Activity.RESULT_OK) {
                    String result = mOpenALPRInstance.recognizeWithCountryRegionNConfig("eu", "", mCurrentPhoto.getAbsolutePath(), mOpenAlprConfFile, 10);
                    Log.d("openalpr", mAndroidDataDir);
                    Log.d("openalpr", result);
                    mSuccessCallback.invoke(result);
                } else {
                    mErorCallback.invoke(TAKE_PHOTO_ERROR_MSG);
                }

            }
        }
    };

    private void dispatchTakePictureIntent() {

        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) return;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri file = Uri.fromFile(getOutputMediaFile());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        currentActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "OpenALPR");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mCurrentPhoto = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        return mCurrentPhoto;
    }

    public OpenALPRModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(mActivityEventListener);
        mAndroidDataDir = reactContext.getApplicationInfo().dataDir;
        mOpenAlprConfFile = mAndroidDataDir + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
        mOpenALPRInstance = OpenALPR.Factory.create(reactContext, mAndroidDataDir);
    }

    @Override
    public String getName() {
        return "OpenALPR";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }

    @ReactMethod
    public void start(Callback success, Callback error) {
        mSuccessCallback = success;
        mErorCallback = error;
        dispatchTakePictureIntent();
    }
}