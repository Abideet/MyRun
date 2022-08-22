package uk.aston.placestest;

import static uk.aston.placestest.ViewJourneyActivity.EXTRA_DATA_ID;
import static uk.aston.placestest.ViewJourneyActivity.EXTRA_DATA_UPDATE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

//Look at why this activity is not updating the name correctly


//Journey name not updating correctly
//something wrong in this class
//TODO: compare with NewWordActivity class in RoomWordSample

public class UpdateJourneyActivity extends AppCompatActivity
{

    public static final String EXTRA_REPLY = "uk.aston.myrunnerapp.REPLY";
    public static final String EXTRA_REPLY_ID = "uk.aston.myrunnerapp.REPLY_ID";

    private EditText mEditWordView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_save_name);

        mEditWordView = findViewById(R.id.editTexRunName);
        int id = -1 ;

        final Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            String name = extras.getString(EXTRA_DATA_UPDATE_NAME, "");
            if (!name.isEmpty())
            {
                mEditWordView.setText(name);
                mEditWordView.setSelection(name.length());
                mEditWordView.requestFocus();
            }
        } // Otherwise, start with empty fields.


        final Button button = findViewById(R.id.saveNameButton);

        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // Create a new Intent for the reply.
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWordView.getText())) {
                    // No word was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // Get the new word that the user entered.
                    String word = mEditWordView.getText().toString();
                    // Put the new word in the extras for the reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY, word);
                    if (extras != null && extras.containsKey(EXTRA_DATA_ID)) {
                        int id = extras.getInt(EXTRA_DATA_ID, -1);
                        if (id != -1)
                        {
                            replyIntent.putExtra(EXTRA_REPLY_ID, id);
                        }
                    }
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }






}
