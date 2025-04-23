package com.example.orleviprojectjava;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Button btnYes, btnNo;
    private Context context;
    private View contextView;
    private TextView txtTimer;
    private CountDownTimer countDownTimer;

    @SuppressLint("MissingInflatedId")
    public CustomDialog(Context context, View contextView) {
        super(context);

        this.context = context;
        this.contextView = contextView;
        setContentView(R.layout.activity_custom_dialog);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        txtTimer = findViewById(R.id.txtTimer);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);

        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (txtTimer != null) {
                    txtTimer.setText("" + millisUntilFinished / 1000);
                }
            }
            @Override
            public void onFinish() {
                if (isShowing()) {
                    dismiss();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (btnYes == view) {
            ((CreatePictureActivity) context).buttonShareImage(contextView);
            dismiss();
        }
        else if (btnNo == view) {
            ((CreatePictureActivity) context).finish();
        }
    }

    @Override
    public void dismiss() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.dismiss();
    }
}