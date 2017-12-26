package sample.alibabacloud.photosharing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import sample.alibabacloud.photosharing.util.CustomFont;

/**
 * Created by saisarathchandra on 25/12/17.
 */

public class WelcomeActivity extends Activity implements View.OnClickListener{

    Button uploadFiles,viewImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        CustomFont customFont = new CustomFont(this);

        uploadFiles = findViewById(R.id.uploadFiles);
        viewImages = findViewById(R.id.viewImages);

        uploadFiles.setTypeface(customFont.getBungeeRegular());
        viewImages.setTypeface(customFont.getBungeeRegular());
        uploadFiles.setOnClickListener(this);
        viewImages.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.uploadFiles){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }else if(id == R.id.viewImages){
            Intent i = new Intent(this,ImageRecyclerList.class);
            startActivity(i);
        }
    }
}
