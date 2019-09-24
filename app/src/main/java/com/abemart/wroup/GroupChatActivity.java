package com.abemart.wroup;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.abemart.wroup.client.WroupClient;
import com.abemart.wroup.common.WiFiP2PInstance;
import com.abemart.wroup.common.WroupDevice;
import com.abemart.wroup.common.listeners.ClientConnectedListener;
import com.abemart.wroup.common.listeners.ClientDisconnectedListener;
import com.abemart.wroup.common.listeners.DataReceivedListener;
import com.abemart.wroup.common.messages.MessageWrapper;
import com.abemart.wroup.service.WroupService;

import java.util.ArrayList;


public class GroupChatActivity extends AppCompatActivity implements DataReceivedListener, ClientConnectedListener, ClientDisconnectedListener {

    public static final String EXTRA_GROUP_NAME = "groupNameExtra";
    public static final String EXTRA_IS_GROUP_OWNER = "isGroupOwnerExtra";

    private ListView listViewChat;
    private ChatAdapter chatAdapter;

    private String groupName;
    private boolean isGroupOwner = false;

    private WroupService wroupService;
    private WroupClient wroupClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipeline);

        chatAdapter = new ChatAdapter(getApplicationContext(), new ArrayList<MessageWrapper>(), WiFiP2PInstance.getInstance(getApplicationContext()).getThisDevice());

        Intent startIntent = getIntent();
        groupName = startIntent.getStringExtra(EXTRA_GROUP_NAME);
        isGroupOwner = startIntent.getBooleanExtra(EXTRA_IS_GROUP_OWNER, false);

        if (isGroupOwner) {
            wroupService = WroupService.getInstance(getApplicationContext());
            wroupService.setDataReceivedListener(this);
            wroupService.setClientDisconnectedListener(this);
            wroupService.setClientConnectedListener(this);
        } else {
            wroupClient = WroupClient.getInstance(getApplicationContext());
            wroupClient.setDataReceivedListener(this);
            wroupClient.setClientDisconnectedListener(this);
            wroupClient.setClientConnectedListener(this);
        }

        listViewChat = (ListView) findViewById(R.id.list_view_objects);
        Button btnSend = (Button) findViewById(R.id.btn_send);
        final EditText editObjectID = (EditText) findViewById(R.id.object_id_input);
        final EditText editObjectName = (EditText) findViewById(R.id.object_name_input);

        listViewChat.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editObjectID.getText().toString();
                final Pipeline newPipelineObject = new Pipeline(Integer.parseInt(id), editObjectName.getText().toString());
                if (newPipelineObject != null) {
                    MessageWrapper normalMessage = new MessageWrapper();
                    normalMessage.setPipelineObject(newPipelineObject);
                    normalMessage.setMessageType(MessageWrapper.MessageType.NORMAL);

                    if (isGroupOwner) {
                        wroupService.sendMessageToAllClients(normalMessage);
                    } else {
                        wroupClient.sendMessageToAllClients(normalMessage);
                    }

                    chatAdapter.add(normalMessage);
                    editObjectID.setText("");
                    editObjectName.setText("");
                }
            }
        });

        setActionBarTitle(groupName);
        setTitle(groupName);
    }

    private void setActionBarTitle(String title) {
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
    }

    @Override
    public void onDataReceived(final MessageWrapper messageWrapper) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.add(messageWrapper);
            }
        });
    }

    @Override
    public void onClientConnected(final WroupDevice wroupDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.device_connected, wroupDevice.getDeviceName()), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClientDisconnected(final WroupDevice wroupDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.device_disconnected, wroupDevice.getDeviceName()), Toast.LENGTH_LONG).show();
            }
        });
    }
}
