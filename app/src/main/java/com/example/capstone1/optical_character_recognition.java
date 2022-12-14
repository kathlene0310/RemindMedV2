package com.example.capstone1;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone1.dependent.editDeleteMedicationsDependent;
import com.example.capstone1.dependent.newMedications;
import com.example.capstone1.v2.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class optical_character_recognition extends AppCompatActivity {
    Float speed, pitch;
    String voice;
    SharedPref sf;
    Button captureImage, saveText, speakButton;
    ImageView viewImage;
    Uri imageUri;
    FloatingActionButton tts;
    TextToSpeech textToSpeech;
    String text;
    int ocrChoice;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_IMAGE_COUNT = 3;
    TextView displayText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optical_character_recognition);

        captureImage = (Button) findViewById(R.id.captureButton);
        saveText = (Button) findViewById(R.id.buttonTxttoEditTxt);
        tts = findViewById(R.id.ttsButtonSxan);
        speakButton = findViewById(R.id.ttsButtonScan);
        sf = new SharedPref(getApplicationContext());

        voice = sf.getVoice();
        speed = sf.getSpeed();
        pitch = sf.getPitch();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS", "Language not supported");
                    }
                    else
                    {
                        tts.setEnabled(true);
                    }
                }
                else
                {
                    Log.e("TTS", "Iniitialization failed");
                }
            }
        });

        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    text = displayText.getText().toString();

                    if(text.length()>30)
                    {
                        text = "Please scan only the medicine name ";
                    }
                    speak();
                }catch (Exception e)
                {
                    text = "Please scan a medicine name";
                    speak();
                }


            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    text = displayText.getText().toString();

                    if(text.length()>30)
                    {
                        text = "Please scan only the medicine name ";
                    }
                    speak();
                }catch (Exception e)
                {
                    text = "Please scan a medicine name";
                    speak();
                }
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {
                    File image_file = File.createTempFile(fileName, ".jpg", storageDirectory);
                    //currentPhotoPath = image_file.getAbsolutePath();
                    ContentValues values = new ContentValues();
                    //imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.android.fileprovider", image_file );
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberOnly = displayText.getText().toString().replaceAll("[^0-9]", "");
                ocrChoice = getIntent().getIntExtra("ocrchoice", 0);
                if (ocrChoice == 1)
                {
                    try {
                        new_medications.medication.setText(displayText.getText().toString());
                        new_medications.inventory.setText(numberOnly);
                        finish();
                    }catch (Exception e)
                    {
                        Toast.makeText(optical_character_recognition.this, "Scan your medicine name", Toast.LENGTH_SHORT).show();

                    }
                }
                else if (ocrChoice == 2)
                {
                    try{
                        edit_delete_medications.medInventory.setText(displayText.getText().toString());
                        finish();
                    }catch (Exception e)
                    {
                        Toast.makeText(optical_character_recognition.this, "Scan your medicine inventory", Toast.LENGTH_SHORT).show();

                    }
                }
                else if(ocrChoice == 3) {
                    try{
                        newMedications.medication.setText(displayText.getText().toString());
                        finish();
                    }catch (Exception e)
                    {
                        Toast.makeText(optical_character_recognition.this, "Scan your medicine inventory", Toast.LENGTH_SHORT).show();

                    }
                }
                else if(ocrChoice == 4) {
                    try{
                        editDeleteMedicationsDependent.medName.setText(displayText.getText().toString());
                        finish();
                    }catch (Exception e)
                    {
                        Toast.makeText(optical_character_recognition.this, "Scan your medicine inventory", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int rotationDegree = 0;
        displayText = (TextView) findViewById(R.id.medNameTV);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            }
            if (requestCode == 2) {
                requestCode = REQUEST_IMAGE_COUNT;
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                {
                    Uri resultUri = result.getUri();
                    ImageView imageView = findViewById(R.id.medImageView);
                    imageView.setImageURI(resultUri);
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap_orig = drawable.getBitmap();

                    Bitmap bitmap = enhanceImageDPI(bitmap_orig, 300);


                    //create image object to be read by visionmlkit
                    InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);

                    //instance of text recognizer
                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    //task that processes the image
                    Task<Text> resultText = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            String a = visionText.getText();
                            displayText.setText(a);
                            String b = a.toLowerCase();
                        }
                    }).addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        }

        }

    private void speak() {

        if(voice != null) {
            if (!voice.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (Voice tmpVoice : textToSpeech.getVoices()) {
                        if (tmpVoice.getName().equals(voice)) {
                            textToSpeech.setVoice(tmpVoice);
                            break;
                        }
                    }
                }

            }
        }

        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }
    public void ocr_To_med(View view) {
       finish();

    }


    public Bitmap enhanceImageDPI(Bitmap image, int dpi) {
        ByteArrayOutputStream imageByteArray = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArray);
        byte[] imageData = imageByteArray.toByteArray();
        setDpi(imageData, 300);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return bitmap;
    }
    public void setDpi(byte[] imageData, int dpi) {
        imageData[13] = 1;
        imageData[14] = (byte) (dpi >> 8);
        imageData[15] = (byte) (dpi & 0xff);
        imageData[16] = (byte) (dpi >> 8);
        imageData[17] = (byte) (dpi & 0xff);
    }


}
