package com.suretrust.farmerconnect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.Arrays;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 123;
    private EditText amountEditText;

    private RadioButton upi_radio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_payment);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        Window window = this.getWindow();
//        window.setStatusBarColor(this.getResources().getColor(R.color.top));

        String upiID = getIntent().getStringExtra("upiID");
        String name = getIntent().getStringExtra("name");

        TextView nametv=findViewById(R.id.name);
        nametv.setText(name);

        String BHIM_UPI = "in.org.npci.upiapp";
        String GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user";
        String PHONE_PE = "com.phonepe.app";
        String PAYTM = "net.one97.paytm";

        List<String> upiApps = Arrays.asList(PAYTM, GOOGLE_PAY, PHONE_PE, BHIM_UPI);

        Button upiButton = findViewById(R.id.upi);
        Button paytmButton = findViewById(R.id.paytm);
        Button gpayButton = findViewById(R.id.gpay);
        Button phonepeButton = findViewById(R.id.phonepe);
        Button bhimButton = findViewById(R.id.bhim);
        amountEditText=findViewById(R.id.amount_edittext);
        upi_radio=findViewById(R.id.upi_radio);

        List<Button> upiAppButtons = Arrays.asList(paytmButton, gpayButton, phonepeButton, bhimButton);

        /*3. Defining a UPI intent with a Paytm merchant UPI spec deeplink */
        String uri = "upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=000000&paytmqr=2810050501011OOQGGB29A01&am=11&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setData(Uri.parse(uri));

        upiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upiId = upiID;
                String amount = amountEditText.getText().toString().trim();

                // Check if the UPI ID and amount are not empty
                if (amount.isEmpty() || upi_radio.isSelected()) {
                    Toast.makeText(PaymentActivity.this, "Please enter amount and select payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create the UPI intent URI using the user input values
                String uri = "upi://pay?pa=" + upiId + "&pn=Payee%20Name&mc=MerchantCode&mode=02&orgid=000000&paytmqr=PaymentReference&am=" + amount + "&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setData(Uri.parse(uri));
                Intent chooser = Intent.createChooser(intent, "Pay with...");
                startActivityForResult(chooser, REQUEST_CODE);
            }
        });

        for (int i = 0; i < upiApps.size(); i++) {
            final Button b = upiAppButtons.get(i);
            final String p = upiApps.get(i);
            Log.d("UpiAppVisibility", p + " | " + isAppInstalled(p) + " | " + isAppUpiReady(p));
            if (isAppInstalled(p) && isAppUpiReady(p)) {
                b.setVisibility(View.VISIBLE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setData(Uri.parse(uri));
                        intent.setPackage(p);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });
            } else {
                b.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("result", data != null ? data.toString() : "null");
            if (data != null) {
                String status = data.getStringExtra("Status");
                Log.d("result", status);
                if (status != null) {
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAppUpiReady(String packageName) {
        boolean appUpiReady = false;
        Intent upiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"));
        PackageManager pm = getPackageManager();
        List<ResolveInfo> upiActivities = pm.queryIntentActivities(upiIntent, 0);
        for (ResolveInfo a : upiActivities) {
            if (a.activityInfo.packageName.equals(packageName)) {
                appUpiReady = true;
                break;
            }
        }
        return appUpiReady;
    }
}
