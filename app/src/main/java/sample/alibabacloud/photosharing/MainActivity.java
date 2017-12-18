package sample.alibabacloud.photosharing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
//import com.alibaba.sdk.android.oss.OSSException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class  MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 800;
    private final static String TAG = "MainActivity";
    Button cameraBtn, galleryBtn, documentsBtn, resizeImg;
    List<Uri> mSelected;
    OSS oss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        documentsBtn = findViewById(R.id.documentsBtn);
        resizeImg = findViewById(R.id.resizeImg);

        mSelected = new ArrayList<>();

        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        documentsBtn.setOnClickListener(this);
        resizeImg.setOnClickListener(this);

//         Create the Client
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(getString(R.string.AccessKey),getString(R.string.AccessKeySecret),getString(R.string.stsToken));

        oss = new OSSClient(getApplicationContext(), getString(R.string.Endpoint), credentialProvider);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.cameraBtn){
            EasyImage.openCamera(this,0);
        }else if(id == R.id.galleryBtn){
            EasyImage.openGallery(this,0);
        }else if(id == R.id.documentsBtn){
            EasyImage.openDocuments(this,0);
        }else if(id == R.id.resizeImg){
            resizeImg();
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
        PutObjectRequest put = new PutObjectRequest(getString(R.string.Bucket_Name), "Demo_Picture_1.jpg", filePath);
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

    public void resizeImg(){

        try {
            String style = "image/resize,m_fixed,w_100,h_100";
            GetObjectRequest request = new GetObjectRequest(getString(R.string.Bucket_Name), "Demo_Picture_1.jpg");
            request.setxOssProcess(style);

            oss.getObject(request);
//        } catch (OSSException oe) {
//        System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                + "but was rejected with an error response for some reason.");
//        System.out.println("Error Message: " + oe.getErrorCode());
//        System.out.println("Error Code:       " + oe.getErrorCode());
//        System.out.println("Request ID:      " + oe.getRequestId());
//        System.out.println("Host ID:           " + oe.getHostId());
    } catch (ClientException ce) {
        System.out.println("Caught an ClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with OSS, "
                + "such as not being able to access the network.");
        System.out.println("Error Message: " + ce.getMessage());
    } catch (Throwable e) {
        e.printStackTrace();
    }

    }
}
