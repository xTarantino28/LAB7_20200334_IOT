package com.example.lab7_20200334_iot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.lab7_20200334_iot.databinding.ActivityLoginBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) { //user logged-in
            if (currentUser.isEmailVerified()) {
                Log.d("msg-test", "Firebase uid: " + currentUser.getUid());
                goToMainActivity();
            }
        }

        binding.loginBtn.setOnClickListener(view -> {

            binding.loginBtn.setEnabled(false);

            /*AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout.Builder(R.layout.custom_login)
                    .setGoogleButtonId(R.id.btn_login_google)
                    .setEmailButtonId(R.id.btn_login_mail)
                    .build();*/

            //no hay sesión
            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setTheme(R.style.Base_Theme_LAB7_20200334_IOT)
                    .setIsSmartLockEnabled(false)
                    //.setAuthMethodPickerLayout(authMethodPickerLayout)
                    //.setLogo(R.drawable.pucp)
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build()
                    ))
                    .build();

            signInLauncher.launch(intent);
        });
    }

    /* launchers tienen 2 partes {
        1: contrato,
        2: callback: que hacer luego de finalizado el contrato
    } */

    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d("msg-test", "Firebase uid: " + user.getUid());
                        Log.d("msg-test", "Display name: " + user.getDisplayName());
                        Log.d("msg-test", "Email: " + user.getEmail());

                        //no verificar para correos inexistentes
                        user.reload().addOnCompleteListener(task -> {
                            if (user.isEmailVerified()) {
                                goToMainActivity();
                            } else {
                                user.sendEmailVerification().addOnCompleteListener(task2 -> {
                                    Toast.makeText(LoginActivity.this, "Se le ha enviado un correo para validar su cuenta", Toast.LENGTH_SHORT).show();
                                    //
                                });
                            }
                        });
                    } else {
                        Log.d("msg-test", "user == null");
                    }
                } else {
                    Log.d("msg-test", "Canceló el Log-in");
                }
                binding.loginBtn.setEnabled(true);
            }
    );

    public void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}