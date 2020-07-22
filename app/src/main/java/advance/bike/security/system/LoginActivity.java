package advance.bike.security.system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String inputPassword;
    private TextView aboutNewPasswordTextView;
    private Button loginButton;
    private EditText passwordEditText;
    private boolean willSaveInputtedPassword=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initAll();


    }

    private void initAll() {
        aboutNewPasswordTextView=findViewById(R.id.loginActivityAboutNewPasswordTextViewId);
        loginButton=findViewById(R.id.loginActivityLoginButtonId);
        passwordEditText=findViewById(R.id.loginActivityPasswordEditTextId);

        loginButton.setOnClickListener(this);

    }

    private void showHideAboutNewPasswordTextView() {
        String savedPassword=Utility.getStringFromStorage(LoginActivity.this,Constants.userSavedPasswordKey,null);
        if (savedPassword==null || TextUtils.isEmpty(savedPassword)){
            aboutNewPasswordTextView.setVisibility(View.VISIBLE);
            willSaveInputtedPassword=true;
        }else {
            aboutNewPasswordTextView.setVisibility(View.GONE);
            willSaveInputtedPassword=false;
        }
    }

    private void startLoginOperation() {
        inputPassword=passwordEditText.getText().toString();
        if (TextUtils.isEmpty(inputPassword)){
            passwordEditText.setError("Please input your password properly.");
            passwordEditText.setFocusable(true);
        }else if (inputPassword.length()<4){
            passwordEditText.setError("Please input minimum 4 digit as your password.");
            passwordEditText.setFocusable(true);
        }else {
            if (willSaveInputtedPassword){
                Utility.setStringToStorage(LoginActivity.this,Constants.userSavedPasswordKey,inputPassword);
            }
            String savedPassword=Utility.getStringFromStorage(LoginActivity.this,Constants.userSavedPasswordKey,null);
            if (savedPassword.equalsIgnoreCase(inputPassword)){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }else {
                passwordEditText.setError("Your password is incorrect, Please try again.");
                passwordEditText.setFocusable(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginActivityLoginButtonId:
                startLoginOperation();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showHideAboutNewPasswordTextView();

    }



}