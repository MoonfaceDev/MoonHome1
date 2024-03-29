package com.moonface.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.moonface.Util.InputUtil;
import com.moonface.Util.PermissionsRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class SignInFragment extends Fragment {
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    private HashMap<String, Object> data_update = new HashMap<>();
    private String emailAddress = "";
    private Intent home_int = new Intent();
    private DatabaseReference userdata;
    private SharedPreferences data;
    private FirebaseAuth userAuth;
    private OnCompleteListener<AuthResult> _userAuth_sign_in_listener;
    private AlertDialog.Builder edittextdialog;
    private FirebaseStorage _storage = FirebaseStorage.getInstance();
    public static Uri profileUri;
    private CheckBox checkbox2;
    private TextView forgotpass_button;
    private EditText email_login;
    private EditText password_login;
    private ProgressDialog prog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        data = getActivity().getSharedPreferences("data", Activity.MODE_PRIVATE);
        edittextdialog = new AlertDialog.Builder(getContext());
        userAuth = FirebaseAuth.getInstance();
        MainActivity.CURRENT_FRAGMENT = 1;
        email_login = view.findViewById(R.id.email_login);
        final TextInputLayout email_input_layout = view.findViewById(R.id.email_input_layout);
        password_login = view.findViewById(R.id.password_login);
        final TextInputLayout password_input_layout = view.findViewById(R.id.password_input_layout);
        checkbox2 = view.findViewById(R.id.checkbox2);
        forgotpass_button = view.findViewById(R.id.forgotpass_button);
        final Button login_button = view.findViewById(R.id.login_button);
        final TextView signuptab_button = view.findViewById(R.id.signuptab_button);
        final TextView signup_label = view.findViewById(R.id.signup_label);
        prog = new ProgressDialog(getContext());
        prog.setMessage(getString(R.string.retrieving_data));
        prog.setIndeterminate(true);
        prog.setCancelable(false);

        MainActivity.initializeImageview2(1);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputUtil.hideKeyboard(getContext(), getActivity());
                if (email_login.getText().toString().length() > 0) {
                    if (password_login.getText().toString().length() > 0) {
                        if(PermissionsRequest.isGranted(PermissionsRequest.WRITE_EXTERNAL_STORAGE, getContext())) {
                            userAuth.signInWithEmailAndPassword(email_login.getText().toString(), password_login.getText().toString()).addOnCompleteListener(getActivity(), _userAuth_sign_in_listener);
                            prog.show();
                        } else {
                            requestPermissions(new String[]{PermissionsRequest.WRITE_EXTERNAL_STORAGE}, 6);
                        }
                    }
                }
            }
        });
        forgotpass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout dialayout = new LinearLayout(getContext());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialayout.setLayoutParams(params);
                dialayout.setOrientation(LinearLayout.VERTICAL);

                final EditText diaedittext = new EditText(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(8,8,8,8);
                diaedittext.setLayoutParams(layoutParams);

                dialayout.addView(diaedittext);
                edittextdialog.setView(dialayout);
                diaedittext.setHint(R.string.enter_email_hint);
                edittextdialog.setTitle(R.string.password_recovery_label);
                edittextdialog.setPositiveButton(R.string.send_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        emailAddress = diaedittext.getText().toString();
                        if (!emailAddress.equals("")) {
                            userAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    final String _errorMessage = task.getException() != null ? task.getException().getMessage() : "";
                                    if (task.isSuccessful()) {
                                        SketchwareUtil.showMessage(getContext(), getString(R.string.email_sent_message));
                                    } else {
                                        SketchwareUtil.showMessage(getContext(), _errorMessage);
                                    }
                                }
                            });
                        }
                    }
                });
                edittextdialog.create().show();
            }
        });
        signuptab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<View> viewArrayList = new ArrayList<>();
                viewArrayList.add(login_button);
                viewArrayList.add(email_login);
                viewArrayList.add(password_login);
                viewArrayList.add(signuptab_button);
                viewArrayList.add(email_input_layout);
                viewArrayList.add(password_input_layout);
                viewArrayList.add(checkbox2);
                viewArrayList.add(forgotpass_button);
                viewArrayList.add(signup_label);
                ArrayList<String> stringArrayList = new ArrayList<>();
                stringArrayList.add("sign_button");
                stringArrayList.add("email");
                stringArrayList.add("password");
                stringArrayList.add("tab_button");
                stringArrayList.add("email_input_layout");
                stringArrayList.add("password_input_layout");
                stringArrayList.add("checkbox");
                stringArrayList.add("button");
                stringArrayList.add("sign_label");
                MainActivity.setCurrentFragment(getActivity().getSupportFragmentManager(), new SignUpFragment(), viewArrayList, stringArrayList);
            }
        });
        _userAuth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
                if (_success) {
                    _logdata();
                } else {
                    SketchwareUtil.showMessage(getContext(), _errorMessage);
                    prog.dismiss();
                }
            }
        };
        if (data.getString("remember_me", "").equals("1")) {
            email_login.setText(data.getString("email", ""));
            password_login.setText(data.getString("password", ""));
            checkbox2.setChecked(true);
        }
    }

    private void _logdata() {
        data.edit().putString("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
        userdata = _firebase.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot _dataSnapshot) {
                data_update = new HashMap<>();
                try {
                   data_update = (HashMap<String, Object>) _dataSnapshot.getValue();
                } catch (Exception _e) {
                    _e.printStackTrace();
                }
                data.edit().putString("email", data_update.get("user").toString()).apply();
                data.edit().putString("nickname", data_update.get("name").toString()).apply();
                data.edit().putBoolean("admin", Boolean.valueOf(data_update.get("admin").toString())).apply();
                data.edit().putString("background", data_update.get("background").toString()).apply();
                File file = null;
                try {
                    file = File.createTempFile(data.getString("id", "") + "_profile_pic", ".jpg", getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileUri = FileProvider.getUriForFile(getContext(), getString(R.string.authorities), file);
                _storage.getReferenceFromUrl(data_update.get("profile_pic_url").toString()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if (checkbox2.isChecked()) {
                            data.edit().putString("password", password_login.getText().toString()).apply();
                            data.edit().putString("remember_me", "1").apply();
                            data.edit().putString("email", FirebaseAuth.getInstance().getCurrentUser().getEmail()).apply();
                            data.edit().putString("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
                            home_int.setClass(getContext(), BootAnimationActivity.class);
                            startActivity(home_int);
                            getActivity().finish();
                        } else {
                            data.edit().putString("email", "").apply();
                            data.edit().putString("password", "").apply();
                            data.edit().putString("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
                            home_int.setClass(getContext(), BootAnimationActivity.class);
                            startActivity(home_int);
                            getActivity().finish();
                        }
                        prog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getContext(), R.string.cant_find_data_message, Toast.LENGTH_SHORT).show();
                        prog.dismiss();
                    }
                });
                data_update.clear();
            }

            @Override
            public void onCancelled(DatabaseError _databaseError) {
                Toast.makeText(getContext(), R.string.cant_find_data_message, Toast.LENGTH_SHORT).show();
                prog.dismiss();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (permissions[0]) {
            case (PermissionsRequest.WRITE_EXTERNAL_STORAGE):
                if(PermissionsRequest.isGranted(PermissionsRequest.WRITE_EXTERNAL_STORAGE, getContext())){
                    userAuth.signInWithEmailAndPassword(email_login.getText().toString(), password_login.getText().toString()).addOnCompleteListener(getActivity(), _userAuth_sign_in_listener);
                    prog.show();
                } else {
                    Toast.makeText(getContext(), R.string.permission_denied_message, Toast.LENGTH_SHORT).show();
                }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
