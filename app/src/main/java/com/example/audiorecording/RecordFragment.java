package com.example.audiorecording;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {
private NavController navController;
private ImageButton listbtn;
private ImageButton reco;
private String recordPermission = Manifest.permission.RECORD_AUDIO;
private int PERMISSION_CODE = 21;
    private TextView filenameText;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;


private boolean isRecording=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recourd, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController= Navigation.findNavController(view);
        listbtn=view.findViewById(R.id.record_list_btn);
        reco=view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);


        listbtn.setOnClickListener(this);
        reco.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.record_list_btn:
                if (isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recourdFragment_to_audioListFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", null);
                    alertDialog.setTitle("Audio Still recording");
                    alertDialog.setMessage("Are you sure, you want to stop the recording?");
                    alertDialog.create().show();
                }else{
                    navController.navigate(R.id.action_recourdFragment_to_audioListFragment);
                }
                break;
            case R.id.record_btn:
                    if (isRecording){
                        //stop recording
                        stopRecording();
                        reco.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped,null));
                        isRecording=false;
                    }else {
                        //start recording
                        if(checkpermission()) {
                            isRecording = true;
                            startRecording();
                            reco.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                        }
                    }
                break;
        }
    }

    private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }


    private void startRecording() {
        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        //Get app external directory path
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }
    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Change text on page to file saved
        filenameText.setText("Recording Stopped, File Saved : " + recordFile);

        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }


}
