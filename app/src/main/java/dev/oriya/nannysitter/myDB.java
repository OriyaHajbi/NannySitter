package dev.oriya.nannysitter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myDB {
    private static myDB myDatabase;
    private FirebaseDatabase database;
    private DatabaseReference reference_users;
    private FirebaseUser current_user;

    public myDB setUser_callback(User_Callback user_callback) {
        this.user_callback = user_callback;
        return this;
    }

    private User_Callback user_callback;
    private User user;
    private boolean isUserNull = true;




    public static myDB getInstance() {
        if (myDatabase == null) {
            myDatabase = new myDB();
            myDatabase.database = FirebaseDatabase.getInstance();
            myDatabase.reference_users = myDatabase.database.getReference("Users");
            myDatabase.current_user = FirebaseAuth.getInstance().getCurrentUser();
        }
        return myDatabase;
    }

    public void addUser(User user) {
        reference_users.child(current_user.getUid()).setValue(user);
    }
    public void LoadUser() {
        reference_users.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 user = snapshot.getValue(User.class);
                if (user == null){
                    // User Does Not Exist
                    user_callback.userDoesNotExist();
                }else{
                    // User Exist
                    user_callback.userExistAndGetUserData(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", "loadUser:onCancelled", error.toException());
            }
        });
    }
}
