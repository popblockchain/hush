package com.stealth.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ImageViewSlice extends View {
	private int nRows = 1;
	private int nCols = 1 ;

	private int nTargetRow = 0 ;
	private int nTargetCol = 0 ;

	int width ;
	int height ;
	Bitmap imgOrg ;
    Bitmap imgDest ;

	Context mContext ;
	int widthSlice ;
	int heightSlice ;

	public ImageViewSlice(Context context) {
		super(context);
	}

	public void setSlices(int rows, int cols) {
		nRows = rows;
		nCols = cols;
		//System.out.println("rHeight==>"+rHeight);
	}

	public void setTargetSlice(int nRow, int nCol)
	{
		nTargetRow = nRow ;
		nTargetCol = nCol ;
	}

	/*
	public void setImgResource(Context context, int resId)
	{
		imgOrg = BitmapFactory.decodeResource(context.getResources(), resId);
        width = imgOrg.getWidth() ; // getMeasuredWidth();
        height = imgOrg.getHeight() ; //getMeasuredHeight();
        widthSlice = width / nRows ;
        heightSlice = height / nCols ;

        imgDest = Bitmap.createBitmap(imgOrg,  nTargetCol*widthSlice, nTargetRow*heightSlice,  widthSlice, heightSlice);
    }
    */

	public void setImgResource(Bitmap bitmap)
	{
		if (bitmap == null)
			return ;

		imgOrg = bitmap ;
		width = imgOrg.getWidth() ; // getMeasuredWidth();
		height = imgOrg.getHeight() ; //getMeasuredHeight();
		widthSlice = width / nRows ;
		heightSlice = height / nCols ;

		imgDest = Bitmap.createBitmap(imgOrg,  nTargetCol*widthSlice, nTargetRow*heightSlice,  widthSlice, heightSlice);
	}

	public ImageViewSlice(Context context, AttributeSet attr) {
		super(context, attr);
	}


	@Override
	protected void onDraw(Canvas canvas) 
	{
        Paint paint = new Paint();
        //canvas.drawBitmap(imgOrg, 0,0, paint); // imgDest ?
        int canvW = canvas.getWidth() ;
        int canvH = canvas.getHeight() ;

        paint.setColor(Color.DKGRAY);
        canvas.drawRect(0,0,canvW,canvH,paint);

        //draw portion of bitmap
		if (imgDest != null)
            canvas.drawBitmap(imgDest,new Rect(0, 0,imgDest.getWidth(),imgDest.getHeight()),new Rect(4,4,canvW-4,canvH-4),paint);
	}
}
