package com.abemart.wroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.messages.MessageWrapper;

import java.util.List;


public class ChatAdapter extends ArrayAdapter<MessageWrapper> {

    private WroupDevice currentDevice;

    public ChatAdapter(Context context, List<MessageWrapper> messages, WroupDevice currentDevice) {
        super(context, 0, messages);
        this.currentDevice = currentDevice;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MessageWrapper message = getItem(position);

        boolean isCurrentDeviceMessage = message.getWroupDevice().equals(currentDevice);
        if (isCurrentDeviceMessage) {
            ChatAdapterOwnerHolder chatAdapterOwnerHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.adapter_chat_owner, parent, false);

                chatAdapterOwnerHolder = new ChatAdapterOwnerHolder();
                chatAdapterOwnerHolder.txtViewMessageId = convertView.findViewById(R.id.text_view_message_Id_owner);
                chatAdapterOwnerHolder.txtViewMessage = convertView.findViewById(R.id.text_view_message_owner);
                convertView.setTag(chatAdapterOwnerHolder);
            } else {
                chatAdapterOwnerHolder = (ChatAdapterOwnerHolder) convertView.getTag();
            }

            chatAdapterOwnerHolder.txtViewMessageId.setText(String.valueOf(message.getPipelineObject().getId()));
            chatAdapterOwnerHolder.txtViewMessage.setText(message.getPipelineObject().getName());
        } else {
            ChatAdapterHolder chatAdapterHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_item_object, parent, false);

                chatAdapterHolder = new ChatAdapterHolder();
                chatAdapterHolder.txtViewUsername = convertView.findViewById(R.id.text_device_username);
                chatAdapterHolder.txtViewMessageId = convertView.findViewById(R.id.text_object_id);
                chatAdapterHolder.txtViewMessage = convertView.findViewById(R.id.text_object_name);
                convertView.setTag(chatAdapterHolder);
            } else {
                chatAdapterHolder = (ChatAdapterHolder) convertView.getTag();
            }

            Pipeline pipelineInstance = message.getPipelineObject();
            chatAdapterHolder.txtViewUsername.setText(message.getWroupDevice().getDeviceName());
            chatAdapterHolder.txtViewMessageId.setText(String.valueOf(pipelineInstance.getId()));
            chatAdapterHolder.txtViewMessage.setText(pipelineInstance.getName());
        }

        return convertView;
    }

    private static class ChatAdapterHolder {
        TextView txtViewUsername;
        TextView txtViewMessageId;
        TextView txtViewMessage;
    }

    private static class ChatAdapterOwnerHolder {
        TextView txtViewMessageId;
        TextView txtViewMessage;
    }
}
