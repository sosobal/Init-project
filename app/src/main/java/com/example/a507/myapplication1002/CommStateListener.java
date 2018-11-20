package com.example.a507.myapplication1002;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class CommStateListener extends PhoneStateListener {
    public CommStateListener() {
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
    }
}
