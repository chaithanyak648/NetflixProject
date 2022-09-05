package com.example.netflix.Mainscreens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflix.Activities.SigninActivity;
import com.example.netflix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Settings extends AppCompatActivity {
    EditText newpassword;
    TextView email,plan,date;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    FirebaseUser user;
    DocumentReference reference;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Button resetpassword,signout;
    String Uid,emailstring,planstring;
    Date validate;
    View view;
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent l=new Intent(Settings.this,Mainscreen.class);
        startActivity(l);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        BottomNavigationView bottomNavigationView;
        firebaseAuth=FirebaseAuth.getInstance();
        newpassword=findViewById(R.id.passwordreset);
        resetpassword=findViewById(R.id.resetpasswordbutton);
        signout=findViewById(R.id.signoutbutton);
        email=findViewById(R.id.emailsettings);
        plan=findViewById(R.id.plansettings);
        date=findViewById(R.id.datesettings);
        user=firebaseAuth.getInstance().getCurrentUser();
        progressDialog=new ProgressDialog(Settings.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        if (firebaseAuth.getCurrentUser()!=null){
            Uid=firebaseAuth.getCurrentUser().getUid();
            reference=firebaseFirestore.collection("Users").document(Uid);
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    validate=documentSnapshot.getDate("Valid_date");
                    emailstring=documentSnapshot.getString("Email");
                    planstring=documentSnapshot.getString("Plan_cost");
                    email.setText(emailstring);
                    plan.setText("₹"+planstring+"/mo.");
                    date.setText(validate.toString());
                    progressDialog.cancel();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseNetworkException){
                        Toast.makeText(getApplicationContext(),"NO internet connection",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplicationContext(),"Error data not fetched",Toast.LENGTH_SHORT).show();
                          progressDialog.cancel();
                }
            });
        }


        bottomNavigationView=findViewById(R.id.bottom_nav);
        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeicon:
                        Intent l=new Intent(Settings.this,Mainscreen.class);
                        startActivity(l);
                        break;
                    case R.id.searchicon:
                        Intent i=new Intent(Settings.this,Search.class);
                        startActivity(i);
                        break;
                    case R.id.settingsicon:
                        Intent j=new Intent(Settings.this,Settings.class);
                        startActivity(j);
                        break;
                }
                return false;
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder signout=new AlertDialog.Builder(Settings.this);
                signout.setTitle("Do you really want to signout?");
                signout.setMessage("press YES to signout");
                signout.setCancelable(false);
                signout.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseAuth.getInstance().signOut();
                        Intent x=new Intent(Settings.this, SigninActivity.class);
                        startActivity(x);
                        finish();
                        signout.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });


                    }

                });

                signout.create().show();
            }

        });
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=new ProgressDialog(Settings.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (newpassword.getText().toString().length()>7){
                   firebaseAuth.signInWithEmailAndPassword(emailstring,newpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                           EditText Changepassword = new EditText(Settings.this);
                           AlertDialog.Builder updatepassword = new AlertDialog.Builder(Settings.this);
                           updatepassword.setTitle("Update Password?");
                           updatepassword.setCancelable(false);
                           Changepassword.setHint("New password");
                           Changepassword.setSingleLine();
                           updatepassword.setView(Changepassword);
                           updatepassword.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   progressDialog.show();
                                   String newpasswordstring = Changepassword.getText().toString();
                                   if (newpasswordstring.length() > 7) {
                                       user.updatePassword(newpasswordstring).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void unused) {
                                               Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                               newpassword.setText("");
                                               progressDialog.cancel();
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               if (e instanceof FirebaseNetworkException)
                                                   Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                                               Toast.makeText(getApplicationContext(), "Password not updated", Toast.LENGTH_SHORT).show();
                                               progressDialog.cancel();
                                           }
                                       });
                                   } else {

                                       Toast.makeText(getApplicationContext(), "Password to short please retry", Toast.LENGTH_SHORT).show();
                                       progressDialog.cancel();
                                   }


                               }

                           });
                           updatepassword.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int i) {
                                   newpassword.setText("");
                                   progressDialog.cancel();
                               }
                           });

                           updatepassword.create().show();
                        }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           if (e instanceof FirebaseNetworkException)
                               Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                           if (e instanceof FirebaseNetworkException)
                                     newpassword.setError("Incorrect password");
                           else
                               newpassword.setError("Incorrect password");
                           progressDialog.cancel();
                       }
                   });
                }
                else {
                       newpassword.setError("Password to short");
                       progressDialog.cancel();
                }
            }
        });

    }
}