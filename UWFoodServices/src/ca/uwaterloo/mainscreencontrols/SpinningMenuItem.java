package ca.uwaterloo.mainscreencontrols;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ca.uwaterloo.uwfoodservices.R;


public class SpinningMenuItem extends FrameLayout 
implements Comparable<SpinningMenuItem> {

    public ImageView mImage;
    private TextView mText;

    private int index;
    private float currentAngle;
    private float itemX;
    private float itemY;
    private float itemZ;
    private boolean drawn;	

    // It's needed to find screen coordinates
    private Matrix mCIMatrix;

    public SpinningMenuItem(Context context) {

        super(context);
        FrameLayout.LayoutParams params = 
                new FrameLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setLayoutParams(params);

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemTemplate = inflater.inflate(R.layout.main_screen_item, this, true);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "Roboto-Medium.ttf");

        mImage = (ImageView)itemTemplate.findViewById(R.id.item_image);
        mImage.setAlpha(255);
        mText = (TextView)itemTemplate.findViewById(R.id.item_text);
        mText.setTextColor(Color.WHITE);
        mText.setTypeface(tf);

    }

    public ImageView getImageItem()
    {
        return mImage;
    }

    public String getName(){
        return mText.getText().toString();
    }	

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }


    public void setCurrentAngle(float currentAngle) {

        if((index == 0) && (currentAngle > 5)){
        }

        this.currentAngle = currentAngle;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    @Override
    public int compareTo(SpinningMenuItem another) {
        return (int)(another.itemZ - this.itemZ);
    }

    public void setItemX(float x) {
        this.itemX = x;
    }

    public float getItemX() {
        return itemX;
    }

    public void setItemY(float y) {
        this.itemY = y;
    }

    public float getItemY() {
        return itemY;
    }

    public void setItemZ(float z) {
        this.itemZ = z;
    }

    public float getItemZ() {
        return itemZ;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setImageBitmap(Bitmap bitmap){
        mImage.setImageBitmap(bitmap);

    }

    public void setText(String txt){
        mText.setText(txt);
    }

    Matrix getCIMatrix() {
        return mCIMatrix;
    }

    void setCIMatrix(Matrix mMatrix) {
        this.mCIMatrix = mMatrix;
    }	

}