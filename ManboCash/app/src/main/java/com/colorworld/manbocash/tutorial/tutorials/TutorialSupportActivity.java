package com.colorworld.manbocash.tutorial.tutorials;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.colorworld.manbocash.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
//import com.kakao.auth.authorization.accesstoken.AccessToken;
//import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class TutorialSupportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_ROLLBACK = "key_rollback";
    public static final int GOOGLE_SIGN_IN = 1000;
    public static final int KAKAO_SIGN_IN = 2000;

    private Context mContext;
    private Activity mActivity;
    private boolean noRollback;

    public com.kakao.usermgmt.LoginButton login_kakao;
    public CallbackManager mCallbackManager;

    /*facebook*/
    public LoginButton loginButton;
    private FirebaseAuth mAuth;
    private com.facebook.AccessToken getFacebookAccessToken;
    private GoogleSignInClient mGoogleSignInClient;

    Button logoutButton;


    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean noRollback) {
        Intent intent = new Intent(context, TutorialSupportActivity.class);
        intent.putExtra(KEY_ROLLBACK, noRollback);
        context.startActivity(intent);
    }


    //    /*
    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.e("d", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
//    */


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.e("ios", "onStart currentUser : " + currentUser);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mActivity = this;

        setContentView(R.layout.activity_tutorial_and_login);

        Log.e("ios", "hash key : " + getKeyHash(mContext));


        login_kakao =  findViewById(R.id.ivFirstBtn);
        login_kakao.setOnClickListener(this);

        /* ---- Google Login ---- */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("387864702922-aj4n1rm93ulli9cootpiptpuhje9a4nr.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*facebook login*/
        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.ivSecondBtn);
        loginButton.setPermissions("email", "public_profile");
        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("ios", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.e("ios", "========facebook login cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("ios", "========facebook login error : " + exception);
            }
        });

        findViewById(R.id.ivThirdBtn).setOnClickListener(this);

        noRollback = getIntent().getBooleanExtra(KEY_ROLLBACK, false);

        if (savedInstanceState == null) {
            replaceTutorialFragment(noRollback);
        }
        Session.getCurrentSession().addCallback(new KakaoSessionCallback());
    }


    /**
     * OnActivityResult() should be overridden for Kakao Login because Kakao Login uses startActivityForResult().
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.i("google", "onActivityResult");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }

    }
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.i("google", "displayName: "+ account.getDisplayName()+ " id: "+account.getId() + " idToken: "+ account.getIdToken());
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("google", "signInResult:failed code=" + e.getStatusCode());

        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("google", "firebaseAuthWithGoogle:" + acct.getId() + "/" + acct.getDisplayName());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("google", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("google", "user => "+ user.getUid());
                            mActivity.finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("google", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.e("ios", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("ios", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e("ios", "facebook currentUser : " + user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("ios", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    /**
     * @param kakaoAccessToken Access token retrieved after successful Kakao Login
     * @return Task object that will call validation server and retrieve firebase token
     */
    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        Toast.makeText(mActivity, "Kakao AccessToken=> " + kakaoAccessToken, Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(mActivity);
        String url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firebaseToken = response.getString("firebase_token");
                    Log.e("ios" , "firebaseToken(From Server->JWT): " + firebaseToken);
                    source.setResult(firebaseToken);
                } catch (Exception e) {
                    source.setException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ios", error.toString());
                source.setException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", kakaoAccessToken);
                return params;
            }
        };

        queue.add(request);
        return source.getTask();
    }


    /**
     * Session callback class for Kakao Login. OnSessionOpened() is called after successful login.
     */
    private class KakaoSessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {

            Session currentSession = Session.getCurrentSession();
            final com.kakao.auth.authorization.accesstoken.AccessToken accessToken = currentSession.getTokenInfo();



            /*
            *테스트용으로 프로덕트일때는 풀어야됨
            *
            SharedPreferences sp = getSharedPreferences("isFirstRun", MODE_PRIVATE);
            sp.edit().putBoolean("running", true).apply();

            Intent isLogin = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(isLogin);
            */


            getFirebaseJwt(accessToken.getAccessToken()).continueWithTask(new Continuation<String, Task<AuthResult>>() {
                @Override
                public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                    String firebaseToken = task.getResult();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    Log.d("kakao", "firebaseToken(before signIn): " + firebaseToken);

                    return auth.signInWithCustomToken(firebaseToken);
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Successfully logged in to Kakao. Now creating or updating a Firebase User.", Toast.LENGTH_LONG).show();

                        Log.e("ios", "Successfully logged in to Kakao. Now creating or updating a Firebase User. token : " + accessToken.getAccessToken());

                        Log.e("ios", "KAKAO session login success");
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        Log.e("kakao", "kakao=> " + currentUser.getDisplayName());
                        mActivity.finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG).show();
                        if (task.getException() != null) {
                            Log.e("kakao", task.getException().toString());
                        }
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

            /*
            * 프로덕트일때 풀어야됨
            SharedPreferences sp = getSharedPreferences("isFirstRun", MODE_PRIVATE);
            sp.edit().putBoolean("running", false).apply();
             */


            if (exception != null) {
                Toast.makeText(mContext, "exception.toString()", Toast.LENGTH_LONG).show();
                Log.e("kakao", exception.toString());
            }

            //지워야됨
//            mActivity.finish();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivThirdBtn:
                googleSignIn();
                break;
                default:
        }
    }

    public void replaceTutorialFragment(boolean noRollback) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, CustomTutorialSupportFragment.newInstance(noRollback))
                .commit();
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

}

