package com.example.surfacecamera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.surfacecamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SenseAdapter extends RecyclerView.Adapter<SenseAdapter.EffectViewHolder> {
    private LayoutInflater mLayoutInflater;
    private String[] senseArr;

    public SenseAdapter(Context mContext, String[] arr) {
        this.senseArr = arr;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public EffectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EffectViewHolder(mLayoutInflater.inflate(R.layout.item_rv_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EffectViewHolder holder, int position) {
        holder.mTextView.setText(senseArr[position]);
    }

    @Override
    public int getItemCount() {
        return senseArr.length;
    }

    public class EffectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.text_view)
        TextView mTextView;

        EffectViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (senseOnItemClickListener != null) {
                senseOnItemClickListener.itemOnClick(getPosition());
            }
        }
    }

    private SenseOnItemClickListener senseOnItemClickListener;

    public interface SenseOnItemClickListener {

        void itemOnClick(int position);

    }

    public void setSenseOnItemClickListener(SenseOnItemClickListener senseOnItemClickListener) {
        this.senseOnItemClickListener = senseOnItemClickListener;
    }
}
