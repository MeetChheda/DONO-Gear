package com.example.donogear.interfaces;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.widget.Button;

public interface ButtonDesign {

    /**
     * Defines layout for a button, such as color, position, size etc
     * @param button - a single button
     * @param borderColor - color for the border
     * @param bgColor - background color, for the button
     */
    static void setButtonLayout(Button button,int borderColor, int bgColor) {
        button.setTextColor(borderColor);
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
