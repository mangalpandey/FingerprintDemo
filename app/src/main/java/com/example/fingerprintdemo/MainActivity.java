package com.example.fingerprintdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements FingerprintHandler.AuthenticateCallBack{



    private ImageView ivFingerprint;
    private TextView tvMessage;
    private CipherKeyGenerator cipherKeyGenerator;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessage =  findViewById(R.id.tvMessage);
        ivFingerprint = findViewById(R.id.ivFingerprint);
        cipherKeyGenerator = new CipherKeyGenerator();
        authenticate();

    }


    @Override
    public void onReceive(int messageId, CharSequence message) {
        switch (messageId){
            case Constant.ERROR:
                ivFingerprint.setImageResource(R.drawable.ic_info_outline);
                break;
            case Constant.FAILURE:
                ivFingerprint.setImageResource(R.drawable.ic_info_outline);
                break;
            case Constant.HELP:
                ivFingerprint.setImageResource(R.drawable.ic_fingerprint_black_24dp);
                break;
            case Constant.SUCCESS:
                ivFingerprint.setImageResource(R.drawable.ic_fingerprint_green_24dp);
                openSuccessActivity();
                break;

        }
        tvMessage.setText(message);

    }
    private void openSuccessActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, SuccessActivity.class));
            }
        }, 400);

    }

    private void authenticate(){
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        assert fingerprintManager != null;
        if (fingerprintManager.isHardwareDetected()) {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                tvMessage.setText(getString(R.string.permission_not_enable));
            }else{
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints())
                    tvMessage.setText(getString(R.string.register_at_least_one));
                else{
                    // Checks whether lock screen security is enabled or not
                    assert keyguardManager != null;
                    if (Objects.requireNonNull(keyguardManager).isKeyguardSecure()) {
                        cipherKeyGenerator.generateKeyStore();
                        if (cipherKeyGenerator.cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipherKeyGenerator.getCipher());
                            FingerprintHandler helper = new FingerprintHandler(this, this);
                            helper.startAuthenticate(fingerprintManager, cryptoObject);
                        }
                    } else {
                        tvMessage.setText(getString(R.string.security_not_enable));
                    }
                }
            }
        } else {
            tvMessage.setText(getString(R.string.device_does_not_have));
        }
    }
}