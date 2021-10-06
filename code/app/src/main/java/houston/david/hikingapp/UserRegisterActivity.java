package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import data.User;

/**
 * This class is responsible for allowing users to registe ron the application, using firebase Auth.
 */
public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private Button btn_register;
    private EditText et_email, et_password, et_firstName, et_lastName;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        //create an instance of Firebase Aith
        mAuth = FirebaseAuth.getInstance();

        btn_register = findViewById(R.id.registerBtn);
        btn_register.setOnClickListener(this);
        et_firstName = findViewById(R.id.firstName_reg);
        et_lastName = findViewById(R.id.lastName_reg);
        et_email = findViewById(R.id.email_reg);
        et_password = findViewById(R.id.password_reg);

        //Get an instance of Firebase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    /**
     * Called when a view has been clicked.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn:
                registerUser();
                startActivity(new Intent(UserRegisterActivity.this, MainActivity.class));
                break;
        }
    }

    /**
     * Pulls data from the register fields by the user
     * with validation to ensure all feilds are filled in correctly.
     */
    private void registerUser() {
        String firstName = et_firstName.getText().toString().trim();
        String lastName = et_lastName.getText().toString().trim();
        String userEmail = et_email.getText().toString().trim();
        String userPassword = et_password.getText().toString().trim();
        String userID = UUID.randomUUID().toString();

        //if no first name is entered
        if(firstName.isEmpty()) {
            et_firstName.setError("First Name is required");
            et_firstName.requestFocus();
            return;
        }

        //If no last Name is entered
        if(lastName.isEmpty()) {
            et_lastName.setError("Last Name is required");
            et_lastName.requestFocus();
            return;
        }

        //if no email is entered
        if(userEmail.isEmpty()) {
            et_email.setError("Email address is required");
            et_email.requestFocus();
            return;

        }

        //if email does not match email style pattern
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            et_email.setError("Please provide a valid email");
            et_email.requestFocus();
            return;
        }

        //If password is not provided
        if(userPassword.isEmpty()) {
            et_password.setError("Password is required");
            et_password.requestFocus();
            return;

        }

        //If password is not longer than six characters (for security)
        if(userPassword.length() < 6) {
            et_password.setError("Password must be atleast six characters long");
            et_password.requestFocus();
            return;
        }

        //Create a new user
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserID = mAuth.getCurrentUser().getUid();
                            DocumentReference reference = firebaseFirestore.collection("AppUsers").document(UserID);
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("firstName", firstName);
                            userMap.put("lastName", lastName);
                            userMap.put("userEmail", userEmail);
                            userMap.put("userID", userID);
                            reference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(UserRegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        } else {
                            Toast.makeText(UserRegisterActivity.this, "Registration failed! " + "\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("reggie", "onFailure:"+ e.toString());
            }
        });
    }

}