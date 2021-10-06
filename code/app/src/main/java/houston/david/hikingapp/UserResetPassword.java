package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class is responsible for resetting a users password
 */
public class UserResetPassword extends AppCompatActivity {

    private EditText email;
    private Button resetPasswordBtn;
    private FirebaseAuth firebaseAuth;

    /**
     * starter method for this class, assigning Ids to variables.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reset_password);

        email = findViewById(R.id.et_userEmail);
        resetPasswordBtn = findViewById(R.id.btn_resetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    /**
     * Method responsible for resetting a users password
     * with validation it can check if an email is entered and in the
     * correct pattern.
     *
     * Using Firebase Auth to check if the user already exists on the system
     * if so an email with a reset link it sent.
     */
    private void resetPassword() {

        String userEmail = email.getText().toString().trim();

        if(userEmail.isEmpty()) {
            email.setError("Please enter an Email");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Please enter a valid Email");
            email.requestFocus();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(UserResetPassword.this, "Check your email for a rest link", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UserResetPassword.this, "Email address not found, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}