package com.example.aadapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordPopup extends AppCompatActivity {

    private FirebaseUser user;

    private TextView accountField;
    private EditText oldPassField;

    private EditText newPassOneField;
    private EditText newPassTwoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_popup);
        Bundle bundle = getIntent().getExtras();
        String account = bundle.getString("email");
        accountField = findViewById(R.id.CurrentAccountText);
        accountField.setText(account.toString());
    }
    public void changePassword(View view){

        //check all required fields are not null
        oldPassField = findViewById(R.id.OldPasswordField);
        newPassOneField = findViewById(R.id.NewPasswordField1);
        newPassTwoField = findViewById(R.id.NewPasswordField2);

        String oldPass=oldPassField.getText().toString();
        String newPassOne=newPassOneField.getText().toString();
        String newPassTwo=newPassTwoField.getText().toString();
        System.out.println(oldPass.length());

        if(oldPass.length()==0||newPassOne.length()==0||newPassTwo.length()==0){
            Toast.makeText(this,"one or more fields not entered",Toast.LENGTH_SHORT).show();
            return;

        }

        if(oldPass.contains(" ")||newPassOne.contains(" ")||newPassTwo.contains(" ")){
            Toast.makeText(this,"one or more fields containe a space",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newPassOne.equals(newPassTwo)){
            Toast.makeText(this,"passwords don't match",Toast.LENGTH_SHORT).show();
            return;
        }


        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {


            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPassOne).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){

                                Toast.makeText(view.getContext(),"Something went wrong. Please try again later",Toast.LENGTH_SHORT).show();
                                return;
                            }else {

                                Toast.makeText(view.getContext(),"Password Changed",Toast.LENGTH_SHORT).show();
                                return;

                            }
                        }
                    });
                }else {
                    Toast.makeText(view.getContext(),"Old Pass may be incorrect Please try again later",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}



        //check that old password is correct



        //check that new passwords match

        //replace new password

        //give feedback
