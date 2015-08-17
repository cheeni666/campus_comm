package com.barebringer.testgcm1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Author extends Activity {
    EditText user, pass;
    int option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        option = getIntent().getIntExtra("option", 1);
        setContentView(R.layout.activity_author);
        user = (EditText) findViewById(R.id._user);
        pass = (EditText) findViewById(R.id._password);
    }

    public void submit(View v) {
        String username = user.getText().toString();
        String password = user.getText().toString();
        if (option == 1 && uservalidation(username, password)) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("username", username);
            i.putExtra("option", option);
            SharedPreferences store = getSharedPreferences("testgcm1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = store.edit();
            editor.putString("usertext", username);
            editor.apply();
            startActivity(i);
            return;
        } else if (option == 1)
            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
        if (option == 0 && adminvalidation(username, password)) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("username", username);
            i.putExtra("option", option);
            startActivity(i);
            return;
        } else if (option == 0)
            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
    }

    public boolean uservalidation(String userName, String passWord) {
        //validation
        return true;
    }

    public boolean adminvalidation(String userName, String passWord) {
        //validation
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
