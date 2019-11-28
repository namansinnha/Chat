public class MessageActivity extends AppCompatActivity {

    //vars:
    CircleImageView profile_image;
    TextView username;
    Toolbar toolbar;
    EditText text_send;
    ImageButton btn_send;
    RecyclerView recyclerView;

    //firebase:
    FirebaseUser fuser;
    DatabaseReference reference;

    //display message
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    //move
    Intent intent;
    ValueEventListener seenListener;

    //Notification
    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_message);

        initialize ();

        setSupportActionBar (toolbar);
        getSupportActionBar ().setTitle ("");
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

        toolbar.setNavigationOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

               startActivity (new Intent (MessageActivity.this, MainActivity.class).setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        apiService = Client.getClient ("https://fcm.googleapis.com/").create (APIService.class);

        intent = getIntent ();
        final String userId = intent.getStringExtra ("userid");

        fuser = FirebaseAuth.getInstance ().getCurrentUser ();
        reference = FirebaseDatabase.getInstance ().getReference ("User").child (userId);

        recyclerView.setHasFixedSize (true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager (getApplicationContext ());
        linearLayoutManager.setStackFromEnd (true);
        recyclerView.setLayoutManager (linearLayoutManager);

        btn_send.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                notify = true;
                String msg = text_send.getText ().toString ();

                if (!msg.equals ("")){

                    sendMessage (fuser.getUid (), userId, msg);

                } else {

                    Toasty.error (getApplicationContext (), "Could not send message", Toasty.LENGTH_SHORT).show ();

                }

                text_send.setText ("");
            }
        });


        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue (User.class);
                username.setText (user.getName ());

                if (user.getImageUrl ().equals ("default")){

                    profile_image.setImageResource (R.drawable.ic_sender_image);

                } else {

                    Glide.with (getApplicationContext ()).load (user.getImageUrl ()).into (profile_image);

                }

                readMessage (fuser.getUid (), userId, user.getImageUrl ());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage (userId);

    }

    private void initialize() {

        toolbar = findViewById (R.id.toolbar_message);
        profile_image = findViewById (R.id.profile_message_image);
        username = findViewById (R.id.details_logged_in);
        btn_send = findViewById (R.id.btn_send);
        text_send = findViewById (R.id.text_send);
        recyclerView = findViewById (R.id.recyclerView);

    }

    private void seenMessage (final String userId) {

        reference = FirebaseDatabase.getInstance ().getReference ("Chats");
        seenListener = reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    Chat chat = snapshot.getValue (Chat.class);

                    if (chat.getReceiver ().equals (fuser.getUid ()) && chat.getSender().equals (userId)) {

                        HashMap<String, Object> hashMap = new HashMap<> ();
                        hashMap.put ("isseen", true);
                        snapshot.getRef ().updateChildren (hashMap);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender, final String receiver, String message) {

        final String userId = intent.getStringExtra ("userid");
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ();

        HashMap<String, Object> hashMap = new HashMap<> ();
        hashMap.put ("sender", sender);
        hashMap.put ("receiver", receiver);
        hashMap.put ("message", message);
        hashMap.put ("isseen", false);

        reference.child("Chats").push().setValue (hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance ().getReference ("Chatlist")
                .child (fuser.getUid ())
                .child (userId);

        chatRef.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists ()) {

                    chatRef.child ("id").setValue (userId);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final String msg = message;
        reference = FirebaseDatabase.getInstance ().getReference ("User").child (fuser.getUid ());
        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue (User.class);
                if (notify) {
                    sendNotification (receiver, user.getName (), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(final String receiver, final String username, final String message) {

        final String userId = intent.getStringExtra ("userid");

        DatabaseReference tokens = FirebaseDatabase.getInstance ().getReference ("Tokens");
        Query query = tokens.orderByKey ().equalTo (receiver);
        query.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    Token token = snapshot.getValue (Token.class);
                    Data data = new Data (fuser.getUid (), R.drawable.ic_boy, username+": "+message, "New Message", userId);


                    Sender sender = new Sender (data, token.getToken ());
                    apiService.sendNotification (sender).enqueue (new Callback<MyResponse> () {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            if (response.code() == 200) {

                                if (response.body ().success != 1) {

                                    Toasty.error (getApplicationContext (), "Failed", Toasty.LENGTH_SHORT).show ();

                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readMessage (final String myid, final String userId, final String imageurl) {

        mchat = new ArrayList<> ();
        reference = FirebaseDatabase.getInstance ().getReference ("Chats");
        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mchat.clear ();
                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    Chat chat = snapshot.getValue (Chat.class);

                    if (chat.getReceiver ().equals (myid) && chat.getSender ().equals (userId) ||
                    chat.getReceiver ().equals (userId) && chat.getSender ().equals (myid)) {

                        mchat.add (chat);

                    }

                    messageAdapter = new MessageAdapter (MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter (messageAdapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser (String userId) {

        SharedPreferences.Editor editor = getSharedPreferences ("PREFS", MODE_PRIVATE).edit ();
        editor.putString ("currentuser", userId);
        editor.apply ();

    }


    private void status(String status) {

        reference = FirebaseDatabase.getInstance ().getReference ("User").child (fuser.getUid ());

        HashMap<String, Object> hashMap = new HashMap<> ();
        hashMap.put ("status", status);

        reference.updateChildren (hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume ();
        final String userId = intent.getStringExtra ("userid");
        status ("online");
        currentUser (userId);
    }

    @Override
    protected void onPause() {
        super.onPause ();
        reference.removeEventListener (seenListener);
        status ("offline");
        currentUser ("none");
    }

}
