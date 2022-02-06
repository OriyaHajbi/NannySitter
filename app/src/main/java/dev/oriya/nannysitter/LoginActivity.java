package dev.oriya.nannysitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText login_EDT_Email, login_EDT_Password;
    private FirebaseAuth firebaseAuth;
    private MaterialButton login_BTN_Login,login_BTN_Register ,login_BTN_ForgotPassword ,login_BTN_google;

    //sign in with google
    private static final int RC_SIGN_IN=1;
    private static final String TAG = "GOOGLEAUTH";
    private static final String default_web_client_id = "663889908846-vppevqtje7ljcgfu6lan43i52mnk9p3l.apps.googleusercontent.com";
    private GoogleSignInClient googleSignInClient;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();

        firebaseAuth = FirebaseAuth.getInstance();
        loginFunction();
        registerFunction();
        resetPassword();
        signInWithGoogle();

    }

    private void signInWithGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        initDialog();
        login_BTN_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void initDialog() {
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_google_wait);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                dialog.dismiss();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, ChooseActivity.class));
                            finish();
                            dialog.dismiss();

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                            Tools.myToast(LoginActivity.this , "Login failed");
                            dialog.dismiss();

                        }
                    }
                });
    }

    private void resetPassword(){
        login_BTN_ForgotPassword.setOnClickListener(v -> {
            String emailAddress = login_EDT_Email.getText().toString().trim();
            if (!emailAddress.isEmpty())
                firebaseAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Tools.myToast(LoginActivity.this,"mail for reset sent to: "+emailAddress);
                                }
                            }
                        });
            else
                Tools.myToast(LoginActivity.this,"you need to enter your mail");

        });
    }

    private void registerFunction() {
        login_BTN_Register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class )));
    }

    private void loginFunction() {
        login_BTN_Login.setOnClickListener(v -> {
            String email= login_EDT_Email.getText().toString().trim();
            String password= login_EDT_Password.getText().toString().trim();
            if(email.isEmpty()) {
                login_EDT_Email.setError("Email is empty");
                login_EDT_Email.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                login_EDT_Email.setError("Enter the valid email");
                login_EDT_Email.requestFocus();
                return;
            }
            if(password.isEmpty()) {
                login_EDT_Password.setError("Password is empty");
                login_EDT_Password.requestFocus();
                return;
            }
            if(password.length()<6) {
                login_EDT_Password.setError("Length of password is more than 6");
                login_EDT_Password.requestFocus();
                return;
            }
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                    startActivity(new Intent(LoginActivity.this, ChooseActivity.class));
                else
                    Tools.myToast(this , "Please Check Your login Credentials");

            });
        });
    }

    private void findViews() {
        login_EDT_Email =findViewById(R.id.Login_LBL_Email);
        login_EDT_Password =findViewById(R.id.Login_LBL_Password);
        login_BTN_Login = findViewById(R.id.Login_BTN_Login);
        login_BTN_Register = findViewById(R.id.Login_BTN_Register);
        login_BTN_ForgotPassword = findViewById(R.id.Login_BTN_ForgotPassword);
        login_BTN_google = findViewById(R.id.Login_BTN_google);

    }
    private void clearInputs() {
        login_EDT_Email.setText("");
        login_EDT_Password.setText("");
    }


}