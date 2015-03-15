package es.ugr.swad.swadroid;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class PasswordDialogPreference extends DialogPreference {

    private EditText mEditTextPassword;

    public PasswordDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.dialog_password);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE) {
            String value = mEditTextPassword.getText().toString();
            callChangeListener(value);
        }
        super.onClick(dialog, which);
    }

    @Override
    protected void onBindDialogView(View view) {

        mEditTextPassword = (EditText) view.findViewById(R.id.etpPassword);

        super.onBindDialogView(view);
    }
}
