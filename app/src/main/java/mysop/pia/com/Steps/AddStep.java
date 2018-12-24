package mysop.pia.com.Steps;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import mysop.pia.com.ListofSOPs.ListofSOPs;
import mysop.pia.com.R;
import mysop.pia.com.Steps.StepsRoom.StepsAppDatabase;
import mysop.pia.com.Steps.StepsRoom.StepsRoomData;

public class AddStep extends AppCompatActivity {

    private static final String TAG = "test";
    @BindView(R.id.textview_add_sop_step_count)
    TextView textviewStepCount;
    @BindView(R.id.edittext_add_step_title)
    EditText ediitTextStepTitle;
    @BindView(R.id.button_add_sop_another_step)
    Button buttonAddAnotherStep;
    @BindView(R.id.button_add_sop_save)
    Button buttonCompleteSOP;
    @BindView(R.id.edittext_add_step_description)
    EditText edittextDescription;
    @BindView(R.id.imageview_add_step_gallery)
    ImageView imageviewGallery;
    @BindView(R.id.imageview_add_step_camera)
    ImageView imageviewCamera;
    @BindView(R.id.imageview_add_step_image_preview)
    ImageView imageviewImagePreview;

    public static final int PICK_IMAGE = 1;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_step);
        ButterKnife.bind(this);
//      GETS SOPTITLE FROM ADD SOP INTENT
        String sopTitle = getIntent().getStringExtra("sopTitle");
        int stepNumber = getIntent().getIntExtra("stepNumber", 1);
        setStepText(stepNumber);
        pickImageFromGallery();

//      DO THIS IF SOP IS COMPLETED
        buttonCompleteSOP.setOnClickListener(v -> {
            AddStepToRoomDatabase(sopTitle, stepNumber);

            Intent returnToListOfSOPs = new Intent(this, ListofSOPs.class);
            startActivity(returnToListOfSOPs);
            finish();
        });
//        DO THIS IF ANOTHER STEP IS ADDED
        buttonAddAnotherStep.setOnClickListener((View v) -> {
            AddStepToRoomDatabase(sopTitle, stepNumber);

            Intent nextStep = new Intent(this, AddStep.class);
            int nextStepNum = stepNumber + 1;
            nextStep.putExtra("stepNumber", nextStepNum);
            nextStep.putExtra("sopTitle", sopTitle);
            startActivity(nextStep);
            finish();
        });
    }

    private void AddStepToRoomDatabase(String sopTitle, int stepNumber){
        String stepTitle = ediitTextStepTitle.getText().toString();
        String stepDescription = edittextDescription.getText().toString();

//            SAVE STEPS FOR SOP
        StepsRoomData newStep = new StepsRoomData(sopTitle, stepTitle, stepNumber,stepDescription, imageUri.toString());
        stepsRoomDatabase().listOfSteps().insertSteps(newStep);
    }

    public StepsAppDatabase stepsRoomDatabase() {
        return Room.databaseBuilder(getApplicationContext(), StepsAppDatabase.class, "steps")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    public void setStepText(int stepNumber){
        String stepConcat = "Step" + stepNumber;
        textviewStepCount.setText(stepConcat);
    }

    public void pickImageFromGallery(){
        imageviewGallery.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(resultCode == RESULT_OK)
                imageUri = data.getData();
            imageviewImagePreview.setVisibility(View.VISIBLE);
            imageviewImagePreview.setImageURI(imageUri);
            imageviewImagePreview.invalidate();
        }
    }
}
