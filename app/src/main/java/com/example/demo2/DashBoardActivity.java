package com.example.demo2;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.demo2.databinding.ActivityDashBoardBinding;
import com.example.demo2.databinding.UserdesignBinding;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import java.util.Objects;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DashBoardActivity extends AppCompatActivity {
    UserAdpter userAdpter;
    private ActivityDashBoardBinding dashBoardBinding;
    private UserViewModel userViewModel;
    private UserApiInterface userApiInterface;
    LiveData<List<UserModel>> vmModelList;
    UserdesignBinding userdesignBinding;
    boolean isSelectAll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardBinding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        userdesignBinding = UserdesignBinding.inflate(getLayoutInflater());
        View view = dashBoardBinding.getRoot();
        setContentView(view);

        Intent serviceIntent = new Intent(this,MyService.class);
        startService(serviceIntent);

        MyService.isOnline(getApplicationContext(), new MyServiceCallback() {
            @Override
            public void onAvailable() {
                Snackbar.make(dashBoardBinding.getRoot()," Internet Connected", Snackbar.LENGTH_SHORT).show();
                clientRequest();
            }
            @Override
            public void onLost() {
                Snackbar.make(dashBoardBinding.getRoot(),"No Internet Connection", Snackbar.LENGTH_SHORT).show();
            }
        });
        dashBoardBinding.addUser.setOnClickListener(v -> {
            Intent adduseri = new Intent(getApplicationContext(), AddUserActivity.class);
            adduseri.putExtra("type", "Add Mode");
            startActivityForResult(adduseri, 1);
        });
        userViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(UserViewModel.class);
        vmModelList = userViewModel.getModelList();
        dashBoardBinding.recycle.setLayoutManager(new LinearLayoutManager(this));
        dashBoardBinding.recycle.setHasFixedSize(true);
        userAdpter = new UserAdpter(new DiffUtil.ItemCallback<UserModel>() {
            @Override
            public boolean areItemsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
                return false;
            }
        }, vmModelList, false);
        dashBoardBinding.recycle.setAdapter(userAdpter);

        vmModelList.observe(this, userModels -> {
            if(userModels.size() == 0) {
                dashBoardBinding.recycle.setVisibility(View.INVISIBLE);
                dashBoardBinding.rvempty.setVisibility(View.VISIBLE);
            } else {
                dashBoardBinding.recycle.setVisibility(View.VISIBLE);
                dashBoardBinding.rvempty.setVisibility(View.INVISIBLE);
                userAdpter.submitList(userModels);
            }

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.RIGHT)
                {
                    new AlertDialog.Builder(viewHolder.itemView.getContext())
                            .setTitle("Delete User")
                            .setMessage("Are you sure want to delete ?")
                            .setIcon(R.drawable.baseline_delete_24)
                            .setCancelable(false)
                                    .setPositiveButton("YES", (dialog, which) -> {
                                      int position = viewHolder.getAdapterPosition();
                                        int id = userAdpter.getUserModel(viewHolder.getAdapterPosition()).getId();

                                        Intent serviceIntent1 = new Intent(DashBoardActivity.this,MyService.class);
                                        startService(serviceIntent1);

                                        MyService.isOnline(getApplicationContext(), new MyServiceCallback() {
                                            @Override
                                            public void onAvailable() {
                                                Snackbar.make(dashBoardBinding.getRoot()," Internet Connected", Snackbar.LENGTH_SHORT).show();
                                                deleteApi(id);
                                            }
                                            @Override
                                            public void onLost() {
                                                Snackbar.make(dashBoardBinding.getRoot(),"No Internet Connection", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                       userViewModel.delete(userAdpter.getUserModel(position));
                                        userAdpter.getUserModel(position).setName(null);
                                        Toast.makeText(DashBoardActivity.this, "Offline Deleted", Toast.LENGTH_SHORT).show();
                                    }).setNegativeButton("NO", (dialog, which) -> userAdpter.notifyItemChanged(viewHolder.getAdapterPosition())).create()
                                    .show();
                }
                else {
                    new AlertDialog.Builder(viewHolder.itemView.getContext())
                            .setTitle("Update User")
                            .setMessage("Are you sure want to Update ?")
                            .setIcon(R.drawable.baseline_edit_24)
                            .setCancelable(false)
                            .setPositiveButton("YES", (dialog, which) -> {
                                Toast.makeText(DashBoardActivity.this, "Offline Updating", Toast.LENGTH_SHORT).show();
                                Intent iupdate = new Intent(DashBoardActivity.this, AddUserActivity.class);
                                iupdate.putExtra("type", "Update");iupdate.putExtra("name", userAdpter.getUserModel(viewHolder.getAdapterPosition()).getName());
                                iupdate.putExtra("mobile", userAdpter.getUserModel(viewHolder.getAdapterPosition()).getMobile());
                                iupdate.putExtra("email", userAdpter.getUserModel(viewHolder.getAdapterPosition()).getEmail());
                                iupdate.putExtra("gender",userAdpter.getUserModel(viewHolder.getAdapterPosition()).getGender());
                                iupdate.putExtra("id", userAdpter.getUserModel(viewHolder.getAdapterPosition()).getId());
                                if(userAdpter.getUserModel(viewHolder.getAdapterPosition()).getProavatar() != null) {
                                    iupdate.putExtra("proavatar", userAdpter.getUserModel(viewHolder.getAdapterPosition()).getProavatar());
                                }
                                startActivityForResult(iupdate, 2);
                            }).setNegativeButton("NO", (dialog, which) -> userAdpter.notifyItemChanged(viewHolder.getAdapterPosition())).create()
                            .show();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.baseline_edit_24)
                        .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeLeftBackgroundColor(R.color.red)
                        .addSwipeRightBackgroundColor(R.color.purple_700)
                        .create()
                        .decorate();

               super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(dashBoardBinding.recycle);
    }
    private void deleteApi(int id) {
        UserModel user = new UserModel(id);
        userApiInterface = RetrofitClientInstance.getRetrofitInstance().create(UserApiInterface.class);
        if(user.getName() == null) {
            Call<ApiRootModel> call = userApiInterface.deleteUser(user);
            call.enqueue(new Callback<ApiRootModel>() {
                @Override
                public void onResponse(@NonNull Call<ApiRootModel> call, @NonNull Response<ApiRootModel> response) {
                    ApiRootModel user1 = response.body();
                    if (response.isSuccessful()) {
                        if (user1.getStatus() == 200) {
                            userViewModel.delete(user);
                            Toast.makeText(DashBoardActivity.this, "Online Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiRootModel> call, @NonNull Throwable t) {
                    Toast.makeText(DashBoardActivity.this, "Online Not deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void clientRequest() {
     
        userApiInterface = RetrofitClientInstance.getRetrofitInstance().create(UserApiInterface.class);

        Call<ApiResultModel> call = userApiInterface.getAllUsers();
        call.enqueue(new Callback<ApiResultModel>() {
            @Override
            public void onResponse(@NonNull Call<ApiResultModel> call, @NonNull Response<ApiResultModel> response) {
                Log.d("TAG", "onResponse: " + response.body().getData());
                List<UserModel> users = response.body().getData();

                if (users != null){
                    users.forEach(userModel -> {
                        userViewModel.insert(userModel);
                        Toast.makeText(DashBoardActivity.this, "API Data Sync", Toast.LENGTH_SHORT).show();

                       /* try to delete duplicate data from local data
                       Objects.requireNonNull(vmModelList.getValue()).forEach(userModel1 -> {
                            index = vmModelList.getValue().indexOf(userModel);
                            int insertId = vmModelList.getValue().get(index).getId();
                            if (userModel.getId() != userModel1.getId()){
                                userViewModel.delete(userModel1);
                            }
                            Log.d("InsertID", "insertId: " + insertId);
                        });*/
                    });
                }
                else {
                    Objects.requireNonNull(vmModelList.getValue()).forEach(user -> {
                        index = vmModelList.getValue().indexOf(user);// vmmodel = room list user
                            userViewModel.delete(user);
                    });
                }
                Objects.requireNonNull(vmModelList.getValue()).forEach(userModel-> {
                    UserModel userModel1 = new UserModel(userModel.getName(), userModel.getMobile(), userModel.getEmail(), userModel.getGender());
                    UserModel userModel2 = new UserModel(userModel.getId(),userModel.getName(), userModel.getMobile(), userModel.getEmail(), userModel.getGender());
                    UserModel userModel3 = new UserModel(userModel.getId());

                    if(userModel.getCreatedAt() == null) {
                        Call<ApiRootModel> call1 = userApiInterface.addUser(userModel1);
                        call1.enqueue(new Callback<ApiRootModel>() {
                            @Override
                            public void onResponse(@NonNull Call<ApiRootModel> call, @NonNull Response<ApiRootModel> response) {
                                ApiRootModel user1 = response.body();
                                if(response.isSuccessful()) {
                                    if(user1.getStatus() == 200) {
                                        userViewModel.insert(userModel1);
                                        Toast.makeText(DashBoardActivity.this, "User added online successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DashBoardActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ApiRootModel> call, @NonNull Throwable t) {
                                Toast.makeText(DashBoardActivity.this, "Online Not Inserted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (userModel.getUpdatedAt() == null) {
                        Call<ApiRootModel> call2 = userApiInterface.updateUser(userModel2);
                        call2.enqueue(new Callback<ApiRootModel>() {
                            @Override
                            public void onResponse(@NonNull Call<ApiRootModel> call, @NonNull Response<ApiRootModel> response) {
                                ApiRootModel user1 = response.body();
                                if(response.isSuccessful()) {
                                    if(user1.getStatus() == 200) {
                                        userViewModel.updates(userModel2);
                                        Toast.makeText(DashBoardActivity.this, "User details updated online successfully", Toast.LENGTH_SHORT).show();
                                    } else if (user1.getStatus() == 400){
                                        Toast.makeText(DashBoardActivity.this, "Invalid Argument(s)", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ApiRootModel> call, @NonNull Throwable t) {
                                Toast.makeText(DashBoardActivity.this, "Online Not Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if(userModel.getName() == null) {
                        Call<ApiRootModel> call3 = userApiInterface.deleteUser(userModel3);
                        call3.enqueue(new Callback<ApiRootModel>() {
                            @Override
                            public void onResponse(@NonNull Call<ApiRootModel> call, @NonNull Response<ApiRootModel> response) {
                                ApiRootModel user1 = response.body();
                                if (response.isSuccessful()) {
                                    assert user1 != null;
                                    if (Objects.equals(user1.getStatus(), 200)) {
                                        userViewModel.delete(userModel3);
                                        Toast.makeText(DashBoardActivity.this, "User deleted online successfully", Toast.LENGTH_SHORT).show();
                                    } else if (Objects.equals(user1.getStatus(), 400)) {
                                        Toast.makeText(DashBoardActivity.this, "Invalid Argument(s).", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<ApiRootModel> call, @NonNull Throwable t) {
                                Toast.makeText(DashBoardActivity.this, "Online Not deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ApiResultModel> call, @NonNull Throwable t) {
                Toast.makeText(DashBoardActivity.this, "API data not sync", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static Menu mMenu = null;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }
    public static Menu getMenu()
    {
        return mMenu;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            byte[] avatar = data.getByteArrayExtra("proavatar");
            String name = data.getStringExtra("name");
            String mobile = data.getStringExtra("mobile");
            String email = data.getStringExtra("email");
            int gender = data.getIntExtra("gender",0);
            UserModel userModel = new UserModel(avatar,name,mobile,email,gender);
            String localInsertId = userModel.getCreatedAt();
            Toast.makeText(this, "Local Insert time : "+localInsertId, Toast.LENGTH_SHORT).show();
            userModel.setCreatedAt(null);
            userViewModel.insert(userModel);
            Toast.makeText(this, "Offline Inserted Successfully", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 2) {
                byte[] avatar = data.getByteArrayExtra("proavatar");
                String name = data.getStringExtra("name");
                String mobile = data.getStringExtra("mobile");
                String email = data.getStringExtra("email");
                int gender = data.getIntExtra("gender",0);
                UserModel userModel = new UserModel(avatar,name,mobile,email,gender);
                userModel.setId(data.getIntExtra("id",0));
                userModel.setUpdatedAt(null);
                userViewModel.updates(userModel);
                Toast.makeText(this, "Offline Updated Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    int index;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuitem,menu);

        MenuItem selectAll = menu.findItem(R.id.selectall);
        MenuItem deSelectAll = menu.findItem(R.id.deSelectall);
        selectAll.setOnMenuItemClickListener(item -> {
                userAdpter = new UserAdpter(new DiffUtil.ItemCallback<UserModel>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
                        return false;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
                        return false;
                    }
                }, vmModelList, true);

                dashBoardBinding.recycle.setAdapter(userAdpter);
                vmModelList.observe(DashBoardActivity.this, userModels -> {
                    if (userModels.size() == 0) {
                        dashBoardBinding.recycle.setVisibility(View.INVISIBLE);
                        dashBoardBinding.rvempty.setVisibility(View.VISIBLE);
                    } else {
                        dashBoardBinding.recycle.setVisibility(View.VISIBLE);
                        dashBoardBinding.rvempty.setVisibility(View.INVISIBLE);
                        userAdpter.submitList(userModels);
                    }
                });
                Objects.requireNonNull(vmModelList.getValue()).forEach(userModel -> {
                    index = vmModelList.getValue().indexOf(userModel);
                    vmModelList.getValue().get(index).setSelected(true);
                    userdesignBinding.checkRight.setVisibility(View.VISIBLE);
                    Log.d("TAG", "onOptionsItemSelected: " + vmModelList.getValue().get(index).getSelected());
                });
                selectAll.setVisible(false);
                deSelectAll.setVisible(true);
                return true;
        });

        deSelectAll.setOnMenuItemClickListener(item -> {
            userAdpter = new UserAdpter(new DiffUtil.ItemCallback<UserModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
                    return false;
                }

                @Override
                public boolean areContentsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
                    return false;
                }
            }, vmModelList, false);
            dashBoardBinding.recycle.setAdapter(userAdpter);
            vmModelList.observe(DashBoardActivity.this, userModels -> {
                if (userModels.size() == 0) {
                    dashBoardBinding.recycle.setVisibility(View.INVISIBLE);
                    dashBoardBinding.rvempty.setVisibility(View.VISIBLE);
                } else {
                    dashBoardBinding.recycle.setVisibility(View.VISIBLE);
                    dashBoardBinding.rvempty.setVisibility(View.INVISIBLE);
                    userAdpter.submitList(userModels);
                }

            });
            Objects.requireNonNull(vmModelList.getValue()).forEach(userModel -> {
                index = vmModelList.getValue().indexOf(userModel);
                vmModelList.getValue().get(index).setSelected(false);
                userdesignBinding.checkRight.setVisibility(View.GONE);
                Log.d("TAG", "onOptionsItemSelected: " + vmModelList.getValue().get(index).getSelected());
            });
            deSelectAll.setVisible(false);
            return true;
        });

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
//        TextView textView = (TextView) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                vmModelList= userViewModel.getSearchList(query);
                vmModelList.observe(DashBoardActivity.this, userModels -> userAdpter.submitList(userModels));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                int labelColor = getResources().getColor(R.color.red);
//                String сolorString = String.format("%X", labelColor).substring(1); // !!strip alpha value!!
//                String styledText = "<font color=\"#%s\">newText</font>";
//                textView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
//                Html.fromHtml((styledText), (сolorString), TextView.BufferType.SPANNABLE);
                vmModelList= userViewModel.getSearchList(newText);
                vmModelList.observe(DashBoardActivity.this, userModels -> {
                    if(userModels.size() == 0) {
                        //searchView.clearFocus();
                        dashBoardBinding.recycle.setVisibility(View.INVISIBLE);
                        dashBoardBinding.rvempty1.setVisibility(View.VISIBLE);
                    } else {
                        dashBoardBinding.rvempty1.setVisibility(View.INVISIBLE);
                        dashBoardBinding.recycle.setVisibility(View.VISIBLE);

                        userAdpter.submitList(userModels);
                        /* try to search text highlighted
                        String text = textView.getText().toString();
                        SpannableString modify = new SpannableString(text);
                        Pattern pattern = Pattern.compile(newText, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(modify);
                        while (matcher.find()) {
                            int startIndex = matcher.start();
                            int endIndex = matcher.end();
                            modify.setSpan(new BackgroundColorSpan(Color.parseColor("#FF6200EE")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        textView.setText(modify);*/
                    }
                });
                return true;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();

        switch (item_id) {
            case R.id.theme:
                Intent settingi = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(settingi);
                break;
            case R.id.logoutBtn:
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent logouti = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(logouti);
                break;
            case R.id.sort1:
                vmModelList= userViewModel.getAscList();
                vmModelList.observe(this, userModels -> {
                    dashBoardBinding.recycle.smoothScrollToPosition(0);
                    userAdpter.submitList(userModels);
                });
                break;
            case R.id.sort2:
                vmModelList= userViewModel.getDescList();
                vmModelList.observe(this, userModels -> {
                    dashBoardBinding.recycle.smoothScrollToPosition(0);
                    userAdpter.submitList(userModels);
                });
                break;
            case R.id.sort3:
                vmModelList= userViewModel.getLastInsertedList();
                vmModelList.observe(this, userModels -> {
                    dashBoardBinding.recycle.smoothScrollToPosition(0);
                    userAdpter.submitList(userModels);
                });
                break;
            case R.id.sort4:
                vmModelList= userViewModel.getLastModifiedList();
                vmModelList.observe(this, userModels -> {
                    dashBoardBinding.recycle.smoothScrollToPosition(0);
                    userAdpter.submitList(userModels);
                });
                break;
            case R.id.deleteall:
                if(isSelectAll){
                    Intent serviceIntent = new Intent(DashBoardActivity.this,MyService.class);
                    startService(serviceIntent);

                    MyService.isOnline(getApplicationContext(), new MyServiceCallback() {
                        @Override
                        public void onAvailable() {
                            Snackbar.make(dashBoardBinding.getRoot()," Internet Connected", Snackbar.LENGTH_SHORT).show();
                            Objects.requireNonNull(vmModelList.getValue()).forEach(userModel -> {
                                index = vmModelList.getValue().indexOf(userModel);
                                if(vmModelList.getValue().get(index).getSelected()) {
                                    deleteApi(vmModelList.getValue().get(index).getId());
                                }
                            });

                        }
                        @Override
                        public void onLost() {
                            Snackbar.make(dashBoardBinding.getRoot(),"No Internet Connection", Snackbar.LENGTH_SHORT).show();

                        }
                    });
                    Objects.requireNonNull(vmModelList.getValue()).forEach(userModel -> {
                        index = vmModelList.getValue().indexOf(userModel);
                        if(vmModelList.getValue().get(index).getSelected()) {
                            userViewModel.delete(userAdpter.getUserModel(index));
                            userAdpter.getUserModel(index).setName(null);
                            vmModelList.observe(DashBoardActivity.this, userModels -> userAdpter.submitList(userModels));
                        }
                    });

                } else {
                    Intent serviceIntent = new Intent(DashBoardActivity.this,MyService.class);
                    startService(serviceIntent);

                    MyService.isOnline(getApplicationContext(), new MyServiceCallback() {
                        @Override
                        public void onAvailable() {
                            Snackbar.make(dashBoardBinding.getRoot()," Internet Connected", Snackbar.LENGTH_SHORT).show();
                            Objects.requireNonNull(vmModelList.getValue()).forEach(userModel -> {
                                index = vmModelList.getValue().indexOf(userModel);
                                Log.d("deleteall", "onAvailable: "+index);
                                if(vmModelList.getValue().get(index).getSelected()) {
                                    deleteApi(vmModelList.getValue().get(index).getId());
                                }
                            });
                        }
                        @Override
                        public void onLost() {
                            Snackbar.make(dashBoardBinding.getRoot(),"No Internet Connection", Snackbar.LENGTH_SHORT).show();

                        }
                    });
                    Objects.requireNonNull(vmModelList.getValue()).forEach(userModel -> {
                        index = vmModelList.getValue().indexOf(userModel);
                        if (vmModelList.getValue().get(index).getSelected()) {
                            userViewModel.delete(userAdpter.getUserModel(index));
                            userAdpter.getUserModel(index).setName(null);
                            vmModelList.observe(DashBoardActivity.this, userModels -> userAdpter.submitList(userModels));
                            userdesignBinding.checkRight.setVisibility(View.GONE);
                        }
                    });

                }
                break;
        }
        return true;
    }
}
