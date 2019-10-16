package com.example.search;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
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
    boolean[] buttonClicked;
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
        buttonClicked = new boolean[tags.size()];
        for (int j = 0; j <= tags.size(); j += 4) {
            LinearLayout newLayout = new LinearLayout(this);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < 4 && j + i < tags.size(); i++) {
                final Button button = new Button(this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(8, 15, 8, 0);
                button.setLayoutParams(layoutParams);
                button.setId(j + i);
                button.setText(tags.get(i + j));
                setButtonLayout(button, Color.BLACK, Color.WHITE);
                //AddButtonLayout(button, position[i]);
                newLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleButton(button);
                        Toast.makeText(MainActivity.this, button.getText().length() + " items found ", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            linearLayout.addView(newLayout);
        }
    }

    private void toggleButton(Button button) {
        if (!buttonClicked[button.getId()]) {
            setButtonLayout(button, Color.WHITE, Color.BLACK);
            button.setTextColor(Color.WHITE);
        } else {
            setButtonLayout(button, Color.BLACK, Color.WHITE);
            button.setTextColor(Color.BLACK);
        }
        buttonClicked[button.getId()] = !buttonClicked[button.getId()];
    }

    private void setButtonLayout(Button button, int borderColor, int bgColor) {
        float[] outerRadii = new float[]{75,75,75,75,75,75,75,75};
        float[] innerRadii = new float[]{75,75,75,75,75,75,75,75};
        ShapeDrawable borderDrawable = new ShapeDrawable(new RoundRectShape(
                outerRadii,
                null,
                innerRadii
        ));
        borderDrawable.getPaint().setColor(borderColor);
        borderDrawable.getPaint().setStyle(Paint.Style.FILL);
        // Define the border width
        borderDrawable.setPadding(5,5,5,5);
        // Set the shape background
        ShapeDrawable backgroundShape = new ShapeDrawable(new RoundRectShape(
                outerRadii,
                null,
                innerRadii
        ));
        backgroundShape.getPaint().setColor(bgColor); // background color
        backgroundShape.getPaint().setStyle(Paint.Style.FILL); // Define background
        backgroundShape.getPaint().setAntiAlias(true);

        // Initialize an array of drawables
        Drawable[] drawables = new Drawable[]{ borderDrawable, backgroundShape };
        backgroundShape.setPadding(10,10,10,10);
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        button.setBackground(layerDrawable);
    }

}
