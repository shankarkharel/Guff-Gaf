package com.example.guff_gaf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String chatUser;
    private DatabaseReference mRef;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String currentUser;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ImageButton chat_back_button;
    private CircleImageView chat_image;
    private TextView chat_name;
    private TextView chat_last_seen;
    private TextView chat_message;
    private ImageButton chat_add_btn, chat_send_btn;

    //chat activity
    private RecyclerView chat_list;
    private SwipeRefreshLayout swipeRefreshLayout;


    private DatabaseReference chat_ref;
    private String push_id;
    private DatabaseReference mChatRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MessagesAdapter messagesAdapter;
    private RecyclerView UserMessageList;
    private  int currentPage = 1;
    private static final int Total_items_to_load = 10;
    private DatabaseReference messageRef;
    private String messageKey = "";
    private int pos=0;
    private int preItemPose=0;
    public static String lastKey="";
    private String preMessageKey="";

    private ImageView sendImg;
    private StorageReference storageReference;
    //private FirebaseRecyclerAdapter<Messages,MyChatHolder> adapter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menue, menu);
        MenuItem menuItem = menu.findItem(R.id.chat_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
        return true;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        chat_back_button = (ImageButton) findViewById(R.id.chat_back_button);
        chat_image = (CircleImageView) findViewById(R.id.chat_image);
        chat_name = (TextView) findViewById(R.id.chat_name);
        chat_last_seen = (TextView) findViewById(R.id.chat_last_seen);
        chat_send_btn = (ImageButton) findViewById(R.id.chat_send_btn);
        chat_add_btn = (ImageButton) findViewById(R.id.chat_add_btn);
        chat_message = (EditText) findViewById(R.id.chat_message);
        //sendImg=(ImageView)findViewById(R.id.send_img);
        //==========================chat activity==========================================

        messagesAdapter = new MessagesAdapter(messagesList);
        UserMessageList = (RecyclerView) findViewById(R.id.chat_list);
        layoutManager = new LinearLayoutManager(this);
        UserMessageList.setLayoutManager(layoutManager);
        UserMessageList.setAdapter(messagesAdapter);
        mChatRef = FirebaseDatabase.getInstance().getReference();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        chatUser = getIntent().getStringExtra("user_key");
        mRef = FirebaseDatabase.getInstance().getReference();
        messageRef= FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        //onStart();
        mRef.child("user").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    String name = snapshot.child("name").getValue().toString();
                    String Url = snapshot.child("dp").getValue().toString();
                    String last_seen = snapshot.child("online").getValue().toString();
                    //getSupportActionBar().setTitle(name);
                    chat_name.setText(name);
                    Picasso.get().load(Url).placeholder(R.drawable.profile).into(chat_image);
                    if (last_seen.equals("true")) {
                        chat_last_seen.setText("Online");

                    } else {
                        TimeStamp timeStamp = new TimeStamp();
                        long lastTime = Long.parseLong(last_seen);
                        String last_seen_time = timeStamp.getTimeAgo(lastTime, getApplicationContext());
                        chat_last_seen.setText(last_seen_time);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        chat_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(back_intent);
            }
        });


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUser = mAuth.getCurrentUser().getUid();
        }
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(chatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", "false");
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatMap = new HashMap();

                    chatMap.put("chat/" + currentUser + "/" + chatUser, chatAddMap);
                    chatMap.put("chat/" + chatUser + "/" + currentUser, chatAddMap);
                    mRootRef.updateChildren(chatMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            Log.d("databaseError", "onComplete: " + error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       chat_add_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              /* Intent galaIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               startActivityForResult(galaIntent,1000);*/
               Toast.makeText(ChatActivity.this, "Is in Developing phase", Toast.LENGTH_SHORT).show();
           }
       });


        chat_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sentMsg();
                chat_message.setText(null);

            }


        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                pos=0;
                loadMoreMsg();
                swipeRefreshLayout.setRefreshing(false);

            }


        });
loadMsg();
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                //sendImg.setImageURI(uri);
                //uploadToFirebase(uri);
                Log.d("imageUri", "onActivityResult: uri" + uri);
                DatabaseReference message_push = mRootRef.child("messages").child(currentUser).child(chatUser).push();
                push_id = message_push.getKey();

                final StorageReference fileRef = storageReference.child("msg_images/" + push_id + ".jpg");
                fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String Url = uri.toString();
                                    Log.d("url", "onSuccess: Url" + Url);
                                    //createNewPost(imageUrl);
                                    //String Url = fileRef.getDownloadUrl().toString();
                                    String current_user_reference = "messages/" + currentUser + "/" + chatUser;
                                    String chat_user_reference = "messages/" + chatUser + "/" + currentUser;
                                    Map messageMap = new HashMap();
                                    messageMap.put("from", currentUser);
                                    messageMap.put("msg", Url);
                                    messageMap.put("seen", "false");
                                    messageMap.put("type", "image");
                                    messageMap.put("time", ServerValue.TIMESTAMP);
                                    //chat_message.setText("");

                                    //getting push id of msg

                                    DatabaseReference message_push = mRootRef.child("messages").child(currentUser)
                                            .child(chatUser).push();
                                    push_id = message_push.getKey();

                                    Map messageUserMap = new HashMap();
                                    messageUserMap.put(current_user_reference + "/" + push_id, messageMap);
                                    messageUserMap.put(chat_user_reference + "/" + push_id, messageMap);

                                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.d("databaseError", "onComplete: " + error);
                                        }
                                    });


                                }
                            });
                        }
                    }
                });
            }
        }
    }*/
/*    private void uploadToFirebase(Uri uri) {

                                    }
                                });

                            }
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ChatActivity.this, "Uploading___", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "error"+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


*/

    private void loadMoreMsg(){
            messageRef = mChatRef.child("messages").child(currentUser).child(chatUser);

            Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);

            messageQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Messages messages = snapshot.getValue(Messages.class);

                    messageKey = snapshot.getKey();
                    Log.d("key", "onChildAdded: message key" + messageKey);
                    Intent intent = new Intent(ChatActivity.this,FrendsFragment.class);
                    intent.putExtra("push_id",messageKey);
                    startActivity(intent);


                    if (!preMessageKey.equals(messageKey)) {
                        messagesList.add(pos++, messages);

                    } else {
                        preMessageKey = lastKey;
                    }
                    if (pos == 1) {
                        lastKey = messageKey;
                    }
                    messagesAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    layoutManager.scrollToPositionWithOffset(10, 0);
                }


                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    private void loadMsg(){
        //super.onStart();
        messageRef =  mChatRef.child("messages").child(currentUser).child(chatUser);
        Query messageQuery = messageRef.limitToLast(currentPage*Total_items_to_load);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);

                pos++;
                if (pos==1){
                    messageKey=snapshot.getKey();
                    lastKey=messageKey;
                    preMessageKey=messageKey;
                }
                messagesList.add(messages);
                messagesAdapter.notifyDataSetChanged();
                //UserMessageList.scrollToPosition(messagesList.size()-1);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sentMsg(){

        String message = chat_message.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            String current_user_reference = "messages/" + currentUser + "/" + chatUser;
            String chat_user_reference = "messages/" + chatUser + "/" + currentUser;
            Map messageMap = new HashMap();
            messageMap.put("from", currentUser);
            messageMap.put("msg", message);
            messageMap.put("seen", "false");
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);

            //getting push id of msg

            DatabaseReference message_push = mRootRef.child("messages").child(currentUser).child(chatUser).push();
            push_id = message_push.getKey();



            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_reference + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_reference + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Log.d("databaseError", "onComplete: " + error);
                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_delete:
            {
                DatabaseReference message_push = mRootRef.child("messages").child(currentUser).child(chatUser);
                message_push.removeValue();
                Toast.makeText(this, "Removing Msgs...", Toast.LENGTH_LONG).show();

                break;
            }

            case R.id.chat_search:{
                Toast.makeText(this, "Is in development phase", Toast.LENGTH_SHORT).show();

                break;}
            default:
                return true;


        }
        return true;
    }


}


