package com.example.search;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> dummyArray = Arrays.asList(
            "Cristiano", "Cristiano Ronaldo", "Jordan", "Michael Jordan", "Ronaldo Shoes", "Ronaldo Jersey",
            "Jordan Air", "Ronaldo United", "Shoes", "Lionel Messi shoes", "Messi jersey"
    );
    List<String> tags = Arrays.asList(
            "Football", "Basketball", "Soccer", "Hockey", "Music", "Theatre", "Shoes", "Jersey",
            "Concert", "Merchandise", "Raffles", "Auctions"
    );
    int[] position = {RelativeLayout.ALIGN_LEFT, RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.ALIGN_RIGHT};
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<> (this, android.R.layout.select_dialog_item, dummyArray);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.BLACK);

        linearLayout = findViewById(R.id.linear);
        addButtons();
    }

    private void addButtons() {
        for (int j = 0; j <= tags.size(); j += 4) {
            LinearLayout newLayout = new LinearLayout(this);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < 4 && j + i < tags.size(); i++) {
                final Button button = new Button(this);
                button.setText(tags.get(i + j));
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 20, 0, 0);
                button.setLayoutParams(layoutParams);
                button.setId(j + i);
                //AddButtonLayout(button, position[i]);
                newLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, button.getText().length() + " items found ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            linearLayout.addView(newLayout);
        }
    }

//    private void AddButtonLayout(Button button, int center) {
//        RelativeLayout.LayoutParams buttonLayoutParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        buttonLayoutParameters.setMargins(0, 0, 0, 0);
//        buttonLayoutParameters.addRule(center);
//        button.setLayoutParams(buttonLayoutParameters);
//    }
}
