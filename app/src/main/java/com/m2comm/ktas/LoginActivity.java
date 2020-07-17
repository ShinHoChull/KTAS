package com.m2comm.ktas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.m2comm.ktas.databinding.ActivityLoginBinding;
import com.m2comm.ktas.module.Custom_SharedPreferences;
import com.m2comm.ktas.module.Global;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Custom_SharedPreferences csp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        this.binding.setLogin(this);

        this.init();

    }

    private void reset() {
        csp.put("timeCount", 0);
        csp.put("selectNum", "0");
        csp.put("cycle","");
    }

    private void init() {
        this.csp = new Custom_SharedPreferences(this);

        this.binding.loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.idEt.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (binding.pwEt.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String deviceid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                csp.put("deviceid",deviceid);
                AndroidNetworking.post(Global.loginUrl)
                        .addBodyParameter("user_id", binding.idEt.getText().toString())
                        .addBodyParameter("passwd", binding.pwEt.getText().toString())
                        .addBodyParameter("deviceid", deviceid)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response=",response.toString());
                                try {
                                    if (response.getString("success").equals("N")) {
                                        Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                                    } else {

                                        String test_code = response.isNull("test_code")?"":response.getString("test_code");
                                        if ( csp.getValue("test_code","").equals("") || !csp.getValue("test_code","").equals(test_code) ) {
                                            csp.put("test_code",test_code);
                                            reset();
                                        }

                                        csp.put("edu_type",response.isNull("edu_type")?"":response.getString("edu_type"));
                                        csp.put("ans_num",response.isNull("ans_num")?"":response.getString("ans_num"));
                                        csp.put("cycle",response.isNull("cycle")?"":response.getString("cycle"));
                                        csp.put("base_limit_time",response.isNull("base_limit_time")?"":response.getString("base_limit_time"));

                                        Intent intent = new Intent(getApplicationContext() , TestActivity.class);
                                        intent.putExtra("url",response.isNull("url")?"":response.getString("url"));
                                        startActivity(intent);
                                        finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }
        });


    }
}