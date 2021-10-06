package houston.david.hikingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.PointType;
import data.Point;

/**
 * This class is responsible for handling the waypoint information inputted by a user.
 */
public class CreateWayPointActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    String photoUriString;
    Button imageButton, submitButton;
    ImageView detailImg;
    EditText breadcrumbText;
    Point breadCrumbPoint;

    /**
     * THis method grabs the intent details (point) that was captured when the
     * user selected the waypoint button.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breadcrumb_detail);

        imageButton = findViewById(R.id.btn_addImgWayPointBtn);
        detailImg = findViewById(R.id.ib_breadCrumbImg);
        submitButton = findViewById(R.id.btn_waypointBtn);
        breadcrumbText = findViewById(R.id.tiet_inputWapoiintText);
        breadcrumbText.setBreakStrategy(LineBreaker.BREAK_STRATEGY_HIGH_QUALITY);
        breadcrumbText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        Intent intent = getIntent();
        //recreate the point from intent.
        breadCrumbPoint = (Point) intent.getSerializableExtra("point") ;

        /**
         * this onclick listener fires off the camera.
         */
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        /**
         * this onclick listener will call the 'setBreadCrumbDetail'
         * method.
         */
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBreadcrumbDetail(breadCrumbPoint);
            }
        });
    }

    /**
     * This method when called fires off the camera intent to take a photo
     * and will create a file for the photo, saved to memory,
     * we can the grab the uri from the image to use in the waypoint dialog.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Could not generate a file.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "houston.david.hikingapp.fileprovider",
                        photoFile);
                photoUriString = photoURI.toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
    }

    /**
     *
     * @return - creates an image file based on the current time stamp to allow for unique naming.
     * and stores in a directory called PIctures,
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * This method passes in a Point, and assign more information too it
     * Description is taken form user input feild.
     * Point type is assigned a type of WayPoint
     * photo uri is set from the inital creation in dispatch picture intent.
     * @param p
     */
    public void setBreadcrumbDetail(Point p) {
        String crumbDesc = breadcrumbText.getText().toString().trim();

        if(crumbDesc.isEmpty() | crumbDesc.length() > 160) {
            breadcrumbText.setError("Please enter 160 characters or less");
            breadcrumbText.requestFocus();
            return;
        }

        p.setPointType(PointType.WAYPOINT);
        p.setPointDescription(crumbDesc);
        p.setPointImageUri(photoUriString);
        createRouteActivity.userpoints.add(p);
        Toast.makeText(this, "New Waypoint Added", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Request method for camera permissions.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this).load(currentPhotoPath).into(detailImg);
        }
    }

}