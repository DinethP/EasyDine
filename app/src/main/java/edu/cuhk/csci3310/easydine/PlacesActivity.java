package edu.cuhk.csci3310.easydine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

public class PlacesActivity extends AppCompatActivity {
    private String apikey = "AIzaSyA4A0EkXxHGQ_0qTMcKvrcwhuQaJJBklPc";
    private String TAG = "PlacesActivity";
    Place place;
    EditText editText;
    TextView name, location, rating;
    ImageView imageView;
    Button cancel_btn, next_btn;
    FusedLocationProviderClient fusedLocationProviderClient;
    ScrollView scrollView;
    FrameLayout usersFrameLayout;
    FrameLayout mapFrameLayout;
    // integers to refer to showLocation or showRoute fragments
    private final int singleOrder = 0;
    private final int groupOrder = 1;
    private boolean openUsersFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        // Initialize the  Places SDK
        Places.initialize(getApplicationContext(), apikey);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        editText = findViewById(R.id.edit_text);
        name = findViewById(R.id.name);
        location = findViewById(R.id.location);
        rating = findViewById(R.id.rating);
        cancel_btn = findViewById(R.id.cancel_btn);
        next_btn = findViewById(R.id.next_btn);
        imageView = findViewById(R.id.imageView);
        scrollView = findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.GONE);
        usersFrameLayout = findViewById(R.id.users_frame_layout);
        usersFrameLayout.setVisibility(View.GONE);
        mapFrameLayout = findViewById(R.id.map_frame_layout);
        mapFrameLayout.setVisibility(View.GONE);

        // next_btn will be disabled at start
        next_btn.setEnabled(false);
        editText.setFocusable(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // only enable next button if text has been entered to editText
            @Override
            public void afterTextChanged(Editable editable) {
                enableNextButton();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PlacesActivity.this);
        // check permission for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // permission is granted
            Log.d(TAG, "onCreate: Permission granted");
            getCurrentLocation();
        } else {
            // when permission not granted, request permission
            ActivityCompat.requestPermissions(PlacesActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intialise Place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.RATING, Place.Field.PHOTO_METADATAS);
                //  create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        // TODO: cannot filter only for restaurants
                        .build(PlacesActivity.this);
                // Start activity for result
                startActivityForResult(intent, 100);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlacesActivity.this, NewOrderDetails.class);
                intent.putExtra("PLACE", place);
                startActivity(intent);
            }
        });
        // default single order radio button is checked
        RadioButton button = findViewById(R.id.radio_single);
        button.setChecked(true);
        // detect radiobutton checks
        RadioGroup radioGroup = findViewById(R.id.particpantsGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                //  get index of which button was clicked
                int index = radioGroup.indexOfChild(radioButton);
                switch (index){
                    case singleOrder:
                        usersFrameLayout.setVisibility(View.GONE);
                        break;
                    case groupOrder:
                        AddParticipantsFragment addParticipantsFragment = new AddParticipantsFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.users_frame_layout, addParticipantsFragment, null).commit();
                        usersFrameLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    // check if text has been entered to editText to enable next_btn
    private void enableNextButton() {
        Log.d(TAG, "From enableNextButton: " + editText.getText().toString());
        boolean isReady = editText.getText().toString().length() > 1;
        next_btn.setEnabled(isReady);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: Bug where even if permission is granted, it shows permission denied toast
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // when permission granted, call method
            getCurrentLocation();
        } else {
            // when permission is denied
            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        Context context = this.getApplicationContext();
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        Activity activity = this;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // location service is enabled get last location
            // check if the permission is granted
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permission, 100);
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    // initialise location
                    Location location = task.getResult();
                    if (location != null) {
                        Log.d(TAG, String.valueOf(location.getLatitude()));
                        Log.d(TAG, String.valueOf(location.getLongitude()));

                    } else {
                        // when location is null, make location requuest
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        // initialse location callback
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                Log.d(TAG, String.valueOf(location1.getLatitude()));
                                Log.d(TAG, String.valueOf(location1.getLongitude()));
                            }
                        };
                        // request location updates
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, permission, 100);
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    }
                }
            });
        }else{
            // when location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            // Initialise place
            place = Autocomplete.getPlaceFromIntent(data);

            // Set address on editText
//            editText.setText((place.getAddress()));
//            Log.d(TAG, "Address: " + place.getAddress());

            //set photo
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxHeight(600) // Optional.
                    .build();
            PlacesClient placesClient = Places.createClient(this);
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
            // set views to be visisble
            scrollView.setVisibility(View.VISIBLE);
            mapFrameLayout.setVisibility(View.VISIBLE);
            // set name
            name.setText(String.format("Name: %s", place.getName()));

            // Set address
            location.setText(String.format("Address: %s", place.getAddress()));
            Log.d(TAG, "Name: " + place.getName());

            //set rating
            rating.setText(String.format("Rating: %.1f", place.getRating()));

            //set fragment
            Bundle bundle = new Bundle();
            MapsFragment mapsFragment = new MapsFragment();

            LatLng latlng = place.getLatLng();
            bundle.putParcelable("Location", latlng);
            bundle.putString("Address", place.getAddress());
            mapsFragment.setArguments(bundle);


            getSupportFragmentManager().beginTransaction().add(R.id.map, mapsFragment, null).commit();
//            AddParticipantsFragment addParticipantsFragment = new AddParticipantsFragment();
//            getSupportFragmentManager().beginTransaction().add(R.id.usersFrameLayout, addParticipantsFragment, null).commit();
            // enable next button
            next_btn.setEnabled(true);

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            // display toast
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}