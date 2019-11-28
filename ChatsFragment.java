public class ChatsFragment extends Fragment {

    //vars:
    private RecyclerView recyclerView;

    //adapter:
    private UserAdapter userAdapter;

    //list
    private List<User> mUsers;
    private List<ChatList> userList;

    //firebase:
    FirebaseUser fuser;
    DatabaseReference reference;

    public ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate (R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById (R.id.recycler_view);
        recyclerView.setHasFixedSize (true);
        recyclerView.setLayoutManager (new LinearLayoutManager (getContext ()));

        fuser = FirebaseAuth.getInstance ().getCurrentUser ();

        userList = new ArrayList<> ();
        reference = FirebaseDatabase.getInstance ().getReference ("Chatlist").child (fuser.getUid ());
        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear ();
                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    ChatList chatList = snapshot.getValue (ChatList.class);
                    userList.add (chatList);

                }

                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        reference = FirebaseDatabase.getInstance ().getReference ("Chats");
//        reference.addValueEventListener (new ValueEventListener () {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear ();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {
//
//                    Chat chat = snapshot.getValue (Chat.class);
//
//                    if (chat.getSender ().equals (fuser.getUid ())) {
//
//                        userList.add (chat.getReceiver ());
//
//                    } if (chat.getReceiver ().equals (fuser.getUid ())) {
//
//                        userList.add (chat.getSender ());
//
//                    }
//
//                    readChats();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        updateToken (FirebaseInstanceId.getInstance ().getToken ());

        return view;
    }

    private void updateToken (String token) {

        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("Tokens");
        Token token1 = new Token (token);
        reference.child (fuser.getUid ()).setValue (token1);

    }


    private void chatList() {

        mUsers = new ArrayList<> ();
        reference = FirebaseDatabase.getInstance ().getReference ("User");
        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear ();
                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    User user = snapshot.getValue (User.class);
                    for (ChatList chatList : userList) {

                        if (user.getId ().equals (chatList.getId ())) {

                            mUsers.add (user);

                        }

                    }

                }

                userAdapter = new UserAdapter (getContext (), mUsers, true);
                recyclerView.setAdapter (userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    private void readChats () {
//
//        mUsers = new ArrayList<> ();
//
//        reference = FirebaseDatabase.getInstance ().getReference ("User");
//        reference.addValueEventListener (new ValueEventListener () {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear ();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {
//
//                    User user = snapshot.getValue (User.class);
//
//                    //display one user from the chat
//                    for (String id : userList) {
//
//                        if (user.getId ().equals (id)) {
//
//                            if (mUsers.size () != 0) {
//
//                                for (User user1 : mUsers) {
//
//                                    if (!user.getId ().equals (user1.getId ())) {
//
//                                        mUsers.add (user);
//
//                                    }
//
//                                }
//
//                            } else {
//
//                                mUsers.add (user);
//
//                            }
//
//                        }
//
//                    }
//
//                }
//
//                userAdapter = new UserAdapter (getContext (), mUsers, true);
//                recyclerView.setAdapter (userAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

}
