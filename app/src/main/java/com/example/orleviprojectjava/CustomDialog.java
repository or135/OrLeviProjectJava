package com.example.orleviprojectjava;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Button btnYes, btnNo;
    private Context context;
    private View contextView;

    public CustomDialog(Context context, View contextView) {
        super(context);

        this.context = context;
        this.contextView = contextView;
        setContentView(R.layout.activity_custom_dialog);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (btnYes == view) {
            ((CreatePictureActivity) context).buttonShareImage(contextView);
            super.dismiss();
        } else if (btnNo == view) {
            ((CreatePictureActivity) context).finish();
        }
    }
}