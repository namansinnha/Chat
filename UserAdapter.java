public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    //vars:
    private Context mContext;
    private List<User> mDetails;
    private boolean isChat;
    String theLastMessage;

    public UserAdapter(Context mContext, List<User> mDetails, boolean isChat) {
        this.mContext = mContext;
        this.mDetails = mDetails;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from (mContext).inflate (R.layout.user_layout, parent, false);
        return new UserAdapter.ViewHolder (view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mDetails.get (position);
        holder.userName.setText (user.getName ());

        if (user.getImageUrl ().equals ("default")) {

            holder.profile_image.setImageResource (R.drawable.ic_boy);

        } else {

            Glide.with (mContext).load (user.getImageUrl ()).into (holder.profile_image);

        }

        if (isChat) {

            lastMessage (user.getId (), holder.last_msg);

        } else {

            holder.last_msg.setVisibility (View.GONE);

        }

        if (isChat) {

            if (user.getStatus ().equals ("online")) {

                holder.img_on.setVisibility (View.VISIBLE);
                holder.img_off.setVisibility (View.GONE);

            } else {

                holder.img_off.setVisibility (View.VISIBLE);
                holder.img_on.setVisibility (View.GONE);

            }

        } else {

            holder.img_off.setVisibility (View.GONE);
            holder.img_on.setVisibility (View.GONE);


        }

        holder.itemView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (mContext, MessageActivity.class);
                intent.putExtra ("userid", user.getId ());
                mContext.startActivity (intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDetails.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;


        public ViewHolder(@NonNull View itemView) {
            super (itemView);

            userName = itemView.findViewById (R.id.user_name);
            profile_image = itemView.findViewById (R.id.profileImage);
            img_off = itemView.findViewById (R.id.img_off);
            img_on = itemView.findViewById (R.id.img_on);
            last_msg = itemView.findViewById (R.id.last_msg);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg) {

        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("Chats");

        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {

                    Chat chat = snapshot.getValue (Chat.class);
                    if (chat.getReceiver ().equals (firebaseUser.getUid ()) && chat.getSender ().equals (userid) ||
                            chat.getReceiver ().equals (userid) && chat.getSender ().equals (firebaseUser.getUid ())){

                            theLastMessage = chat.getMessage ();

                    }

                }

                switch (theLastMessage) {

                    case "default" :
                        last_msg.setText ("No new Message");
                        break;

                    default:
                        last_msg.setText (theLastMessage);
                        break;

                }

                theLastMessage  = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
