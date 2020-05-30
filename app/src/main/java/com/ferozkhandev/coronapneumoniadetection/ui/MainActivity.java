package com.ferozkhandev.coronapneumoniadetection.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.ferozkhandev.coronapneumoniadetection.R;
import com.ferozkhandev.coronapneumoniadetection.databinding.ActivityMainBinding;
import com.ferozkhandev.coronapneumoniadetection.retrofit.ImageUploadClient;
import com.ferozkhandev.coronapneumoniadetection.utils.FileOperations;
import com.ferozkhandev.coronapneumoniadetection.utils.ProgressRequestBody;
import com.ferozkhandev.coronapneumoniadetection.utils.RetrofitUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks {

    private static final int PICK_FROM_GALLERY_CODE = 3;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PERMISSION_CODE = 2;
    private String currentPhotoPath;
    private File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.addImage.setOnClickListener(v -> checkPermissions());
        binding.uploadImage.setOnClickListener(v -> {
            if (file !=null ){
                uploadImage(file);
            } else {
                Snackbar.make(findViewById(android.R.id.content), "No image Selected.",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
        viewModel.getUriMutableLiveData().observe(this, uri -> {
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                binding.imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        } else if (requestCode == PICK_FROM_GALLERY_CODE && resultCode == RESULT_OK){
            if (data!=null && data.getData()!=null){
                Uri selectedImage = data.getData();
                viewModel.setUriMutableLiveData(selectedImage);
                File image = new File(FileOperations.getRealPathFromURI(selectedImage, this));
                file = image;
                /*Bitmap photo = null;
                try {
                    photo = FileOperations.getThumbnail(selectedImage, this, 120);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                dispatchTakePictureIntent();
                pickFromGallery();
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Permission for required action is not given"
                        , Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
//                dispatchTakePictureIntent();
                pickFromGallery();
            }
        } else {
//            dispatchTakePictureIntent();
            pickFromGallery();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Snackbar.make(findViewById(android.R.id.content), ""+ex.getLocalizedMessage(),
                        Snackbar.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ferozkhandev.coronapneumoniadetection.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        /*File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );*/

        File image = new File(storageDir+ imageFileName+".jpg");
        file = image;

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void pickFromGallery(){
        Intent pickPhoto = new Intent();
        pickPhoto.setType("image/*");
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickPhoto,"Select Picture"), PICK_FROM_GALLERY_CODE);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = binding.imageView.getWidth();
        int targetH = binding.imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        binding.imageView.setImageTintMode(null);
        binding.imageView.setImageBitmap(bitmap);
    }










    /* Call API & Detect Corona Logic below */

    private void uploadImage(File file){
        ProgressRequestBody fileBody = new ProgressRequestBody(file, ".jpg" ,this);
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("image", file.getName(), fileBody);

        ImageUploadClient client = RetrofitUtil.retrofit.create(ImageUploadClient.class);
        Call<String> call = client.uploadimage(filePart);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.code() == 201){
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.uploadImage.setEnabled(true);
                    Snackbar.make(findViewById(android.R.id.content),
                            ""+response.body()
                            , Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.uploadImage.setEnabled(true);
                Snackbar.make(findViewById(android.R.id.content),
                        file.getName()+ t.getMessage()
                        , Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onProgressUpdate(int percentage) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.uploadImage.setEnabled(false);
        binding.progressBar.setProgress(percentage);
    }

    @Override
    public void onError() {
        binding.uploadImage.setEnabled(true);
    }

    @Override
    public void onFinish() {
        binding.uploadImage.setEnabled(true);
    }
}
