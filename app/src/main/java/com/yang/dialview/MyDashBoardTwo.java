package com.yang.dialview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * 时间：2018/12/29 10:07
 * 描述:我的自定义表盘
 */
public class MyDashBoardTwo extends View {
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint outPaint;//外圆环画笔
    private Paint inPaint;//内圆环画笔
    private Paint textPaint;//刻度字画笔
    private Paint scalePaint;//刻度画笔
    private Paint circleInPaint;//最里面圆画笔
    private Paint circleMidPaint;//中间层圆画笔
    private Paint circleOutPaint;//外层圆画笔

    private double defaultWidth = 400f;//控件默认宽度
    double a = Math.toRadians(15);//多出来的角度
    private double defaultHigth =  defaultWidth/2f*(Math.sin(a)+ 1);//控件默认高度
    private int outWidth;//外圆宽度  dp
    private int inWidth;//内圆宽度  dp
    private int outToIn;//外圆和内圆的间距 dp
    private float textSize;//刻度的大小
    private int padOut;//外圆距离控件的边上的距离
    private float circleIn;//内圆心大小
    private float circleMiddle;//中心圆大小
    private float circleOut;//在外面圆大小

    private int outColor;
    private String title;//标题
    public MyDashBoardTwo(Context context) {
        super(context, null);
    }

    public MyDashBoardTwo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MyDashBoardTwo);
        outColor = attributes.getColor(R.styleable.MyDashBoardTwo_outColor, Color.parseColor("#B7DEFF"));
        per = perPoint = attributes.getFloat(R.styleable.MyDashBoardTwo_num, 0f);
        title = attributes.getString(R.styleable.MyDashBoardTwo_title);
        init();
    }

    /**
     * 初始化各控件
     */
    private void  init() {
        outPaint = new Paint();
        inPaint = new Paint();
        circleInPaint = new Paint();
        circleMidPaint = new Paint();
        circleOutPaint = new Paint();
        textPaint = new Paint();
        scalePaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        double width = getMySize(defaultWidth, widthMeasureSpec);
        double higth = getMySize(defaultHigth, widthMeasureSpec);

        if ((higth-width/2f/70f*7f) > width/2d*(Math.sin(a)+ 1)) {
            higth = width/2d*(Math.sin(a)+ 1) + width/2f/70f*7f;
        }else {
            width = (higth-width/2f/70f*7f)/(Math.sin(a)+ 1)*2d;
        }

        mCenterY = mCenterX = (float) width/2f;
        outWidth = (int) (mCenterX/70f*2f);
        inWidth = (int) (mCenterX/70f*7f);
        outToIn = (int) (mCenterX/70f*4f);
        padOut = (int)  (mCenterX/70f*7f);
        textSize = mCenterX/70f*5;
        circleIn = mCenterX/70f*2.3f;
        circleMiddle = mCenterX/70f*3.2f;
        circleOut = mCenterX/70f*3.6f;
        setMeasuredDimension((int) width, (int) higth);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        drawOutCircle(canvas);
        drawInCircle(canvas);
        drawScale(canvas);
        drawTextPath(canvas);
        drawPointer(canvas);
        drawTitleAndPonit(canvas);
    }

    /**
     * 画外环
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.save();
        outPaint.setColor(outColor);
        outPaint.setStrokeWidth(outWidth);
        outPaint.setStyle(Paint.Style.STROKE);
//        outPaint.setStrokeCap(Paint.Cap.ROUND);//作用于圆环结尾
        int temp = outWidth/2 + padOut;
        RectF rectF = new RectF(temp, temp, 2*mCenterX-temp, 2*mCenterX-temp);
        canvas.drawArc(rectF, 165, 210, false, outPaint);
        canvas.restore();
    }

    /**
     * 画内环
     * @param canvas
     */
    private void drawInCircle(Canvas canvas) {
        canvas.save();
        //渐变的颜色是360度，如果只显示270，那么则会缺失部分颜色
        SweepGradient mSweepGradient;
        int[] mGradientColors = {Color.parseColor("#01E330"), Color.parseColor("#FF5816"), Color.parseColor("#FF5816"),Color.parseColor("#FFEE1A"),Color.parseColor("#FFEE1A"), Color.parseColor("#01E330")};
        // 设置渐变
        float[] positions = new float[]{0f, 0.45f, 0.55f, 0.70f, 0.77f, 1f};
        mSweepGradient = new SweepGradient(mCenterX, mCenterY, mGradientColors, positions);
        inPaint.setShader(mSweepGradient);

//        inPaint.setColor(Color.RED);
        inPaint.setStrokeWidth(inWidth);
        inPaint.setStyle(Paint.Style.STROKE);
        int temp = outWidth+outToIn+inWidth/2 + padOut;
        RectF rectF = new RectF(temp, temp, 2*mCenterX-temp, 2*mCenterX-temp);
        canvas.drawArc(rectF, 165, 210, false, inPaint);
        canvas.restore();
    }

    /**
     * 画刻度
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        canvas.save();
        scalePaint.setColor(Color.WHITE);
        scalePaint.setStrokeWidth(dp2px(1));

        int mStartAngle = 165;
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float mRadius = mCenterX-outWidth-outToIn - padOut;
        float temp = outWidth + outToIn + padOut;
        float x0 = (float) (temp + mRadius * (1 - cos));
        float y0 = (float) (temp + mRadius * (1 - sin));
        float x1 = (float) (temp + mRadius - (mRadius - mCenterX/70f*7f) * cos);
        float y1 = (float) (temp + mRadius - (mRadius - mCenterX/70f*7f) * sin);
        canvas.drawLine(x0, y0, x1, y1, scalePaint);
        float angle = 210 * 1f / 10;//刻度分10份
        for (int i = 0; i < 10; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, scalePaint);
        }
        canvas.restore();
    }

    /**
     * 沿着圆环画刻度数字
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawTextPath(Canvas canvas) {
        canvas.save();
        textPaint.setColor(Color.parseColor("#ACA8A8"));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        float mRadius = padOut + outWidth+outToIn+inWidth+mCenterX/70f*5.5f;//字的圆弧半径
        float angle = 210 * 1f / 10;//刻度分10份
        for (int i = 0; i < 11; i++) {
            Path path = new Path();
            path.addArc(mRadius , mRadius, mCenterX*2-mRadius, mCenterY*2-mRadius, 165 + (i-1)*angle, 2*angle);
            canvas.drawTextOnPath(String.valueOf(i*10),  path, 0, 0, textPaint);
        }
        canvas.restore();
    }

    /**
     * 画刻度数字（数字都是正的）
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        canvas.save();
        textPaint.setColor(Color.parseColor("#ACA8A8"));
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(textSize);
        float α;
        float[] p;
        int mSection = 10;//分几段
        int mStartAngle = 165;//起始角度
        float angle = 210 * 1f / mSection;//刻度分10份
        float mRadius = mCenterX-outWidth-outToIn-inWidth-padOut;//字的圆弧半径
        float temp = mCenterX/70f*4;//字距离刻度的距离
        for (int i = 0; i <= mSection; i++) {
            α = mStartAngle + angle * i;
            p = getCoordinatePoint((int) (mRadius - temp), α);
            if (α % 360 > 135 && α % 360 < 225) {
                textPaint.setTextAlign(Paint.Align.LEFT);
            } else if ((α % 360 >= 0 && α % 360 < 45) || (α % 360 > 315 && α % 360 <= 360)) {
                textPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                textPaint.setTextAlign(Paint.Align.CENTER);
            }
//            int txtH = mRectText.height();
            int txtH = (int) (textSize/5f*3f);//数字所占的空间
            if (i <= 1 || i >= (mSection-1)) {
                canvas.drawText(String.valueOf((i) * mSection), p[0], p[1] + txtH / 2, textPaint);
            } else if (i == 3) {
                canvas.drawText(String.valueOf((i) * mSection), p[0] + txtH / 2, p[1] + txtH, textPaint);
            } else if (i == (10 - 3)) {
                canvas.drawText(String.valueOf((i) * mSection), p[0] - txtH / 2, p[1] + txtH, textPaint);
            } else {
                canvas.drawText(String.valueOf((i) * mSection), p[0], p[1] + txtH, textPaint);
            }
        }
        canvas.restore();
    }

    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }


    private float per = 0;
    private float perOld;
    private float perPoint;
    /**
     * 画指针
     * @param canvas
     */
    private void drawPointer(Canvas canvas) {
        canvas.save();
        circleInPaint.setColor(Color.parseColor("#5DB5FF"));
        circleMidPaint.setColor(Color.parseColor("#CDE8FF"));
        circleOutPaint.setColor(Color.parseColor("#FFFFFF"));
        float pointLength =mCenterX - (outWidth/2 + padOut);//指针的长度
        float angle = 165f + 210f/100f*perPoint;

        //绘制三角形形成指针
        if (angle <= 180) {
            angle = 180 - angle;
            canvas.drawCircle((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))), circleOut, circleOutPaint);
            canvas.drawCircle((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))), circleMiddle, circleMidPaint);
            canvas.drawCircle((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))), circleIn, circleInPaint);
        }else if (angle <= 270) {
            angle = angle - 180;
            canvas.drawCircle((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))), circleOut, circleOutPaint);
            canvas.drawCircle((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))), circleMiddle, circleMidPaint);
            canvas.drawCircle((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))), circleIn, circleInPaint);
        }else if (angle <= 360) {
            angle = 360 - angle;
            canvas.drawCircle((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))), circleOut, circleOutPaint);
            canvas.drawCircle((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))), circleMiddle, circleMidPaint);
            canvas.drawCircle((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))), circleIn, circleInPaint);
        }else if (angle <= 450) {
            angle = angle - 360;
            canvas.drawCircle((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))), circleOut, circleOutPaint);
            canvas.drawCircle((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))), circleMiddle, circleMidPaint);
            canvas.drawCircle((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))), circleIn, circleInPaint);
        }
        canvas.restore();
    }

    /**
     * 画数字显示和标题
     * @param canvas
     */
    private void drawTitleAndPonit(Canvas canvas) {
        canvas.save();
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.parseColor("#FFB018"));
        textPaint.setTextSize(mCenterX/70f*20);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText((int)per + "分", mCenterX, mCenterY-mCenterX/70f*6, textPaint);

        if (!TextUtils.isEmpty(title)) {
            textPaint.setTypeface(Typeface.DEFAULT);
            textPaint.setColor(Color.parseColor("#333333"));
            textPaint.setTextSize(mCenterX/70f*8.5f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(title, mCenterX, mCenterY+mCenterX/70f*14, textPaint);
        }
        canvas.restore();
    }

    private double getMySize(double defaultSize, int measureSpec) {
        double mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
            default:
                break;
        }
        return mySize;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    //设置刻度设置动画
    public void setNumber(float per){
        this.perOld = this.per;
        this.per = per;
        ValueAnimator va =  ValueAnimator.ofFloat(perOld,per);
        va.setDuration(1000);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                perPoint = (float) animation.getAnimatedValue();
                if (perPoint > 100) {
                    perPoint = 100f;
                }
                invalidate();
            }
        });
        va.start();
    }
}
