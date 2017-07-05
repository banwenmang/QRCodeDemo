package com.shouyiren.qrcodedemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.shouyiren.qrcodedemo.qrcodecore.QRCodeView;
import com.shouyiren.qrcodedemo.zxing.QRCodeDecoder;
import com.shouyiren.qrcodedemo.zxing.ZXingView;

import me.iwf.photopicker.PhotoPicker;

/**
 * 作者：ZhouJianxing on 2017/7/5 09:16
 * email:zhoujianxing0707@gmail.com
 */

public class QRCodeScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = QRCodeScanActivity.class.getSimpleName();
    private QRCodeView mQRCodeView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        ToastUtils.showShort(result);
        vibrate();
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            // 开灯
            case R.id.open_flashlight:
                mQRCodeView.openFlashlight();
                break;

            // 关灯
            case R.id.close_flashlight:
                mQRCodeView.closeFlashlight();
                break;

            // 选取 二维码图片进行扫描
            case R.id.choose_qrcde_from_gallery:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .start(QRCodeScanActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRCodeView.showScanRect();

        if (resultCode == RESULT_OK &&
                requestCode == PhotoPicker.REQUEST_CODE && data != null) {

            final String picturePath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);

             /*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {

                    // 解析图片二维码
                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        ToastUtils.showShort("未发现二维码");
                    } else {
                        ToastUtils.showShort(result);
                    }
                }
            }.execute();
        }
    }
}
