package com.localapp.camera;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.localapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.localapp.camera.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.localapp.ui.fragments.FeedFragment.VIDEO_REQUEST;
import static com.localapp.ui.activities.SignUpActivity.PICK_IMAGE_REQUEST;

public class Camera2Activity extends AppCompatActivity implements View.OnClickListener , GalleryRecyclerViewAdapter.OnItemClickListener{

    private static final String TAG = "Camera2Activity";
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    public static int WHICH_REQUEST = -1;



    public enum FlashMode{
        FLASH_ON,
        FLASH_OFF,
        FLASH_AUTO
    }
    private static FlashMode flashMode = FlashMode.FLASH_AUTO;
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private Button flipCamera, flashCameraButton;


    private RecyclerView myRecyclerView;
    private GalleryRecyclerViewAdapter galleryRecyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        /*ActionBar actionBar = getActionBar();
        actionBar.hide();*/

        WHICH_REQUEST = getIntent().getIntExtra("requestCode" ,-1);

//        cameraId = CAMERA_BACK;
        cameraId = CAMERA_FRONT;

        flipCamera = (Button) findViewById(R.id.flipCamera);
        flipCamera.setOnClickListener(this);

        flashCameraButton = (Button) findViewById(R.id.flash);
        flashCameraButton.setOnClickListener(this);

        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        takePictureButton.setOnLongClickListener(videoHoldListener);
//        takePictureButton.setOnTouchListener(videoTouchListener);

        myRecyclerView = (RecyclerView)findViewById(R.id.gallery_RecyclerView);
        linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        myRecyclerView.setItemViewCacheSize(50);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setHasFixedSize(true);
        myRecyclerView.setDrawingCacheEnabled(true);



//        prepareGallery();

        new GetAllImagePathTask().execute(); //background task
    }

    boolean isVideoButtonLongPressed = false;
    View.OnLongClickListener videoHoldListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // Do something when your hold starts here.
            Log.v(TAG,"on Long Click");

            if (!isVideoButtonLongPressed  && WHICH_REQUEST != PICK_IMAGE_REQUEST) {
//                startRecordingVideo();

                isVideoButtonLongPressed = true;
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                isVideoButtonLongPressed = false;

                return true;
            }
            return false;
        }
    };

    final int REQUEST_VIDEO_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri videoUri = intent.getData();
                    String path = getRealPathFromUriForImagesAndVideo(videoUri);
                    Intent returnIntent = new Intent();
//            returnIntent.putExtra("result",Uri.fromFile(new File(path)).toString());
                    returnIntent.putExtra("result",Uri.fromFile(new File(path)).toString());
                    setResult(VIDEO_REQUEST,returnIntent);
                    finish();

                }
                break;
            case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK && intent != null ) {

                    CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                    Uri uri = result.getUri();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",uri.toString());
                    setResult(WHICH_REQUEST,returnIntent);
                    finish();
//
                }
                break;

        }

    }

    private String getRealPathFromUriForImagesAndVideo(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    @Override
    public void onItemClick(GalleryRecyclerViewAdapter.ItemHolder item, int position) {
//        Toast.makeText(this, ""+item.getItemUri(), Toast.LENGTH_SHORT).show();
        /*Intent returnIntent = new Intent();
        returnIntent.putExtra("result",item.getItemUri().toString());
        setResult(WHICH_REQUEST,returnIntent);
        finish();*/
        startCropImageActivity(item.getItemUri());
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }


    public void prepareGallery(List<Uri> imageList) {

        /*List<Uri> imageList = new ArrayList<>();

        ArrayList<File> files = getFilePaths();
        for (File file:files) {
            imageList.add(Uri.fromFile(file));
        }*/



                galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(imageList,this);
        myRecyclerView.setAdapter(galleryRecyclerViewAdapter);

        galleryRecyclerViewAdapter.setOnItemClickListener(this);

    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //This is called when the camera is open
            Log.d(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            try {
                cameraDevice.close();
                cameraDevice = null;
            }catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(Camera2Activity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
//            createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture() {
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }


        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                try {
                    jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
            int width = 960;
            int height =720;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Flash setting
            setFlash(captureBuilder);



            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            if (cameraId == CAMERA_BACK) {
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            }else {
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, INVERSE_ORIENTATIONS.get(rotation));
            }

//            final File imageFile = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");

            final File imageFile;


            String state = Environment.getExternalStorageState();
            File folder = null;
            if (state.contains(Environment.MEDIA_MOUNTED)) {
                folder = new File(Environment
                        .getExternalStorageDirectory() + "/Localapp");
            } else {
                folder = new File(Environment
                        .getExternalStorageDirectory() + "/Localapp");
            }

            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                java.util.Date date = new java.util.Date();
                imageFile = new File(folder.getAbsolutePath()
                        + File.separator
                        + String.valueOf(System.currentTimeMillis()/1000)
                        + "_localapp.jpg");

                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "Image Not saved",
                        Toast.LENGTH_SHORT).show();
                return;
            }



            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(imageFile);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(Camera2Activity.this, "Saved:" + imageFile, Toast.LENGTH_SHORT).show();
                    createCameraPreview();

                    startCropImageActivity(Uri.fromFile(imageFile));

                    /*Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",Uri.fromFile(imageFile).toString());
                    setResult(WHICH_REQUEST,returnIntent);
                    finish();*/
                }
            };


            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }




    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(Camera2Activity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }



    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.d(TAG, "is camera open");
        try {
            //if device have single camera
            if (manager.getCameraIdList().length == 1) {
                cameraId = manager.getCameraIdList()[0];
            }

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
//            imageDimension = new Size(960,720);
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[1];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(Camera2Activity.this, PERMISSIONS, REQUEST_CAMERA_PERMISSION);
                return;
            }

            // Check if the flash is supported.
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            mFlashSupported = available == null ? false : available;

            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "openCamera X");
    }

    protected void updatePreview() {
        try {
            if(null == cameraDevice) {
                Log.e(TAG, "updatePreview error, return");
            }
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

//        setFlash(captureRequestBuilder);
            try {
                cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    /**
     * flip camera
     */
    public void flipCamera() {
        if (cameraId.equals(CAMERA_FRONT)) {
            cameraId = CAMERA_BACK;
            closeCamera();
            openCamera();

        } else if (cameraId.equals(CAMERA_BACK)) {
            cameraId = CAMERA_FRONT;
            closeCamera();
            openCamera();
        }
    }

    private void setFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            switch (flashMode){
                case FLASH_AUTO:
                    requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    break;
                case FLASH_OFF:
                    requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                            CaptureRequest.CONTROL_AE_MODE_OFF);
                    break;
                case FLASH_ON:
                    requestBuilder.set(CaptureRequest.FLASH_MODE,
                            CaptureRequest.FLASH_MODE_SINGLE);
                    break;
            }

        }
    }

    private void flashOnButton() {
        switch (flashMode) {
            case FLASH_AUTO:
                flashMode = FlashMode.FLASH_OFF;
                flashCameraButton.setBackground(getDrawable(R.drawable.ic_flash_off));
                return;

            case FLASH_OFF:
                flashMode = FlashMode.FLASH_ON;
                flashCameraButton.setBackground(getDrawable(R.drawable.ic_flash_on));

                return;
            case FLASH_ON:
                flashMode = FlashMode.FLASH_AUTO;
                flashCameraButton.setBackground(getDrawable(R.drawable.ic_automatic_flash));
                return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(Camera2Activity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash:
                flashOnButton();
                break;
            case R.id.flipCamera:
                flipCamera();
                break;
           /* case R.id.captureImage:
                takeImage();
                break;*/

            default:
                break;
        }
    }


    public ArrayList<File> getFilePaths() {

        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<File> resultIAV = new ArrayList<File>();

        String[] directories = null;
        if (u != null)
        {
            c = managedQuery(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst()))
        {
            do
            {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try{
                    dirList.add(tempDir);
                }
                catch(Exception e)
                {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for(int i=0;i<dirList.size();i++)
        {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if(imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if(imagePath.isDirectory())
                    {
                        imageList = imagePath.listFiles();

                    }
                    if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
//                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
//                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            )
                    {



                        String path= imagePath.getAbsolutePath();
//                        resultIAV.add(Uri.parse(path));
                        resultIAV.add(imagePath);


                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(resultIAV, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                Long obj1 = o1.lastModified();
                Long obj2 = o2.lastModified();
                return obj1.compareTo(obj2);
            }
        });

        Collections.reverse(resultIAV);

        return resultIAV;


    }

    private class GetAllImagePathTask extends AsyncTask<Void,Void,List<Uri>>{

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected List<Uri> doInBackground(Void... params) {
            List<Uri> imageList = new ArrayList<>();

            try {
                ArrayList<File> files = getFilePaths();
                for (File file : files) {
                    imageList.add(Uri.fromFile(file));
                }
                return imageList;
            }catch (NullPointerException npe) {
                npe.printStackTrace();
                return null;
            }
        }


        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param uris The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(List<Uri> uris) {
            super.onPostExecute(uris);

            if (uris != null) {
                prepareGallery(uris);
            }else {
                Toast.makeText(Camera2Activity.this, "Camera open failed", Toast.LENGTH_SHORT).show();
                Camera2Activity.this.finish();
            }
        }
    }

}
