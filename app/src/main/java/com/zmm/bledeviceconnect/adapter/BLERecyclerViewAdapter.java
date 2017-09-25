package com.zmm.bledeviceconnect.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmm.bledeviceconnect.R;
import com.zmm.bledeviceconnect.model.BleDevice;
import com.zmm.bledeviceconnect.utils.UIUtils;

import java.util.List;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/6/28
 * Time:下午1:49
 */

public class BLERecyclerViewAdapter extends RecyclerView.Adapter<BLERecyclerViewAdapter.DeviceRecyclerViewHolder>   {

    private List<BleDevice> deviceList;
    private OnItemClickListener mOnItemClickListener;

    public BLERecyclerViewAdapter(List<BleDevice> deviceNames) {
        this.deviceList = deviceNames;
    }

    public interface OnItemClickListener{
        void OnClickListener(View view, int index);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public DeviceRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_device_register, parent, false);
        return new DeviceRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeviceRecyclerViewHolder holder, final int position) {

        holder.setData(position);
        holder.mDeviceItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                if(mOnItemClickListener != null){
                    mOnItemClickListener.OnClickListener(view,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }


    public class DeviceRecyclerViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mDeviceItem;
        private TextView mDeviceId;

        public DeviceRecyclerViewHolder(View itemView) {
            super(itemView);
            mDeviceId = itemView.findViewById(R.id.tv_device_id);
            mDeviceItem = itemView.findViewById(R.id.ll_device_item);

        }

        public void setData(int position) {
            itemView.setTag(position);
            mDeviceId.setText(deviceList.get(position).getName());
        }
    }




}
