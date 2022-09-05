package com.example.netflix.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflix.Mainscreens.Mainscreen;
import com.example.netflix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentGateway extends AppCompatActivity implements PaymentResultListener  {
    String PlanName,PlanCost,PlanCostFormat,UserEmailId,UserPassword,UserID;
    EditText firstnameedittext,lastnameedittext,contactnumberedittext;
    Button startyourmembership;
    CheckBox iagree;
    TextView texturl,textView,change,costtoset,plannametoset;
    String firstname,lastname,contactnumber;
    String TAG="payment error";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Date today,validate;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        getSupportActionBar().hide();
        Checkout.preload(this);//done!
        Intent i=getIntent();
        PlanName=i.getStringExtra("PlanName");
        PlanCost=i.getStringExtra("PlanCost");// this null price is not coming from previus activity solve that.
        Log.d("PLANCOT", "cost:"+PlanCost);
        // open from where you sending this
        PlanCostFormat=i.getStringExtra("PlanCostFormat");
        UserEmailId=i.getStringExtra("EmailId");
        UserPassword=i.getStringExtra("Password");
        Checkout.preload(getApplicationContext());
        firstnameedittext=findViewById(R.id.firstnameedittext);
        lastnameedittext=findViewById(R.id.lastnameedittext);
        contactnumberedittext=findViewById(R.id.contactnumberedittext);
        startyourmembership=findViewById(R.id.startmembershipbutton);
        iagree=findViewById(R.id.iagree);
        texturl=findViewById(R.id.typetoputurltext);
        textView=findViewById(R.id.step3of3);
        change=findViewById(R.id.Change);
        costtoset=findViewById(R.id.CostToset);
            costtoset.setText(PlanCostFormat);
        plannametoset=findViewById(R.id.plannametoset);
        plannametoset.setText(PlanName);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        Calendar c=Calendar.getInstance();
        today=c.getTime();
        c.add(Calendar.MONTH,1);
         validate=c.getTime();
        SpannableString st=new SpannableString("STEP 3 OF 3");
        StyleSpan boldspan=new StyleSpan(Typeface.BOLD);
        StyleSpan boldspan1=new StyleSpan(Typeface.BOLD);
        st.setSpan(boldspan,5,6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        st.setSpan(boldspan1,10,11,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(st);
        SpannableString ss=new SpannableString("By checking below,you agree to our Terms of Use,Privacy Statement,and that you are over 18.Netflix will automatically continue membership and charge the monthly membership fee to your payment method until you cancel.You may cancel at any time to avoid feature charges.");
        ClickableSpan clickableSpan=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com/en")));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
        ClickableSpan clickableSpan1=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com/legal/privacy")));

            }
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
           ss.setSpan(clickableSpan,36,47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
           ss.setSpan(clickableSpan1,49,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
           texturl.setText(ss);
           texturl.setMovementMethod(LinkMovementMethod.getInstance());
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PaymentGateway.this,ChooseYourPlan.class);
                startActivity(i);
            }
        });
        startyourmembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstnameedittext.getText().toString().length() > 3 && lastnameedittext.getText().toString().length() > 3 && firstnameedittext.getText().toString().matches("[a-z A-Z]+") && lastnameedittext.getText().toString().matches("[a-z A-Z]+") && contactnumberedittext.getText().toString().length() == 10 && iagree.isChecked()) {
                    progressDialog=new ProgressDialog(PaymentGateway.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    startPayment();
                }

                else {
                    if (firstnameedittext.getText().toString().length() <= 3 || !firstnameedittext.getText().toString().matches("[a-z A-Z]+")){
                        {
                            firstnameedittext.setError("Enter a valid first name");
                        }
                        if (lastnameedittext.getText().toString().length() <= 3 || !lastnameedittext.getText().toString().matches("[a-z A-Z]+")){
                            {
                                lastnameedittext.setError("Enter a valid last name");
                            }
                            if (contactnumberedittext.getText().toString().length()!=10)
                            {
                                contactnumberedittext.setError("Enter a valid 10 digit contact number");
                                progressDialog.cancel();
                            }
                            if (!iagree.isChecked()){
                                Toast.makeText(getApplicationContext(), "Please agree the policy", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please fill the correct user information", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }
                    }
                }
            }
        });
    }



    public void startPayment(){
        Checkout checkout=new Checkout();//
       checkout.setKeyID("rzp_test_BD4v44UU9W6N65");
        final Activity activity=this;
        firstname=firstnameedittext.getText().toString();
        lastname=lastnameedittext.getText().toString();
        contactnumber=contactnumberedittext.getText().toString();
        String name=firstname+lastname;
        try {
            JSONObject options=new JSONObject();
            options.put("name",name);
            options.put("description","APP PAYMENT");
            options.put("currency","INR");
           String payment=PlanCost;
            double total=Double.parseDouble(payment.toString());
//            //int total=Integer.valueOf(payment);
           total=total*100;
            options.put("amount",total);
            options.put("prefill.email",UserEmailId);
            options.put("prefill.contact",contactnumber);
            checkout.open(activity,options);


        }catch (Exception e){
            Log.e(TAG,"error occures",e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        firebaseAuth.createUserWithEmailAndPassword(UserEmailId,UserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference=firebaseFirestore.collection("Users").document(UserID);
                    Map<String,Object> user=new HashMap<>();
                    user.put("Email",UserEmailId);
                    user.put("First_Name",firstname);
                    user.put("last_Name",lastname);
                    user.put("Plan_cost",PlanCost);
                    user.put("contact_number",contactnumber);
                    user.put("Valid_date",validate);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent i = new Intent(PaymentGateway.this, Mainscreen.class);
                            startActivity(i);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                if (e instanceof FirebaseNetworkException){
                                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                                  Toast.makeText(getApplicationContext(),"Values not stored",Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                        }
                    });

                }
            }
        });

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "payment unsucessfull", Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
        Log.e(TAG,"error occures");
    }
}

