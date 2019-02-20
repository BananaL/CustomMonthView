package com.openxu.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 月份自定义控件
 * Created By lixiangjiao on 2019-02-12
 */
public class CustomMonth extends View {
    //各背景颜色
    private int mTitleBg,mMonthBg,mValueBg;
    //标题颜色大小
    private int mTitleTextColor;
    private float mTitleTextSize,mTitleHeight;
    //标题栏按钮
    private int mYearRowL, mYearhRowR;
    //月份颜色大小
    private int mMonthTextColor;
    private float mMonthTextSize;
    //月份值  颜色大小
    private int mValueTextColor,mValueSelectedBg;
    private float mValueTextSize,mValueHeight;
    //水平分割线颜色大小
    private int mSpaceLineColor;
    private float mSpaceLineHeight;

    private int currentYear;
    private int currentMonth = -1;


    private int columnWidth;       //每列宽度
    private float viewTotalHight ;   //view的总高度
    private Paint mPaint;
    private Paint bgPaint;

    private int currentClickValueIndex = -1;


    //左右年份时间点击位置处理
    float yearStartY = 0f;
    float yearEndY   = 0f;
    float yearLeftStartX = 0f;
    float yearLeftEndX = 0f;
    float yearRightStartX = 0f;
    float yearRightEndX = 0f;

    Calendar calendar;

    private List<String> dataLists = null;
    public CustomMonth(Context context) {
        this(context,null);
    }

    public CustomMonth(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomMonth(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.CustomMonth,defStyleAttr,0);

        mTitleBg = a.getColor(R.styleable.CustomMonth_mTitleBg,Color.GRAY);
        mMonthBg = a.getColor(R.styleable.CustomMonth_mMonthBg,Color.LTGRAY);
        mValueBg = a.getColor(R.styleable.CustomMonth_mValueBg,Color.WHITE);

        mTitleTextColor = a.getColor(R.styleable.CustomMonth_mTitleTextColor,Color.BLACK);
        mTitleHeight    = a.getDimension(R.styleable.CustomMonth_mTitleHeight,80);
        mTitleTextSize  = a.getDimension(R.styleable.CustomMonth_mTitleTextSize,40);

        mYearRowL  = a.getResourceId(R.styleable.CustomMonth_mYearRowL,R.drawable.custom_calendar_row_left);
        mYearhRowR = a.getResourceId(R.styleable.CustomMonth_mYearRowR,R.drawable.custom_calendar_row_right);

        mMonthTextColor = a.getColor(R.styleable.CustomMonth_mMonthTextColor,Color.BLACK);
        mMonthTextSize  = a.getDimension(R.styleable.CustomMonth_mMonthTextSize,30);

        mValueTextColor   = a.getColor(R.styleable.CustomMonth_mValueTextColor,Color.BLACK);
        mValueSelectedBg  = a.getColor(R.styleable.CustomMonth_mValueSelectedBg,Color.GREEN);
        mValueTextSize    = a.getDimension(R.styleable.CustomMonth_mValueTextSize,30);
        mValueHeight      = a.getDimension(R.styleable.CustomMonth_mValueHeight,40);

        mSpaceLineColor  = a.getColor(R.styleable.CustomMonth_mSpaceLineColor,Color.GREEN);
        mSpaceLineHeight = a.getDimension(R.styleable.CustomMonth_mSpaceLineHeight,2);

        a.recycle();

        init();
    }

    public void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        calendar = Calendar.getInstance();
        currentYear  = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawContent(canvas);//画第一条线
        drawTitle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        columnWidth = (int) ((widthSize-5*mSpaceLineHeight)/6);//计算每一列的宽度
        viewTotalHight = mSpaceLineHeight*5+mValueHeight*4+mTitleHeight;//计算整个view的高度
        Log.d("LXJ","widthSize = "+widthSize+":::::::heightSize = "+viewTotalHight);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec), (int) viewTotalHight);
    }

    /**
     * "2019"
     * @param year
     */
    public void setYear(int year){
           currentYear = year;
           invalidate();//重绘界面
    }

    public void drawTitle(Canvas canvas){
        //画背景
        bgPaint.setColor(mTitleBg);//标题栏背景
        RectF rectF = new RectF(0,0,getWidth(),mTitleHeight);
        canvas.drawRect(rectF,bgPaint);
        //画年份
        mPaint.setTextSize(mTitleTextSize);
        mPaint.setColor(mTitleTextColor);
        float textLen = FontUtil.getFontlength(mPaint, String.valueOf(currentYear));
        float textHight = FontUtil.getFontHeight(mPaint);
        float textStartw = (getWidth() - textLen)/ 2;
        float textStartH = (mTitleHeight-textHight)/2;
        canvas.drawText(String.valueOf(currentYear),textStartw,textStartH+FontUtil.getFontLeading(mPaint),mPaint);

        //画箭头 左边箭头
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.custom_calendar_row_left);
        int bitmapH = bitmap.getHeight();
        int bitmapW = bitmap.getWidth();
        float bitmapLStart = textStartw - 70;
        yearLeftStartX = bitmapLStart-20;
        yearLeftEndX = yearLeftStartX+bitmapW+40;
        yearStartY = (mTitleHeight-bitmapH)/2-10;
        yearEndY = yearStartY+bitmapH+20;
        canvas.drawBitmap(bitmap,bitmapLStart,(mTitleHeight-bitmapH)/2,new Paint());
        //画右边箭头
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.custom_calendar_row_right);
        float bitmapRStart = textStartw+textLen+70-bitmapW;
        yearRightStartX = bitmapRStart-20;
        yearRightEndX = yearRightStartX+bitmapW+40;
        canvas.drawBitmap(bitmap,bitmapRStart,(mTitleHeight-bitmapH)/2,new Paint());
        bitmap.recycle();
    }

    public void drawContent(Canvas canvas){
        RectF rectF = null;
        //画背景
        bgPaint.setColor(mSpaceLineColor);//标题栏背景
        rectF = new RectF(0,0,getWidth(),viewTotalHight);
        canvas.drawRect(rectF,bgPaint);
        float widthStart = 0;
        float hightStart = 0;
        float hightBigin = mTitleHeight+mSpaceLineHeight;
        float widthEnd = 0;
        float hightEnd = 0;
        int count = 0;
         for(int i = 0;i<4;i++){
               //每行   计算rect的起点
               hightStart = hightBigin+mValueHeight*i+mSpaceLineHeight*i;
               hightEnd = hightStart+mValueHeight;
               if(i%2==0){
                   //月份
                   String [] month = null;
                   if(i==0){
                       month = new String[]{"一月","二月","三月","四月","五月","六月"};
                   }else if(i==2){
                       month = new String[]{"七月","八月","九月","十月","十一月","十二月"};
                   }
                   for(int j = 0;j<6;j++){
                       //每列
                       widthStart = columnWidth*j+mSpaceLineHeight*j;
                       widthEnd = widthStart+columnWidth;
                       bgPaint.setColor(mMonthBg);
                       rectF = new RectF(widthStart,hightStart,widthEnd,hightEnd);
                       Log.d("LXJ","左上角：（"+ widthStart+"."+hightStart+"):::右下角：（"+ widthEnd+"."+hightEnd+")");
                       canvas.drawRect(rectF,bgPaint);

                       //画月份名字
                       mPaint.setTextSize(mMonthTextSize);
                       mPaint.setColor(mMonthTextColor);
                       float textLen = FontUtil.getFontlength(mPaint,month[j]);
                       float textHight = FontUtil.getFontHeight(mPaint);
                       canvas.drawText(month[j],((columnWidth-textLen)/2)+widthStart,((mValueHeight-textHight)/2)+hightStart+FontUtil.getFontLeading(mPaint),mPaint);
                   }
               }else{
                   //月份对应的值
                   for(int j = 0;j<6;j++){
                       //每列
                       widthStart = columnWidth*j+mSpaceLineHeight*j;
                       widthEnd = widthStart+columnWidth;
                       if(currentMonth==count){
                           //先画背景
                           if(currentClickValueIndex==-1){
                               bgPaint.setColor(mValueSelectedBg);
                           }else{
                               bgPaint.setColor(mValueBg);
                           }
                           rectF = new RectF(widthStart,hightStart,widthEnd,hightEnd);
                           canvas.drawRect(rectF,bgPaint);

                           //再画虚线
                           rectF = new RectF(widthStart+2,hightStart+2,widthEnd-2,hightEnd-2);//保证虚线不会超出背景位置
                           bgPaint.setColor(mValueSelectedBg);
                           bgPaint.setStyle(Paint.Style.STROKE);
                           PathEffect effect = new DashPathEffect( new float[]{5, 10, 15,20 },0);
                           bgPaint.setPathEffect(effect);
                           bgPaint.setStrokeWidth(3);
                           canvas.drawRect(rectF,bgPaint);
                       }else{

                           if(currentYear!=calendar.get(Calendar.YEAR)&&count==0&&currentClickValueIndex==-1){
                               //不是当前年份的时候，默认选中一月
                               bgPaint.setColor(mValueSelectedBg);
                           }else{
                               bgPaint.setColor(mValueBg);
                           }


                           bgPaint.setStyle(Paint.Style.FILL);
                           rectF = new RectF(widthStart,hightStart,widthEnd,hightEnd);
                           Log.d("LXJ","左上角：（"+ widthStart+"."+hightStart+"):::右下角：（"+ widthEnd+"."+hightEnd+")");
                           canvas.drawRect(rectF,bgPaint);
                       }

                       if(currentClickValueIndex==count){
                           bgPaint.setStyle(Paint.Style.FILL);
                           bgPaint.setColor(mValueSelectedBg);
                           rectF = new RectF(widthStart,hightStart,widthEnd,hightEnd);
                           Log.d("LXJ","左上角：（"+ widthStart+"."+hightStart+"):::右下角：（"+ widthEnd+"."+hightEnd+")");
                           canvas.drawRect(rectF,bgPaint);
                       }

//                       //画月对应的值
                       mPaint.setTextSize(mValueTextSize);
                       mPaint.setColor(mValueTextColor);
                       float textLen = FontUtil.getFontlength(mPaint,dataLists.get(count));
                       float textHight = FontUtil.getFontHeight(mPaint);
                       canvas.drawText(dataLists.get(count),((columnWidth-textLen)/2)+widthStart,((mValueHeight-textHight)/2)+hightStart+FontUtil.getFontLeading(mPaint),mPaint);
                       count++;
                   }
               }

         }
    }

    public void setDataList(List<String> list) throws DataListErrorException {
        if(list!=null&&list.size()==12){
            this.dataLists = list;
        }else{
            this.dataLists = new ArrayList<>();
            throw new DataListErrorException("传入数组大小只能是size = 12");
        }

    }

    public class DataListErrorException extends Exception{
        public DataListErrorException(String message) {
            super(message);
        }
    }

    //点击事件处理 
    private PointF focusPoint = new PointF();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        focusPoint.set(event.getX(),event.getY());
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch(action){
            case MotionEvent.ACTION_UP:
                handleTouchPoint(focusPoint);
                break;
            case MotionEvent.ACTION_DOWN:
//                handleTouchPoint(focusPoint);
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    public void yearChangeListener(){
        int year  = calendar.get(Calendar.YEAR);
        int mouth = calendar.get(Calendar.MONTH);
        if(currentYear == year){
            currentMonth = mouth;
        }else{
            currentMonth = -1;
        }
    }

    public void handleTouchPoint(PointF point){

        //处理年份增减的点击
        if(yearStartY<point.y&&point.y<yearEndY){
            if(point.x>yearLeftStartX&&point.x<yearLeftEndX){
                //年份减
                currentYear--;
                currentClickValueIndex = -1;
                yearChangeListener();
            }

            if(point.x>yearRightStartX&&point.x<yearRightEndX){
                //年份加
                currentYear++;
                currentClickValueIndex = -1;
                yearChangeListener();
            }
            invalidate();
            return;
        }

        //点击月份值
        //第一排月份值起点位置
        float oneLineStartY = (int) (mTitleHeight+mSpaceLineHeight*2+mValueHeight);
        float oneLineEndY   = oneLineStartY+mValueHeight;

        float twoLineStartY = oneLineEndY+mSpaceLineHeight+mValueHeight;
        float twoLineEndY   = twoLineStartY+mValueHeight;

        if(oneLineStartY<point.y&&point.y<oneLineEndY){
            int horizontalIndex = (int) (point.x/(columnWidth+mSpaceLineHeight));
            if(horizontalIndex*(columnWidth+mSpaceLineHeight)<point.x){
                Log.d("LXJ_VALUE",dataLists.get(horizontalIndex));
            }
            currentClickValueIndex = horizontalIndex;
            invalidate();
        }else if(twoLineStartY<point.y&&point.y<twoLineEndY){
                int horizontalIndex = (int) (point.x/(columnWidth+mSpaceLineHeight));
                if(horizontalIndex*(columnWidth+mSpaceLineHeight)<point.x){
                    Log.d("LXJ_VALUE",dataLists.get(horizontalIndex+6));
                }
            currentClickValueIndex = horizontalIndex+6;
            invalidate();
        }else if(twoLineEndY<20){
            //头部
        }else{
            //其他区域
        }
    }

    public interface OnMonthViewTouchListener{
        void onLeftYearClick();
        void onRightYEarClick();
        void onMonthValueClick();
    }
}
