class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        String refreshToken = FirebaseInstanceId.getInstance ().getToken ();

        if (firebaseUser != null) {

            updateToken (refreshToken);

        }

    }

    private void updateToken(String refreshToken) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        DatabaseReference reference = FirebaseDatabase.getInstance ().getReference ("Tokens");
        Token token = new Token (refreshToken);
        reference.child (firebaseUser.getUid ()).setValue (token);

    }


}
