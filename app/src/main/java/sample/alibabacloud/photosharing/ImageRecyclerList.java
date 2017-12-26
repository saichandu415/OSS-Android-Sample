package sample.alibabacloud.photosharing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;

import java.util.ArrayList;
import java.util.List;

import sample.alibabacloud.photosharing.model.ImageData;

/**
 * Created by saisarathchandra on 25/12/17.
 */

public class ImageRecyclerList extends Activity {

    private List<ImageData> imageDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageDataAdapter mAdapter;
    OSS oss;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_recycler);

        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new ImageDataAdapter(imageDataList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(getString(R.string.AccessKey),getString(R.string.AccessKeySecret),getString(R.string.stsToken));
        oss = new OSSClient(getApplicationContext(), getString(R.string.Endpoint), credentialProvider);

        getFiles();

    }

    public void getFiles(){
        ListObjectsRequest listObjects = new ListObjectsRequest(getString(R.string.Bucket_Name));

        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                imageDataList.clear();
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                            + result.getObjectSummaries().get(i).getETag() + " "
                            + result.getObjectSummaries().get(i).getLastModified());

                    ImageData imageData = new ImageData();
                    imageData.setImageName(result.getObjectSummaries().get(i).getKey());
                    imageData.setImageURL(getString(R.string.Bucket_Endpoint)+result.getObjectSummaries().get(i).getKey());
                    imageDataList.add(imageData);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
        task.waitUntilFinished();
    }

}
