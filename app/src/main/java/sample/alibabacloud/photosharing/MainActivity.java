package sample.alibabacloud.photosharing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import sample.alibabacloud.photosharing.model.ImageProcessDataModel;
import sample.alibabacloud.photosharing.util.Constants;
import sample.alibabacloud.photosharing.util.CustomFont;

public class  MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 800;
    private final static String TAG = "MainActivity";
    ImageButton cameraBtn, galleryBtn, documentsBtn;
    Button resizeImg, cropImg, rotateImg, sharpenImg, watermarkImg;
    List<Uri> mSelected;
    OSS oss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomFont customFont = new CustomFont(this);

        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        documentsBtn = findViewById(R.id.documentsBtn);

        resizeImg = findViewById(R.id.resizeImg);
        cropImg = findViewById(R.id.cropImg);
        rotateImg = findViewById(R.id.rotateImg);
        sharpenImg = findViewById(R.id.sharpenImg);
        watermarkImg = findViewById(R.id.watermarkImg);

        resizeImg.setTypeface(customFont.getBreeSerifRegular());
        cropImg.setTypeface(customFont.getBreeSerifRegular());
        rotateImg.setTypeface(customFont.getBreeSerifRegular());
        sharpenImg.setTypeface(customFont.getBreeSerifRegular());
        watermarkImg.setTypeface(customFont.getBreeSerifRegular());

        mSelected = new ArrayList<>();

        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        documentsBtn.setOnClickListener(this);
        resizeImg.setOnClickListener(this);
        cropImg.setOnClickListener(this);
        rotateImg.setOnClickListener(this);
        sharpenImg.setOnClickListener(this);
        watermarkImg.setOnClickListener(this);
//         Create the Client

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(getString(R.string.AccessKey),getString(R.string.AccessKeySecret),getString(R.string.stsToken));
        oss = new OSSClient(getApplicationContext(), getString(R.string.Endpoint), credentialProvider);


    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.cameraBtn){
            Toast.makeText(this,"Opening Camera",Toast.LENGTH_SHORT).show();
            EasyImage.openCamera(this,0);
        }else if(id == R.id.galleryBtn){
            Toast.makeText(this,"Opening Gallery",Toast.LENGTH_SHORT).show();
            EasyImage.openGallery(this,0);
        }else if(id == R.id.documentsBtn){
            Toast.makeText(this,"Opening Documents",Toast.LENGTH_SHORT).show();
            EasyImage.openDocuments(this,0);
        }else if(id == R.id.resizeImg){
            Toast.makeText(this,"Image Resize Requested",Toast.LENGTH_SHORT).show();
            ProcessImg processImg = new ProcessImg(this);
            ImageProcessDataModel imgProcess = new ImageProcessDataModel();
            imgProcess.setStyle(Constants.RESIZE_STYLE_DEF);
            imgProcess.setStyleName(Constants.RESIZE_STYLE_NAME);
            processImg.execute(imgProcess);
        }else if(id == R.id.cropImg){
            Toast.makeText(this,"Image Crop Requested",Toast.LENGTH_SHORT).show();
            ProcessImg processImg = new ProcessImg(this);
            ImageProcessDataModel imgProcess = new ImageProcessDataModel();
            imgProcess.setStyle(Constants.CROP_STYLE_DEF);
            imgProcess.setStyleName(Constants.CROP_STYLE_NAME);
            processImg.execute(imgProcess);
        }else if(id == R.id.rotateImg){
            Toast.makeText(this,"Image Rotate Requested",Toast.LENGTH_SHORT).show();
            ProcessImg processImg = new ProcessImg(this);
            ImageProcessDataModel imgProcess = new ImageProcessDataModel();
            imgProcess.setStyle(Constants.ROTATE_STYLE_DEF);
            imgProcess.setStyleName(Constants.ROTATE_STYLE_NAME);
            processImg.execute(imgProcess);
        }else if(id == R.id.sharpenImg){
            Toast.makeText(this,"Image Sharpening Requested",Toast.LENGTH_SHORT).show();
            ProcessImg processImg = new ProcessImg(this);
            ImageProcessDataModel imgProcess = new ImageProcessDataModel();
            imgProcess.setStyle(Constants.SHARPEN_STYLE_DEF);
            imgProcess.setStyleName(Constants.SHARPEN_STYLE_NAME);
            processImg.execute(imgProcess);
        }else if(id == R.id.watermarkImg){
            Toast.makeText(this,"Image Watermarking Requested",Toast.LENGTH_SHORT).show();
            ProcessImg processImg = new ProcessImg(this);
            ImageProcessDataModel imgProcess = new ImageProcessDataModel();
            imgProcess.setStyle(Constants.WATERMARK_STYLE_DEF);
            imgProcess.setStyleName(Constants.WATERMARK_STYLE_NAME);
            processImg.execute(imgProcess);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Log.d(TAG, "onImagePickerError: Error in Image handling");
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Log.d(TAG, "onImagePicked:"+imageFile.getAbsolutePath());
                putFileOSS(imageFile.getAbsolutePath());

            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                    if (photoFile != null)
                        photoFile.delete();
                }
            }
        });
    }

    public void putFileOSS(String filePath){
        PutObjectRequest put = new PutObjectRequest(getString(R.string.Bucket_Name), "Demo_Picture_1.png", filePath);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }
            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // Request exception
                if (clientExcepion != null) {
                    // Local exception, such as a network exception
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // Service exception
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

    }

    class ProcessImg extends AsyncTask<ImageProcessDataModel, Void, Void> {
        private ProgressDialog dialog;

        public ProcessImg(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Processing Image");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(ImageProcessDataModel... dataModel) {

                ImageProcessDataModel data = dataModel[0];
                String fileName = "Demo_Picture_1";
                String transformedFileName = fileName+"_"+data.getStyleName()+".jpg";

                try {
//                    String style = "image/resize,m_fixed,w_100,h_100";
                    GetObjectRequest request = new GetObjectRequest(getString(R.string.Bucket_Name), fileName+".png");
                    TestGetCallback getCallback = new TestGetCallback();

                    request.setxOssProcess(data.getStyle());

                    request.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
                        @Override
                        public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                            Log.d(TAG, "getobj_progress: " + currentSize+"  total_size: " + totalSize);
                            final long percent= currentSize/totalSize;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("Processed : "+(percent*100));
                                }
                            });
                        }
                    });

                    OSSAsyncTask task = oss.asyncGetObject(request, getCallback);
                    task.waitUntilFinished();

                    Log.d(TAG, "resizeImg: "+getCallback.result.getMetadata().toString());
                    Log.d(TAG, "resizeImg: "+getCallback.request.getObjectKey());


                    PutObjectRequest put = new PutObjectRequest(getString(R.string.Bucket_Name), transformedFileName, org.apache.commons.io.IOUtils.toByteArray(getCallback.result.getObjectContent()));
                    put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                        @Override
                        public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                            Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                        }
                    });

                    OSSAsyncTask task2 = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                        @Override
                        public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                            Log.d("PutObject", "UploadSuccess");
                            Log.d("ETag", result.getETag());
                            Log.d("RequestId", result.getRequestId());
                        }
                        @Override
                        public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                            // Request exception
                            if (clientExcepion != null) {
                                // Local exception, such as a network exception
                                clientExcepion.printStackTrace();
                            }
                            if (serviceException != null) {
                                // Service exception
                                Log.e("ErrorCode", serviceException.getErrorCode());
                                Log.e("RequestId", serviceException.getRequestId());
                                Log.e("HostId", serviceException.getHostId());
                                Log.e("RawMessage", serviceException.getRawMessage());
                            }
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(dialog.isShowing()){
                dialog.dismiss();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Operation Completed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public final static class TestGetCallback implements OSSCompletedCallback<GetObjectRequest, GetObjectResult> {

        private GetObjectRequest request;
        private GetObjectResult result;
        private ClientException clientException;
        private ServiceException serviceException;


        @Override
        public void onSuccess(GetObjectRequest request, GetObjectResult result) {
            this.request = request;
            this.result = result;

            Log.d(TAG, "onSuccess: "+result);
        }

        @Override
        public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
            this.request = request;
            this.clientException = clientExcepion;
            this.serviceException = serviceException;
        }
    }
}
