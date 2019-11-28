public class RegisterActivity extends AppCompatActivity {

    //vars
    private TextInputEditText register_email, register_password, register_name, register_phonenumber;
    private AppCompatTextView already_created_account;
    private Button registerButton;
    private Toolbar registerToolbar;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;

    //Widget
    private ProgressDialog mProgressDialog;

    //regex
    String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    //variables
    String name, email, password, phonenumber;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register);


        mFirebaseAuth = FirebaseAuth.getInstance ();
        mFirebaseUser = FirebaseAuth.getInstance ().getCurrentUser ();
        mProgressDialog = new ProgressDialog (this);

        initializeVars ();
        settingToolbar ();

        register_password.setTransformationMethod (new AsteriskPasswordTransformationMethod ());

        already_created_account.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                startActivity (new Intent (RegisterActivity.this, LoginActivity.class));

            }
        });

        registerButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                final String email = register_email.getText ().toString ().trim ();
                final String password = register_password.getText ().toString ().trim ();

                if (validateEntries ()) {

                    mProgressDialog.setMessage ("Please wait you are getting registered");
                    mProgressDialog.show ();

                    mFirebaseAuth.createUserWithEmailAndPassword (email, password).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful ()) {

                                mProgressDialog.dismiss ();
                                sendDetailsToDatabase ();
                                startActivity (new Intent (RegisterActivity.this, LoginActivity.class));

                            } else {

                                mProgressDialog.dismiss ();
                                Toasty.error (getApplicationContext (), task.getException ().getMessage (), Toasty.LENGTH_SHORT).show ();

                            }

                        }
                    });

                }

            }
        });
    }

    private void initializeVars() {

        registerToolbar = findViewById (R.id.toolbar_login);
        register_email = findViewById (R.id.register_username);
        register_password = findViewById (R.id.register_password);
        register_name = findViewById (R.id.register_name);
        register_phonenumber = findViewById (R.id.register_phonenumber);
        registerButton = findViewById (R.id.registerButton);
        already_created_account = findViewById (R.id.already_has_account);

    }

    private void settingToolbar() {

        registerToolbar.setTitle ("Register");
        registerToolbar.setSubtitle ("to connect with application");
    }

    private boolean validateEntries() {

        boolean result;

        name = register_name.getText ().toString ().trim ();
        email = register_email.getText ().toString ().trim ();
        password = register_password.getText ().toString ().trim ();
        phonenumber = register_phonenumber.getText ().toString ().trim ();

        if ((!validateEmail ()) || (!validatePassword ()) || (!validatePhoneNumber ()) || (!validateName ())) {

            result = false;

        } else {

            result = true;

        }


        return result;
    }

    private boolean validateEmail() {

        boolean result;

        String email = register_email.getText ().toString ().trim ();

        if (email.matches (emailPattern) && email.length () > 0) {

            result = true;

        } else {
            register_email.setError ("Invalid email");
            result = false;
        }

        return result;
    }

    private boolean validateName(){

        boolean result;

        if (register_name.getText ().toString ().length () <= 2 ){

            register_name.setError ("3 Characters expected");
            result = false;

            if (register_name.getText ().toString ().length () == 0){

                register_name.setError ("Name is Mandatory");
                result= false;

            }

        }else {

            result = true;

        }

        return result;

    }

    private boolean validatePassword() {

        boolean result;

        if (register_password.length () <= 5) {

            register_password.setFocusable (true);
            register_password.setError ("Atleast 6 characters expected");


            if (register_password.length () == 0) {

                register_password.setError ("Password is mandatory");

            }

            result = false;

        } else {

            result = true;
        }

        return result;

    }

    private boolean validatePhoneNumber() {

        boolean result;

        if (register_phonenumber.length () <= 9) {

            register_phonenumber.setFocusable (true);
            register_phonenumber.setError ("Not a valid number");

            if (register_phonenumber.length () == 0) {

                register_phonenumber.setError ("Phone number is mandatory");

            }

            result = false;

        } else {

            result = true;

        }

        return result;

    }

    private void sendDetailsToDatabase(){

        mFirebaseDatabase = FirebaseDatabase.getInstance ();
        id = FirebaseAuth.getInstance ().getUid ();

        mDatabaseReference = mFirebaseDatabase.getReference ("User").child (id);

        HashMap<String, String> hashMap = new HashMap<> ();
        hashMap.put ("id", id);
        hashMap.put ("name", name);
        hashMap.put ("email", email);
        hashMap.put ("phonenumber", phonenumber);
        hashMap.put ("imageUrl", "default");
        hashMap.put ("status", "offline");
        hashMap.put ("search", name.toLowerCase ());

        mDatabaseReference.setValue (hashMap).addOnCompleteListener (new OnCompleteListener<Void> () {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful ()) {

                    Intent intent = new Intent (RegisterActivity.this,  LoginActivity.class);
                    intent.addFlags (Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (intent);
                    finish ();

                } else {

                    Toasty.error (getApplicationContext (), task.getException ().getMessage (), Toasty.LENGTH_SHORT).show ();


                }

            }
        });

    }
}
