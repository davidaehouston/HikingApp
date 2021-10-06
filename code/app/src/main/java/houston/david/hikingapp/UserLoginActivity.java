package houston.david.hikingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/** this class is responsible for validating &
 * logging ina user, using firebase Aith.
 */
public class UserLoginActivity extends AppCompatActivity {

    private Button btn_register, btn_login;
    private EditText userEmail, userPassword;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;


    /**
     * Starter method when activity begins,
     * assigning ids to variables and onclick listeners.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        btn_register = findViewById(R.id.btn_loginRegister);
        btn_login = findViewById(R.id.btn_loginButton);
        userEmail = findViewById(R.id.email_login);
        userPassword = findViewById(R.id.password_login);
        firebaseAuth = FirebaseAuth.getInstance();
        forgotPassword = (TextView) findViewById(R.id.tv_forgotPassword);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this, UserRegisterActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this, UserResetPassword.class));
            }
        });

    }

    /**
     * This metohd provies the login information, entered by the user, and checks if
     * it matches on Firebase servers.
     * With validation, the user is prompted to fill in the details accureatly.
     */
    private void userLogin() {
        //get email and password
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        //check if email is empty.
        if(email.isEmpty()) {
            userEmail.setError("Email is Required");
            userEmail.requestFocus();
            return;
        }

        //check if email matches email pattern
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter a valid email");
            userEmail.requestFocus();
            return;
        }

        //check if password is empty.
        if(password.isEmpty()) {
            userPassword.setError("Password is Required");
            userPassword.requestFocus();
            return;
        }

        //Check if password length is less than six characters.
        if(password.length() < 6) {
            userPassword.setError("Min Password of six characters");
            userPassword.requestFocus();
            return;
        }

        //check credentials with Firebase Auth.
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(UserLoginActivity.this, "User logged In", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent (UserLoginActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(UserLoginActivity.this, "User login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}