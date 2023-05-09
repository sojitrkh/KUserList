package com.example.demo2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.example.demo2.databinding.ActivityAddUserBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {

    private ActivityAddUserBinding addUserBinding;
    private RadioButton radioButton;
    int userGender;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addUserBinding = ActivityAddUserBinding.inflate(getLayoutInflater());
        View view = addUserBinding.getRoot();
        setContentView(view);

        String type = getIntent().getStringExtra("type");
        if(type.equals("Update")) {
            setTitle("Update");
            editData();
        }
        else {
            setTitle("Add Mode");
            insertData();
        }
        imagePick();

        addUserBinding.include2.fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(Objects.requireNonNull(addUserBinding.include2.fullname.getText()).toString().isEmpty()) {
                    addUserBinding.include2.fullname.setError("Enter Name");
                    addUserBinding.include2.fullname.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!Objects.requireNonNull(addUserBinding.include2.fullname.getText()).toString().trim().matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")) {
                    addUserBinding.include2.fullname.setError("Only Alphabetical Character Valid");
                    addUserBinding.include2.fullname.requestFocus();
                }
            }
        });

        addUserBinding.include2.phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(Objects.requireNonNull(addUserBinding.include2.fullname.getText()).toString().isEmpty()) {
                    addUserBinding.include2.fullname.setError("Enter Name");
                    addUserBinding.include2.fullname.requestFocus();
                } else if (Objects.requireNonNull(addUserBinding.include2.phone.getText()).toString().isEmpty()) {
                    addUserBinding.include2.phone.setError("Enter Contact No.");
                    addUserBinding.include2.phone.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Objects.requireNonNull(addUserBinding.include2.phone.getText()).toString().trim().matches("^[0-9]{10}$")) {
                    addUserBinding.include2.phone.setError("Please Enter valid phone number");
                    addUserBinding.include2.phone.requestFocus();
                }
            }
        });
        addUserBinding.include2.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (Objects.requireNonNull(addUserBinding.include2.phone.getText()).toString().isEmpty()) {
                    addUserBinding.include2.phone.setError("Enter Contact No.");
                    addUserBinding.include2.phone.requestFocus();
                } else if (Objects.requireNonNull(addUserBinding.include2.email.getText()).toString().isEmpty()) {
                    addUserBinding.include2.email.setError("Enter Email Address");
                    addUserBinding.include2.email.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Objects.requireNonNull(addUserBinding.include2.email.getText()).toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    addUserBinding.include2.email.setError("Please Enter Valid Email Address.");
                    addUserBinding.include2.email.requestFocus();
                }
            }
        });

    }

    private void editData() {
            int id = getIntent().getIntExtra("id",0);
            byte[] bytes = getIntent().getByteArrayExtra("avatar");
            if (bytes != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                addUserBinding.avatar.setImageBitmap(bitmap);
            }
            else {
                addUserBinding.avatar.setImageResource(R.drawable.baseline_account_circle_24);
            }
            addUserBinding.include2.fullname.setText(getIntent().getStringExtra("name"));
            addUserBinding.include2.phone.setText(getIntent().getStringExtra("mobile"));
            addUserBinding.include2.email.setText(getIntent().getStringExtra("email"));

            // show edit button instead of submit
            addUserBinding.submitBtn.setVisibility(View.GONE);
            addUserBinding.editBtn.setVisibility(View.VISIBLE);

            addUserBinding.editBtn.setOnClickListener(v -> {
                byte[] proavatar = ImageViewToByte(addUserBinding.avatar);
                String name = Objects.requireNonNull(addUserBinding.include2.fullname.getText()).toString().trim();
                String mobile = Objects.requireNonNull(addUserBinding.include2.phone.getText()).toString().trim();
                String email = Objects.requireNonNull(addUserBinding.include2.email.getText()).toString().trim();

                Intent serviceIntent = new Intent(AddUserActivity.this,MyService.class);
                startService(serviceIntent);

                MyService.isOnline(getApplicationContext(), new MyServiceCallback() {
                    @Override
                    public void onAvailable() {
                        Snackbar.make(addUserBinding.getRoot()," Internet Connected", Snackbar.LENGTH_SHORT).show();
                        updateApi(id, name, mobile, email, userGender);
                    }
                    @Override
                    public void onLost() {
                        Snackbar.make(addUserBinding.getRoot(),"No Internet Connection", Snackbar.LENGTH_SHORT).show();
                    }
                });
                boolean check = validateInfo(name,mobile,email);

                if(check) {
                    Intent idash = new Intent(getApplicationContext(), DashBoardActivity.class);
                    idash.putExtra("avatar", proavatar);
                    idash.putExtra("name", name);
                    idash.putExtra("mobile", mobile);
                    idash.putExtra("email", email);
                    idash.putExtra("gender",userGender);
                    idash.putExtra("id",id);
                    setResult(RESULT_OK,idash);
                    finish();
                } else {
                    Toast.makeText(AddUserActivity.this, "Enter User Details", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void updateApi(int id, String name, String mobile, String email, int userGender) {
        UserModel user = new UserModel(id, name, mobile, email, userGender);
        UserApiInterface userApiInterface = RetrofitClientInstance.getRetrofitInstance().create(UserApiInterface.class);
        if (user.getUpdatedAt() == null) {
            Call<ApiRootModel> call = userApiInterface.updateUser(user);
            call.enqueue(new Callback<ApiRootModel>() {
                @Override
                public void onResponse(@NonNull Call<ApiRootModel> call, @NonNull Response<ApiRootModel> response) {

                    ApiRootModel user1 = response.body();
                    if (response.isSuccessful()) {
                        if (user1.getStatus() == 200) {
                            userViewModel.updates(user);
                            Toast.makeText(AddUserActivity.this, "Online Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiRootModel> call, @NonNull Throwable t) {
                    Toast.makeText(AddUserActivity.this, "Online Not Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void imagePick() {

        addUserBinding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(AddUserActivity.this)
                        .crop()
                        .cropOval()
                        .maxResultSize(512,512,true)
                        .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                        .createIntentFromDialog(new Function1(){
                            public Object invoke(Object var1){
                                this.invoke((Intent)var1);
                                return Unit.INSTANCE;
                            }
                            public void invoke(@NotNull Intent it){
                                Intrinsics.checkNotNullParameter(it,"it");
                                launcher.launch(it);
                            }
                        });
            }
        });

    }

    private void insertData() {
        addUserBinding.submitBtn.setOnClickListener(v -> {
            byte[] proavatar =  ImageViewToByte(addUserBinding.avatar);
            String name = Objects.requireNonNull(addUserBinding.include2.fullname.getText()).toString();
            String mobile = Objects.requireNonNull(addUserBinding.include2.phone.getText()).toString().trim();
            String email = Objects.requireNonNull(addUserBinding.include2.email.getText()).toString().trim();


            Intent serviceIntent = new Intent(AddUserActivity.this,MyService.class);
            startService(serviceIntent);

            MyService.isOnline(getApplicationContext(), new MyServiceCallback() {
                @Override
                public void onAvailable() {
                    Snackbar.make(addUserBinding.getRoot()," Internet Connected", Snackbar.LENGTH_SHORT).show();
                    insertApi(name, mobile, email, userGender);
                }
                @Override
                public void onLost() {
                    Snackbar.make(addUserBinding.getRoot(),"No Internet Connection", Snackbar.LENGTH_SHORT).show();
                }
            });

            int selectGenderId = addUserBinding.radiogender1.getCheckedRadioButtonId();
            boolean check = validateInfo(name,mobile,email);
            if(selectGenderId <= 0) {
                Toast.makeText(AddUserActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            } else {
                if (check == true) {

                    Intent idash = new Intent(getApplicationContext(), DashBoardActivity.class);
                    idash.putExtra("proavatar", proavatar);
                    idash.putExtra("name", name);
                    idash.putExtra("mobile", mobile);
                    idash.putExtra("email", email);
                    idash.putExtra("gender",userGender);
                    setResult(RESULT_OK, idash);
                    finish();

                    addUserBinding.editBtn.setVisibility(View.GONE);
                    addUserBinding.submitBtn.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(AddUserActivity.this, "Required All field", Toast.LENGTH_SHORT).show();
                }
            }


        });
        addUserBinding.cancleBtn.setOnClickListener(v -> {
            addUserBinding.avatar.setImageResource(R.mipmap.ic_launcher);
            addUserBinding.include2.fullname.setText("");
            addUserBinding.include2.phone.setText("");
            addUserBinding.include2.email.setText("");
            radioButton.clearFocus();
        });
    }

    private void insertApi(String name, String mobile, String email, int userGender) {

        UserApiInterface userApiInterface = RetrofitClientInstance.getRetrofitInstance().create(UserApiInterface.class);
        UserModel user = new UserModel(name, mobile, email, userGender);
        if(user.getCreatedAt() == null) {
            Call<ApiRootModel> call = userApiInterface.addUser(user);
            call.enqueue(new Callback<ApiRootModel>() {
                @Override
                public void onResponse(@NonNull Call<ApiRootModel> call, @NonNull Response<ApiRootModel> response) {
                    ApiRootModel user1 = response.body();
                    if (response.isSuccessful()) {
                        if (user1.getStatus() == 200) {
                            userViewModel.insert(user);
                            Toast.makeText(AddUserActivity.this, "Online Inserted", Toast.LENGTH_SHORT).show();
                        } else if(user1.getStatus() == 400) {
                            userViewModel.delete(user);
                            Toast.makeText(AddUserActivity.this, "Already Exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiRootModel> call, @NonNull Throwable t) {
                    Toast.makeText(AddUserActivity.this, "Online Not Inserted", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    @SuppressLint("NonConstantResourceId")
    public  void RadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.male1:
                if(checked)
                    userGender = 0;
                break;
            case R.id.female1:
                if(checked)
                    userGender = 1;
                break;
        }
    }
    private boolean validateInfo(String name, String mobile, String email) {

        if(name.isEmpty()) {
            addUserBinding.include2.fullname.requestFocus();
            addUserBinding.include2.fullname.setError("Enter Name");
            return false;
        } else if (!name.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")) {
            addUserBinding.include2.fullname.requestFocus();
            addUserBinding.include2.fullname.setError("Please Enter only alphabetical character.");
            return false;
        } else if (mobile.isEmpty()) {
            addUserBinding.include2.phone.requestFocus();
            addUserBinding.include2.phone.setError("Enter Contact No.");
            return false;
        } else if (!mobile.matches("^[0-9]{10}$")) {
            addUserBinding.include2.phone.requestFocus();
            addUserBinding.include2.phone.setError("Please Enter valid phone number");
            return false;
        } else if (email.isEmpty()) {
            addUserBinding.include2.email.requestFocus();
            addUserBinding.include2.email.setError("Enter Email Address");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            addUserBinding.include2.email.requestFocus();
            addUserBinding.include2.email.setError("Please Enter Valid Email Address.");
            return false;
        } else {
            return true;
        }
    }

    private byte[] ImageViewToByte(ImageView avatar) {

        try {
            Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Image Not Enter", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    ActivityResultLauncher<Intent> launcher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                if(result.getResultCode()==RESULT_OK){
                    Uri uri=result.getData().getData();
                    if(uri != null) {
                        addUserBinding.avatar.setImageURI(uri);
                    } else {
                        addUserBinding.avatar.setImageResource(R.drawable.baseline_account_circle_24);
                    }
                }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                    Toast.makeText(this, "Enter Image", Toast.LENGTH_SHORT).show();
                }
            });
}