public class ProfileFragment extends Fragment {

    //vars:
    CircleImageView image_profile;
    TextView username;

    //firebase:
    DatabaseReference reference;
    FirebaseUser fuser;
    StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageuri;
    private StorageTask uploadTask;


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate (R.layout.fragment_profile, container, false);

        image_profile = view.findViewById (R.id.profile_image);
        username = view.findViewById (R.id.username);

        storageReference = FirebaseStorage.getInstance ().getReference ("Uploads");

        fuser = FirebaseAuth.getInstance ().getCurrentUser ();
        reference = FirebaseDatabase.getInstance ().getReference ("User").child (fuser.getUid ());

        reference.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue (User.class);
                username.setText (user.getName ());
                if (user.getImageUrl ().equals("default")){
                    image_profile.setImageResource(R.drawable.ic_boy);
                } else {
                    Glide.with(getContext()).load(user.getImageUrl ()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_profile.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                openImage();

            }
        });

        return view;
    }

    private void openImage () {

        Intent intent = new Intent ();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);
        startActivityForResult (intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContext ().getContentResolver ();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton ();
        return mimeTypeMap.getExtensionFromMimeType (contentResolver.getType (uri));

    }

    private void uploadImage () {

        final ProgressDialog progressDialog = new ProgressDialog (getContext ());
        progressDialog.setMessage (" Uploading... ");
        progressDialog.show ();

        if (imageuri != null) {

            final StorageReference fileReference = storageReference.child (System.currentTimeMillis ()
             + "."+getFileExtension (imageuri));

            uploadTask = fileReference.putFile (imageuri);

            uploadTask.continueWithTask (new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task ) throws Exception {

                    if (!task.isSuccessful ()) {

                        throw task.getException ();

                    }

                    return fileReference.getDownloadUrl ();
                }
            }).addOnCompleteListener (new OnCompleteListener<Uri> () {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful ()) {

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString ();

                        reference = FirebaseDatabase.getInstance ().getReference ("User").child (fuser.getUid ());

                        HashMap<String, Object> map = new HashMap<> ();
                        map.put ("imageUrl", mUri);
                        reference.updateChildren (map);

                        progressDialog.dismiss ();

                    } else {

                        Toasty.error (getContext (), "Failed to update", Toasty.LENGTH_SHORT).show ();
                        progressDialog.dismiss ();

                    }

                }
            }).addOnFailureListener (new OnFailureListener () {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toasty.error (getContext (), e.getMessage (), Toasty.LENGTH_SHORT).show ();

                }
            });

        } else {

            Toasty.info (getContext (), "No Image selected", Toasty.LENGTH_SHORT).show ();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData () != null) {

            imageuri = data.getData ();

            if (uploadTask != null && uploadTask.isInProgress ()){

                Toasty.info (getContext (), "Upload in progress", Toasty.LENGTH_SHORT).show ();

            } else {

                uploadImage ();
            }


        }

    }
}
