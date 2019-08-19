package com.genius.crudsec.Helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.genius.crudsec.Encapsulation.Profile;
import com.genius.crudsec.R;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private Context mCtx;
    private List<Profile> profileList;

    public ProfileAdapter(Context mCtx, List<Profile> profileList) {
        this.mCtx = mCtx;
        this.profileList = profileList;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.profile_list, null);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        Profile profile = profileList.get(position);

        //loading the image
        Glide.with(mCtx)
                .load(profile.getmAvatar())
                .into(holder.imageView);

        holder.txtId.setText(String.valueOf(profile.getId()));
        holder.txtName.setText(profile.getmName());
        holder.txtClass.setText(profile.getmClass());
        holder.txtSchool.setText(profile.getmSchool());
        holder.txtEmail.setText(profile.getmEmail());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder{

        TextView txtName, txtClass, txtSchool, txtEmail, txtId;
        ImageView imageView;

        public ProfileViewHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.txtId);
            txtName = itemView.findViewById(R.id.txtName);
            txtClass = itemView.findViewById(R.id.txtClass);
            txtSchool = itemView.findViewById(R.id.txtSchool);
            txtEmail = itemView.findViewById(R.id.txtEmail);

            imageView = itemView.findViewById(R.id.imageView);


        }
    }
}
