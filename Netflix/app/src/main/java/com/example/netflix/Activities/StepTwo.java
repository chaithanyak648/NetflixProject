package com.example.netflix.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Timer;
import java.util.TimerTask;

public class StepTwo extends AppCompatActivity {
    String PlanName,PlanCost,PlanCostFormat,UserEmailId,UserPassword;
    TextView Signin,textView;
    Button continuebutton;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    Boolean X ;
    ProgressDialog progressDialog;
    EditText emailiduser,userpassword;
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_two);
        getSupportActionBar().hide();
        Intent i = getIntent();
        PlanName = i.getStringExtra("PlanName");
        PlanCost = i.getStringExtra("PlanCost");
        PlanCostFormat = i.getStringExtra("PlanCostFormat");
        textView = findViewById(R.id.step2of3);
        continuebutton = findViewById(R.id.continuesteptwo);
        //Toast.makeText(this, ""+PlanName+"\n"+PlanCost+"\n"+PlanCostFormat, Toast.LENGTH_SHORT).show();
        Signin = findViewById(R.id.signinstepone);
        emailiduser = findViewById(R.id.emailededittextsteptwo);
        userpassword = findViewById(R.id.passwordedittextsteptwo);
        progressBar = findViewById(R.id.progressbarsteptwo);
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StepTwo.this, SigninActivity.class);
                startActivity(i);
            }
        });
        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserEmailId = emailiduser.getText().toString().trim();
                UserPassword = userpassword.getText().toString().trim();
                X=true;

                Log.d("XX",UserEmailId);
                Log.d("XX",UserPassword);
                if (UserEmailId.length() < 10 ) {
                    emailiduser.setError("Enter a valid  email id");
                    X = false;
                }
                if (UserPassword.length() < 8) {
                    userpassword.setError("password to short");
                    X = false;
                }
                if (X) {
                    {  progressDialog=new ProgressDialog(StepTwo.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        firebaseAuth.signInWithEmailAndPassword(UserEmailId, UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Please enter via the main login screen", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(StepTwo.this, SigninActivity.class);
                                    startActivity(i);

                                } else {
                                    Log.d("task", "onComplete: "+task.getException().getLocalizedMessage());
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        emailiduser.setError("Email id already registered");
                                        X = false;
                                        progressDialog.cancel();
                                    }   if (task.getException() instanceof FirebaseNetworkException){
                                        Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                                        X=false;
                                        progressDialog.cancel();
                                    }

                                      if (UserEmailId.length()>9 && UserPassword.length()>7&&
                                              X){
                                          Intent i = new Intent(StepTwo.this, StepThree.class);
                                          i.putExtra("PlanName", PlanName);
                                          i.putExtra("PlanCost", PlanCost);
                                          i.putExtra("PlanCostFormat", PlanCostFormat);
                                          i.putExtra("EmailId", UserEmailId);
                                          i.putExtra("Password", UserPassword);
                                          startActivity(i);
                                          progressDialog.cancel();

                                      }
                                }

                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StepTwo.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("ERROR", "onFailure: "+e.getLocalizedMessage());
                            }
                        });     //




                    }
                }


                SpannableString st = new SpannableString("STEP 2 OF 3");
                StyleSpan boldspan = new StyleSpan(Typeface.BOLD);
                StyleSpan boldspan1 = new StyleSpan(Typeface.BOLD);
                st.setSpan(boldspan, 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                st.setSpan(boldspan1, 10, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(st);
            }



            public void progress() {
                final Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        counter++;
                        progressBar.setProgress(counter);
                        if (counter == 1000) {
                            timer.cancel();
                        }
                    }
                };
                timer.schedule(timerTask, 0, 100);
            }
        });}}