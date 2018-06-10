package com.example.fingerprintdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;


public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;
    private AuthenticateCallBack authenticateCallBack;


    FingerprintHandler(Context mContext, AuthenticateCallBack authenticateCallBack) {
        this.context = mContext;
        this.authenticateCallBack = authenticateCallBack;

    }

    public void startAuthenticate(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        authenticateCallBack.onReceive(Constant.ERROR, "Fingerprint Authentication error\n" + errString);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        authenticateCallBack.onReceive(Constant.HELP, "Fingerprint Authentication help\n" + helpString);
    }


    @Override
    public void onAuthenticationFailed() {
        authenticateCallBack.onReceive(Constant.FAILURE, "Fingerprint Authentication failed");
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        authenticateCallBack.onReceive(Constant.SUCCESS, "Fingerprint Authentication succeeded");
    }

    public interface AuthenticateCallBack{
        void onReceive(int messageId, CharSequence message);
    }
    
}
