package tech.mapleton.catchup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

public class CreateAccount extends AppCompatActivity {
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText passwordConfirm;
    private SignUpHandler signUpHandler;
    private ProgressDialog waitDialog;
    private AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passwordEdit = findViewById(R.id.password);
        passwordConfirm = findViewById(R.id.confirm_password);
        emailEdit = findViewById(R.id.email_address);

        signUpHandler = new SignUpHandler() {
            @Override
            public void onSuccess(final CognitoUser user, final boolean signUpConfirmationState,
                                  final CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Check signUpConfirmationState to see if the user is already confirmed
                closeWaitDialog();
                if (signUpConfirmationState) {
                    // User is already confirmed
                    showDialogMessage("Sign up successful!",emailEdit.getText().toString()+" has been Confirmed", true);
                }
                else {
                    // User is not confirmed
                    confirmSignUp(cognitoUserCodeDeliveryDetails);
                }

            }

            @Override
            public void onFailure(final Exception exception) {
                closeWaitDialog();
                TextView label = findViewById(R.id.message_text);
                label.setText(getText(R.string.signup_failed));
                showDialogMessage("Sign up failed",AppHelper.formatException(exception),false);
            }
        };
    }

    /**
     * Attempt to create a new account
     * @param view the calling view
     */
    public void createAccount(final View view) {
        final String password = passwordEdit.getText().toString();
        final String email = emailEdit.getText().toString();

        if (areInputsValid(email, password)) {
            final CognitoUserAttributes userAttributes = new CognitoUserAttributes();
            userAttributes.addAttribute("e" +
                    "mail", email);

            showWaitDialog("Signing up...");
            AppHelper.getUserPool().signUpInBackground(email, password, userAttributes,
                    null, signUpHandler );
        }
    }

    private boolean areInputsValid(final String email, final String password) {
        final String confirmed = passwordConfirm.getText().toString();

        boolean valid = true;
        if (!password.equals(confirmed)) {
            passwordConfirm.setError(getString(R.string.password_mismatch));
            valid = false;
        }
        if (!EmailParser.isValid(email)) {
            emailEdit.setError(getString(R.string.invalid_email));
            valid = false;
        }

        return valid;
    }

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("source","signup");
        intent.putExtra("name", emailEdit.getText().toString());
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
    }

    private void showWaitDialog(final String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exit) {
                        exit(emailEdit.getText().toString());
                    }
                } catch (Exception e) {
                    if(exit) {
                        exit(emailEdit.getText().toString());
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if(resultCode == RESULT_OK){
                String name = null;
                if(data.hasExtra("name")) {
                    name = data.getStringExtra("name");
                }
                exit(name, passwordEdit.getText().toString());
            }
        }
    }

    private void exit(String uname) {
        exit(uname, null);
    }

    private void exit(final String uname, final String password) {
        final Intent intent = new Intent();
        intent.putExtra("name", uname == null ? "" : uname);
        intent.putExtra("password", password == null ? "" : password);
        setResult(RESULT_OK, intent);
        finish();
    }
}
