package com.example.lalit.videoapplication;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

public class MainActivity extends Activity {

    private final int VIDEO_REQUEST_CODE = 100;
    private String ONEDRIVE_Client_ID = "56d616ac-7bf2-4282-a389-d09803737e57";
    private IPicker mPicker;
    private boolean picker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.startPickerButton)).setOnClickListener(mStartPickingListener);
    }

    public void captureVideo(View view)
    {
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilepath();
        Uri video_uri = Uri.fromFile(video_file);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,video_uri);
        camera_intent.putExtra("android.intent.extras.CAMERA_FACING", CAMERA_FACING_FRONT);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);

    }

    private final View.OnClickListener mStartPickingListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            picker = true;
            mPicker = Picker.createPicker(ONEDRIVE_Client_ID);

            // open one drive app to pick the file
            mPicker.startPicking((Activity) v.getContext(), LinkType.WebViewLink);
        }
    };



    private final View.OnClickListener FilesaverListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            picker = false;

            // create example file to save to OneDrive
            final String filename = "sample_video.mp4";
            // file path
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/", filename);

            Log.e("file", Uri.parse("file://" + file.getAbsolutePath()) +"...." + file);

            // create and launch the saver
            mSaver = Saver.createSaver(ONEDRIVE_Client_ID);
            mSaver.startSaving((Activity)v.getContext(), filename,
                    Uri.parse("file://" + file.getAbsolutePath()));
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==VIDEO_REQUEST_CODE)
        {

            if (resultCode==RESULT_OK)
            {
                Toast.makeText(getApplicationContext(),"Video Successfully Recorded", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Video Capture Failed", Toast.LENGTH_LONG).show();
            }

        }


        // Get the results from the picker
        IPickerResult result = mPicker.getPickerResult(requestCode, resultCode, data);

        // Handle the case if nothing was picked
        if (result != null) {

            // Do something with the picked file
            Log.e("main", "Link to file " + result.getName() + ": " + result.getLink());
            ((TextView) findViewById(R.id.pic_name)).setText(result.getName());
            ((TextView) findViewById(R.id.pic_link)).setText(result.getLink().toString());

            return;
        }

    }

    public File getFilepath()
    {
        File folder = new File("sdcard/video_app");
        if (!folder.exists())
        {
            folder.mkdir();
        }

        File video_file = new File(folder,"sample_video.mp4");

        return video_file;
    }
}
