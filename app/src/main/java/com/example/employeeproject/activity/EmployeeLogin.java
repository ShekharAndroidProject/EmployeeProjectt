package com.example.employeeproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.employeeproject.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class EmployeeLogin extends AppCompatActivity {
    EditText et_loginUsername, et_loginPassword;
    Button btn_loginLogin;
    String val_phone, val_email;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;
    Context context;
    GoogleApiClient googleApiClient;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);
        et_loginUsername = (EditText) findViewById(R.id.et_loginUsername);
        et_loginPassword = (EditText) findViewById(R.id.et_loginPassword);
        btn_loginLogin = (Button) findViewById(R.id.btn_loginLogin);

        activity = EmployeeLogin.this;
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(activity);

        SignInButton googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestProfile()
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(EmployeeLogin.this, "Something went wrong ", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(EmployeeLogin.this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btn_loginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginValidate();
            }
        });
//              FirebaseApp.initializeApp(this);
//

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "profile", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fb", "facebook:onSuccess:" + loginResult);
                Toast.makeText(EmployeeLogin.this, "Success", Toast.LENGTH_LONG).show();
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            }

            @Override
            public void onCancel() {
                Log.d("FB", "facebook:onCancel");
                Toast.makeText(EmployeeLogin.this, "Cancelled", Toast.LENGTH_LONG).show();
                // updateUI(null);
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                Toast.makeText(EmployeeLogin.this, "Error", Toast.LENGTH_LONG).show();
                Toast.makeText(EmployeeLogin.this, "" + exception, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.i("Response", response.toString());

                    String email = response.getJSONObject().getString("email");
                    String firstName = response.getJSONObject().getString("first_name");
                    String lastName = response.getJSONObject().getString("last_name");
                    String gender = response.getJSONObject().getString("gender");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("google", "firebaseAuthWithGoogle:" + account.getId());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("google", "Google sign in failed", e);
                // [START_EXCLUDE]
                //  updateUI(null);
                // [END_EXCLUDE]
            }
            Toast.makeText(EmployeeLogin.this, "Succes", Toast.LENGTH_SHORT).show();
            startSignInIntent();
            Intent i = new Intent(getApplicationContext(), GmailUserInfo.class);
            startActivity(i);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code     indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("google", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }


    //  private void updateUI(FirebaseUser user) {
//        hideProgressBar();
//        if (user != null) {
//            mBinding.status.setText(getString(R.string.facebook_status_fmt, user.getDisplayName()));
//            mBinding.detail.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            mBinding.buttonFacebookLogin.setVisibility(View.GONE);
//            mBinding.buttonFacebookSignout.setVisibility(View.VISIBLE);
//        } else {
//            mBinding.status.setText(R.string.signed_out);
//            mBinding.detail.setText(null);
//
//            mBinding.buttonFacebookLogin.setVisibility(View.VISIBLE);
//            mBinding.buttonFacebookSignout.setVisibility(View.GONE);
    //     }
    //   }

    private void loginValidate() {
        String val_username = et_loginUsername.getText().toString().trim();
        String val_paswword = et_loginPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String phonePattern = "[0-9]{13}";
        String username_phone = "1234567890";
        String username_email = "abc123@gmail.com";
        String password = "12345678";
        if (val_username.matches(emailPattern)) {
            val_phone = "";
            val_email = "" + val_username;
        } else if (val_username.matches(phonePattern)) {
            val_email = "";
            val_phone = "" + val_username;
            return;
        }
        if (val_paswword.isEmpty() && val_username.isEmpty()) {
            et_loginUsername.setError("Required field");
            et_loginPassword.setError("Required field");
            return;
        }
        if ((val_email.matches(username_email) || val_phone.matches(username_phone)) && val_paswword.matches(password)) {
            Toast.makeText(EmployeeLogin.this, "Login Succesful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), EmployeeHome.class);
            startActivity(intent);
        } else {
            Toast.makeText(EmployeeLogin.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
        }

    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
}
//    String val_username=et_loginUsername.getText().toString().trim();
//                if (!val_username.contains("@")){
//                        et_loginUsername.setError("proper email required");
//                        return;
//                        }
//                        else if (!val_username.contains("0-9")){
//                        et_loginUsername.setError("proper phone required");
//                        return;
//                        }
//       val_username.matches(emailPattern) || val_username.matches(phonePattern)?(et_loginUsername.setError("Proper email is required")):et_loginUsername.setError("phone no is needed");
