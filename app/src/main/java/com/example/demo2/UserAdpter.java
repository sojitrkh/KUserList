package com.example.demo2;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Ignore;

import com.example.demo2.databinding.UserdesignBinding;
import java.util.List;
import java.util.Objects;

public class UserAdpter extends ListAdapter<UserModel,UserAdpter.ViewHolder> {

    MutableLiveData<List<UserModel>> mVmModelList = new MutableLiveData<>();
    LiveData<List<UserModel>> vmModelList = mVmModelList;
    boolean isSelectAll = false;

    public UserAdpter(@NonNull DiffUtil.ItemCallback<UserModel> diffCallback, LiveData<List<UserModel>> vmModelList, boolean isSelectAll) {
        super(diffCallback);
        this.vmModelList = vmModelList;
        this.isSelectAll = isSelectAll;
    }

    @Ignore
    public UserAdpter() {
        super(CALLBACK);
    }
    private static final DiffUtil.ItemCallback<UserModel> CALLBACK = new DiffUtil.ItemCallback<UserModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getMobile().equals(newItem.getMobile()) &&
                    oldItem.getEmail().equals(newItem.getEmail()) &&
                    oldItem.getGender()==newItem.getGender();

        }
    };

    @NonNull
    @Override
    public UserAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userdesign,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        UserModel userModel = getUserModel(position);
        byte[] image = userModel.getProavatar();
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            holder.designBinding.viewavatar.setImageBitmap(bitmap);
        } else {
            holder.designBinding.viewavatar.setImageResource(R.drawable.baseline_account_circle_24);
        }
        holder.designBinding.viewname.setText(userModel.getName());
        holder.designBinding.viewphone.setText(userModel.getMobile());
        holder.designBinding.viewemail.setText(userModel.getEmail());
        if (userModel.getGender() == 0) {
            holder.designBinding.viewgender.setText("Male");
        } else {
            holder.designBinding.viewgender.setText("Female");
        }
        Objects.requireNonNull(vmModelList.getValue()).forEach(userModel1 -> {
            if (userModel1.getSelected()){
                holder.designBinding.checkRight.setVisibility(View.VISIBLE);
            } else {
                holder.designBinding.checkRight.setVisibility(View.GONE);
                Menu menu =DashBoardActivity.getMenu();
                menu.findItem(R.id.deSelectall).setVisible(false);
            }
        });

        if(isSelectAll) {
            holder.designBinding.checkRight.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnLongClickListener(v -> {
            vmModelList.getValue().get(position).setSelected(true);
            holder.designBinding.checkRight.setVisibility(View.VISIBLE);
            Menu menu =DashBoardActivity.getMenu();
            menu.findItem(R.id.selectall).setVisible(true);
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Swipe Left: Update \n Swipe Right: Delete", Toast.LENGTH_SHORT).show();
            boolean isEnable = vmModelList.getValue().get(position).getSelected();
            if (isEnable) {
                holder.designBinding.checkRight.setVisibility(View.GONE);
                vmModelList.getValue().get(position).setSelected(false);
                Menu menu =DashBoardActivity.getMenu();
                menu.findItem(R.id.selectall).setVisible(false);
            }
        });
    }

    public UserModel getUserModel(int position)
    {
        return getItem(position);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        UserdesignBinding designBinding;
        public MenuItem selectall, deSelectall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            designBinding = UserdesignBinding.bind(itemView);
            selectall = itemView.findViewById(R.id.selectall);
            deSelectall = itemView.findViewById(R.id.deSelectall);

        }
    }



}
