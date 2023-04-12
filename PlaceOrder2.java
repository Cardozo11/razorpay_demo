package com.example.hirework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.razorpay.Checkout;
//import com.razorpay.PaymentResultListener;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlaceOrder2 extends AppCompatActivity implements PaymentResultListener {
//public class PlaceOrder2 extends AppCompatActivity{
    Button payBtn;
    TextView amount,message;


    GigModel gig;

    OrderModel order;


    String purpose;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order2);

        gig=new GigModel(this);
        order=new OrderModel(this);

        amount =findViewById(R.id.amount);
        payBtn=findViewById(R.id.paymentBtn);
        message=findViewById(R.id.displayMessage);

        purpose=getIntent().getStringExtra("purpose");




        if(purpose.equals("placeOrder")){
            loadPrice();
        }
        else if(purpose.equals("pendingOrders")){
            allowPayment();
        }
        


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String samount = amount.getText().toString();

                // rounding off the amount.
                int amount = Math.round(Float.parseFloat(samount) * 100);

                // initialize Razorpay account.
                Checkout checkout = new Checkout();

                // set your id as below
                checkout.setKeyID("rzp_test_x7nTopI2xBRXgn");

                // set image
                checkout.setImage(R.drawable.rzp_logo);

                // initialize json object
                JSONObject object = new JSONObject();
                try {
                    // to put name
                    object.put("name", "LinkUp");

                    // put description
                    object.put("description", "Test payment");

                    // to set theme color
                    object.put("theme.color", "");

                    // put the currency
                    object.put("currency", "INR");

                    // put amount
                    object.put("amount", amount);

                    // put mobile number
                    object.put("prefill.contact", "9284064503");

                    // put email
                    object.put("prefill.email", "chaitanyamunje@gmail.com");

                    // open razorpay to checkout activity
                    checkout.open(PlaceOrder2.this, object);
                } catch (JSONException e) {
                    Log.e("TAG", "Error");
//                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public void onPaymentSuccess(String s) {

        updatePaymentStatus();
        startActivity(new Intent(getApplicationContext(),OrderSuccess.class));

    }

    @Override
    public void onPaymentError(int i, String s) {
        // on payment failed.
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }

    public void Back(View view){
        startActivity(new Intent(getApplicationContext(),CustomerOrderStats.class));
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),CustomerOrderStats.class));
    }


    private void updatePaymentStatus(){
        String oId=String.valueOf(order.orderId);
        StringRequest request = new StringRequest(Request.Method.POST, PAYMENT_STATUS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.getMessage()!=null){
                    Toast toast=Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("oId", oId);
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(PlaceOrder2.this);
        requestQueue.add(request);
    }

    private void loadPrice(){
        String price=getIntent().getStringExtra("order_price");
        amount.setText(price);
    }


    private void allowPayment(){
        order.orderAmount=getIntent().getIntExtra(order.INTENT_ORDER_PRICE,0);
        amount.setText(String.valueOf(order.orderAmount));
        message.setVisibility(View.GONE);
        payBtn.setEnabled(true);

        // getting ord Id of the current order
        order.orderId=getIntent().getIntExtra(order.INTENT_ORDER_ID,0);

    }
}