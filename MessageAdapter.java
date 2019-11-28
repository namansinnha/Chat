public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser fuser;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from (mContext).inflate (R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder (view);

        } else {

            View view = LayoutInflater.from (mContext).inflate (R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder (view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get (position);

        holder.show_message.setText (chat.getMessage ());

        if (imageurl.equals ("default")) {

            holder.profile_image.setImageResource (R.drawable.ic_sender_image);

        } else {

            Glide.with (mContext).load (imageurl).into (holder.profile_image);

        }

        if (position == mChat.size ()-1) {

            if (chat.isIsseen ()) {

                holder.txt_seen.setText ("seen");

            } else {

                holder.txt_seen.setText ("delivered");

            }

        } else {

            holder.txt_seen.setVisibility (View.GONE);

        }


    }

    @Override
    public int getItemCount() {
        return mChat.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public CircleImageView profile_image;
        public TextView txt_seen;


        public ViewHolder(@NonNull View itemView) {
            super (itemView);

            show_message = itemView.findViewById (R.id.show_message);
            profile_image = itemView.findViewById (R.id.pic);
            txt_seen = itemView.findViewById (R.id.txt_seen);
        }
    }


    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance ().getCurrentUser ();
        if (mChat.get (position).getSender ().equals (fuser.getUid ())) {

            return MSG_TYPE_RIGHT;

        } else {

            return MSG_TYPE_LEFT;

        }


    }
}
