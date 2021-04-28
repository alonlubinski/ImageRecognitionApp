package com.alon.imagerecognitionapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alon.imagerecognitionapp.R;
import com.alon.imagerecognitionapp.Models.RecognitionItem;
import com.alon.imagerecognitionapp.Adapters.RecognitionItemAdapter;
import com.alon.imagerecognitionapp.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView main_IMG_image;
    private RecyclerView main_RCV;
    private Button main_BTN_select, main_BTN_detect;
    private int RESULT_LOAD_IMG = 0;
    private ArrayList<RecognitionItem> dataSet;
    private RecyclerView.LayoutManager layoutManager;
    private RecognitionItemAdapter recognitionItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSet = new ArrayList<>();
        String fileName = "labels.txt";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            while((line = reader.readLine()) != null){
                RecognitionItem item = new RecognitionItem();
                item.setName(line);
                dataSet.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        findAll();
        setClickListeners();
    }

    private void setClickListeners() {
        main_BTN_select.setOnClickListener(this);
        main_BTN_detect.setOnClickListener(this);
    }

    private void findAll() {
        main_IMG_image = findViewById(R.id.main_IMG_image);
        main_RCV = findViewById(R.id.main_RCV);
        main_BTN_select = findViewById(R.id.main_BTN_select);
        main_BTN_detect = findViewById(R.id.main_BTN_detect);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_BTN_select:
                Intent photoSelect = new Intent(Intent.ACTION_PICK);
                photoSelect.setType("image/*");
                startActivityForResult(photoSelect, RESULT_LOAD_IMG);
                break;
            case R.id.main_BTN_detect:
                Bitmap bm = ((BitmapDrawable) main_IMG_image.getDrawable()).getBitmap();
                Bitmap resize = Bitmap.createScaledBitmap(bm, 224, 224, true);
                try {
                    MobilenetV110224Quant iModel = MobilenetV110224Quant.newInstance(this); // Image Classification Model


                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

                    TensorImage selectImage = TensorImage.fromBitmap(resize);

                    ByteBuffer byteBuffer = selectImage.getBuffer();

                    inputFeature0.loadBuffer(byteBuffer);



                    // Runs model inference and gets result.
                    MobilenetV110224Quant.Outputs iOutputs = iModel.process(inputFeature0);
                    TensorBuffer outputFeature0 = iOutputs.getOutputFeature0AsTensorBuffer();


                    ArrayList<RecognitionItem> sortedDataSet = new ArrayList<>();
                    sortedDataSet.addAll(dataSet);
                    setMatchPercents(sortedDataSet, outputFeature0.getFloatArray());
                    Collections.sort(sortedDataSet, new Comparator<RecognitionItem>() {
                        @Override
                        public int compare(RecognitionItem o1, RecognitionItem o2) {
                            return Float.compare(o2.getMatch(), o1.getMatch());
                        }
                    });

                    initRecyclerView(sortedDataSet.subList(0, 100));
                    // Releases models resources if no longer used.
                    iModel.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }
                break;
        }
    }

    private void setMatchPercents(ArrayList<RecognitionItem> sortedDataSet, float[] arr){
        for(int i = 0; i < arr.length; i++){
            sortedDataSet.get(i).setMatch(arr[i] / 255 * 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_LOAD_IMG){
                Uri imageUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectImage = BitmapFactory.decodeStream(imageStream);
                    main_IMG_image.setImageBitmap(selectImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "You have not picked image", Toast.LENGTH_LONG).show();
        }
    }

    private void initRecyclerView(List<RecognitionItem> sortedDataSet) {
        main_RCV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        main_RCV.setLayoutManager(layoutManager);
        recognitionItemAdapter = new RecognitionItemAdapter(sortedDataSet);
        main_RCV.setAdapter(recognitionItemAdapter);
    }
}