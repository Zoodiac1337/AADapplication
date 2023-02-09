package com.example.aadapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordPopup extends AppCompatActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_popup);
    }
    private void changePassword(View view){

        //check all required fields are not null
        String oldPass = findViewById(R.id.OldPasswordField).toString();
        String newPassOne = findViewById(R.id.NewPasswordField1).toString();
        String newPassTwo = findViewById(R.id.NewPasswordField2).toString();

        if(oldPass.contains(" ")||newPassOne.contains(" ")||newPassTwo.contains(" ")){
            Toast.makeText(this,"one or more fields containe a space",Toast.LENGTH_SHORT);
            return;
        }

        if(newPassOne!=newPassTwo){
            Toast.makeText(this,"passwords don't match",Toast.LENGTH_SHORT);
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

                                Toast.makeText(view.getContext(),"Something went wrong. Please try again later",Toast.LENGTH_SHORT);
                                return;
                            }else {

                                Toast.makeText(view.getContext(),"Password Changed",Toast.LENGTH_SHORT);
                                return;

                            }
                        }
                    });
                }else {
                    Toast.makeText(view.getContext(),"Something went wrong. Please try again later",Toast.LENGTH_SHORT);
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
