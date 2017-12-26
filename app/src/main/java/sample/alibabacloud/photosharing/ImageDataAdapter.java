package sample.alibabacloud.photosharing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import sample.alibabacloud.photosharing.model.ImageData;

/**
 * Created by saisarathchandra on 25/12/17.
 */

public class ImageDataAdapter extends RecyclerView.Adapter<ImageDataAdapter.MyViewHolder> {

    private List<ImageData> imageDataList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView imageName;
        public ImageView imageView;


        public MyViewHolder(View view) {
            super(view);
           imageView = view.findViewById(R.id.imageView);
           imageName = view.findViewById(R.id.imageName);
        }
    }


    public ImageDataAdapter(List<ImageData> imageDataList,Context mContext) {
        this.imageDataList = imageDataList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageData imageData = imageDataList.get(position);
        Glide.with(mContext).load(imageData.getImageURL()).into(holder.imageView);
        holder.imageName.setText(imageData.getImageName());
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }
}
