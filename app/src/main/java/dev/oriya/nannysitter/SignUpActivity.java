package dev.oriya.nannysitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private MaterialButton signUp_BTN_Sign_Up, signUp_BTN_Sign_In;
    private EditText signUp_LBL_Email, signUp_LBL_Password, signUp_LBL_FirstName, signUp_LBL_LastName, signUp_LBL_Password2;
    private FirebaseAuth mAuth;
    private final int MIN_PASSWORD_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViews();

        mAuth=FirebaseAuth.getInstance();
        signUpFunction();
        signInFunction();


    }

    private void signInFunction() {
        signUp_BTN_Sign_In.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this , LoginActivity.class));
                finish();
            }
        });

    }

    private void signUpFunction() {
        signUp_BTN_Sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signUp_LBL_Email.getText().toString().trim();
                String password= signUp_LBL_Password.getText().toString().trim();
                String firstName= signUp_LBL_Password.getText().toString().trim();
                String lastName= signUp_LBL_Password.getText().toString().trim();
                if(firstName.isEmpty()) {
                    signUp_LBL_FirstName.setError("firstName is empty");
                    signUp_LBL_FirstName.requestFocus();
                    return;
                }
                if(lastName.isEmpty()) {
                    signUp_LBL_LastName.setError("lastName is empty");
                    signUp_LBL_LastName.requestFocus();
                    return;
                }
                if(email.isEmpty()) {
                    signUp_LBL_Email.setError("Email is empty");
                    signUp_LBL_Email.requestFocus();
                    return;
                }
                // checking the proper email format
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signUp_LBL_Email.setError("Enter the valid email address");
                    signUp_LBL_Email.requestFocus();
                    return;
                }
                if(password.isEmpty()) {
                    signUp_LBL_Password.setError("Enter the password");
                    signUp_LBL_Password.requestFocus();
                    return;
                }
                // checking minimum password Length
                if(password.length() < MIN_PASSWORD_LENGTH) {
                    signUp_LBL_Password.setError("Length of the password should be more than 6");
                    signUp_LBL_Password.requestFocus();
                    resetPasswords();
                    return;
                }
                // Checking if repeat password is same
                if (!signUp_LBL_Password.getText().toString().equals(signUp_LBL_Password2.getText().toString())) {
                    signUp_LBL_Password2.setError("Password does not match");
                    resetPasswords();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Tools.myToast(SignUpActivity.this , "You are successfully Registered");
                            startActivity(new Intent(SignUpActivity.this , ProfileActivity.class));
                            finish();
                        }
                        else{
                            startActivity(new Intent(SignUpActivity.this, ProfileActivity.class));
                            finish();
                            Tools.myToast(SignUpActivity.this , "You are not Registered! Try again");
                        }
                    }
                });

            }
        });
    }

    private void resetPasswords() {
        signUp_LBL_Password.setText("");
        signUp_LBL_Password2.setText("");
    }

    private void findViews() {
        signUp_LBL_Email =findViewById(R.id.SignUp_LBL_Email);
        signUp_LBL_FirstName =findViewById(R.id.SignUp_LBL_FirstName);
        signUp_LBL_LastName =findViewById(R.id.SignUp_LBL_LastName);
        signUp_LBL_Password =findViewById(R.id.SignUp_LBL_Password);
        signUp_LBL_Password2 =findViewById(R.id.SignUp_LBL_Password2);
        signUp_BTN_Sign_Up =findViewById(R.id.SignUp_BTN_Sign_Up);
        signUp_BTN_Sign_In =findViewById(R.id.SignUp_BTN_Sign_In);
    }
}