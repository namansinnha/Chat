public class UsersFragment extends Fragment {

    //vars:
    private RecyclerView recyclerView;
    private EditText search_users;

    //includes
    private UserAdapter userAdapter;
    private List<User> mUsers;

    public UsersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate (R.layout.fragment_users, container, false);

        recyclerView = view.findViewById (R.id.recycler_view);
        recyclerView.setHasFixedSize (true);
        recyclerView.setLayoutManager (new LinearLayoutManager (getContext ()));

        mUsers = new ArrayList<> ();

        readUsers ();

        search_users = view.findViewById (R.id.search_user);
        search_users.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchUsers (s.toString ().toLowerCase ());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;

    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance ().getCurrentUser ();
        Query query = FirebaseDatabase.getInstance ().getReference ("User").orderByChild ("search")
                .startAt (s)
                .endAt (s + "\uf8ff");

        query.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear ();

                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    User user = snapshot.getValue (User.class);

                    assert user != null;
                    assert fuser != null;

                    if (!user.getId ().equals (fuser.getUid ())) {

                        mUsers.add (user);

                    }

                }

                userAdapter = new UserAdapter (getContext (), mUsers, false);
                recyclerView.setAdapter (userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("User");

        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (search_users.getText ().toString ().equals ("")) {


                    mUsers.clear ();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                        User user = snapshot.getValue (User.class);

                        assert user != null;
                        assert firebaseUser != null;

                        if (!user.getId ().equals (firebaseUser.getUid ())) {

                            mUsers.add (user);

                        }
                    }

                    userAdapter = new UserAdapter (getContext (), mUsers, false);
                    recyclerView.setAdapter (userAdapter);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
