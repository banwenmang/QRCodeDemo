package com.shouyiren.qrcodedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.shouyiren.qrcodedemo.qrcodecore.BGAQRCodeUtil;
import com.shouyiren.qrcodedemo.zxing.QRCodeEncoder;

import me.iwf.photopicker.PhotoPicker;

/**
 * 作者：ZhouJianxing on 2017/7/5 11:33
 * email:zhoujianxing0707@gmail.com
 */

public class QRCodeGenerateActivityTwo extends AppCompatActivity {
    private ImageView mIvLogoPre;
    private EditText mEtWordPre;
    private ImageView mIvLogoQr;
    private ImageView mIvWordQr;
    private String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_generate_two);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        initView();
    }

    private void initView() {
        mIvLogoPre = (ImageView) findViewById(R.id.iv_show_pre);
        mIvLogoQr = (ImageView) findViewById(R.id.iv_show_qr);
        mEtWordPre = (EditText) findViewById(R.id.et_word_pre);
        mIvWordQr = (ImageView) findViewById(R.id.iv_word_qrcode);
    }

    public void chooseLogo(View v) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .start(QRCodeGenerateActivityTwo.this);
    }

    public void saveLogoQr(View v) {
        mIvLogoQr.setDrawingCacheEnabled(true);
        String logoQrPath = SDCardUtils.getSDCardPath() + "/qrcode/logo/" + mContent;
        ImageUtils.save(mIvLogoQr.getDrawingCache(), logoQrPath, Bitmap.CompressFormat.PNG, false);
        //ToastUtils.showShort(save ? "保存二维码成功" : "保存二维码失败");

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + logoQrPath)));
    }

    public void createWordQr(View v) {
        String content = mEtWordPre.getText().toString();
        createChineseQRCode(TextUtils.isEmpty(content) ? "周建星" : content);
    }

    public void saveWordQr(View v) {
        mIvWordQr.setDrawingCacheEnabled(true);
        String wordQrPath = SDCardUtils.getSDCardPath() + "/qrcode/word/" + mContent;
        ImageUtils.save(mIvWordQr.getDrawingCache(), wordQrPath, Bitmap.CompressFormat.PNG, false);
        //ToastUtils.showShort(save ? "保存二维码成功" : "保存二维码失败");

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + wordQrPath)));
    }

    private void createChineseQRCode(final String content) {
        mContent = content;
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(content, BGAQRCodeUtil.dp2px(QRCodeGenerateActivityTwo.this, 150));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mIvWordQr.setImageBitmap(bitmap);
                } else {
                    ToastUtils.showShort("生成中文二维码失败");
                }
            }
        }.execute();
    }

    private void createChineseQRCodeWithLogo(final String content, final Bitmap bitmap) {
        mContent = content;
        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return QRCodeEncoder.syncEncodeQRCode(content, BGAQRCodeUtil.dp2px(QRCodeGenerateActivityTwo.this, 150), Color.parseColor("#ff0000"), bitmap);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mIvLogoQr.setImageBitmap(bitmap);
                } else {
                    ToastUtils.showShort("生成带logo的中文二维码失败");
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                requestCode == PhotoPicker.REQUEST_CODE && data != null) {

            final String picturePath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
            Glide.with(getApplicationContext())
                    .load(picturePath)
                    .into(mIvLogoPre);

            String content = mEtWordPre.getText().toString();
            mIvLogoPre.setDrawingCacheEnabled(true);
            Bitmap bitmap = mIvLogoPre.getDrawingCache();

            createChineseQRCodeWithLogo(TextUtils.isEmpty(content) ? "周建星" : content, bitmap);
        }
    }
}
