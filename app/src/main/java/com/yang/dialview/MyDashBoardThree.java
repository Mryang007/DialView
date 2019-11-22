package com.yang.dialview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
public class MyDashBoardThree extends View {
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint inPaint;//内圆环画笔
    private Paint textPaint;//刻度字画笔
    private Paint scalePaint;//刻度画笔
    private Paint pointerPaint;//画指针
    private Paint centerPaint;//圆心画笔
    private Paint textBg;//文字背景画笔

    private double defaultWidth = 400f;//控件默认宽度
    double a = Math.toRadians(29);//多出来的角度
    private double defaultHigth =  defaultWidth/2f*(Math.sin(a)+ 1);//控件默认高度
    private int inWidth;//内圆宽度  dp
    private int outToIn;//外圆和内圆的间距 dp
    private float textSize;//刻度的大小
    private float circleInBlack;//圆心大小-黑色
    private float circleInWhite;//圆心大小-白色

    private String title;//标题
    public MyDashBoardThree(Context context) {
        super(context, null);
    }

    public MyDashBoardThree(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MyDashBoardThree);
        per = perPoint = attributes.getFloat(R.styleable.MyDashBoardThree_num2, 0f);
        title = attributes.getString(R.styleable.MyDashBoardThree_title2);
        init();
    }

    /**
     * 初始化各控件
     */
    private void  init() {
        inPaint = new Paint();
        textPaint = new Paint();
        scalePaint = new Paint();
        pointerPaint = new Paint();
        centerPaint = new Paint();
        textBg = new Paint();
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
        inWidth = (int) (mCenterX/70f*19f);
        outToIn = (int) (mCenterX/70f*22f);
        textSize = mCenterX/70f*5;
        circleInBlack = mCenterX/70f*6;
        circleInWhite = mCenterX/70f*3.2f;
        setMeasuredDimension((int) width, (int) higth);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        drawCircleCenterBlack(canvas);
        drawInCircle(canvas);
        drawScale(canvas);
        drawTextPath(canvas);
        drawPointer(canvas);
        drawTitleAndPonit(canvas);
        drawCircleCenterWhite(canvas);
        drawText(canvas);
    }

    /**
     * 画圆心--黑色
     */
    private void drawCircleCenterBlack(Canvas canvas) {
        canvas.save();
        centerPaint.setColor(Color.parseColor("#333333"));
        canvas.drawCircle(mCenterX, mCenterY, circleInBlack, centerPaint);
        canvas.restore();
    }

    /**
     * 画圆心--白色
     */
    private void drawCircleCenterWhite(Canvas canvas) {
        canvas.save();
        centerPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, circleInWhite, centerPaint);
        canvas.restore();
    }

    /**
     * 画圆环
     * @param canvas
     */
    private void drawInCircle(Canvas canvas) {
        canvas.save();
        //渐变的颜色是360度，如果只显示270，那么则会缺失部分颜色
        SweepGradient mSweepGradient;
        int[] mGradientColors = {Color.parseColor("#39EF46"), Color.parseColor("#FF4F26"), Color.parseColor("#FF4F26"),Color.parseColor("#FF9519"),Color.parseColor("#FFD200"), Color.parseColor("#39EF46")};
        // 设置渐变
        float[] positions = new float[]{0f, 0.45f, 0.55f, 0.65f, 0.77f, 1f};
        mSweepGradient = new SweepGradient(mCenterX, mCenterY, mGradientColors, positions);
        inPaint.setShader(mSweepGradient);

        inPaint.setStrokeWidth(inWidth);
        inPaint.setStyle(Paint.Style.STROKE);
        int temp = outToIn+inWidth/2;
        RectF rectF = new RectF(temp, temp, 2*mCenterX-temp, 2*mCenterX-temp);
        canvas.drawArc(rectF, 150f, 240, false, inPaint);
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

        float mStartAngle = 157.5f;
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float mRadius = mCenterX-outToIn;
        float temp = outToIn;
        float x0 = (float) (temp + mRadius * (1 - cos));
        float y0 = (float) (temp + mRadius * (1 - sin));
        float x1 = (float) (temp + mRadius - (mRadius - mCenterX/70f*8f) * cos);
        float y1 = (float) (temp + mRadius - (mRadius - mCenterX/70f*8f) * sin);
        canvas.drawLine(x0, y0, x1, y1, scalePaint);
        float angle = 225 * 1f / 50f;//刻度分10份
        for (int i = 1; i <= 50; i++) {
            if (i%5 == 0) {
                scalePaint.setColor(Color.WHITE);
            }else {
                scalePaint.setColor(Color.parseColor("#66FFFFFF"));
            }
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
        textPaint.setColor(Color.parseColor("#99FFFFFF"));
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        float mRadius = outToIn+mCenterX/70f*14f;//字的圆弧半径
        float angle = 225 * 1f / 10;//刻度分10份
            for (int i = 0; i < 11; i++) {
                Path path = new Path();
                path.addArc(mRadius , mRadius, mCenterX*2-mRadius, mCenterY*2-mRadius, 157.5f + (i-1)*angle, 2*angle);
                canvas.drawTextOnPath(String.valueOf(i*10),  path, 0, 0, textPaint);
        }
        canvas.restore();
    }

    /**
     * 画刻度数字（数字都是正的）
     * @param canvas
     */
    @SuppressLint("NewApi")
    private void drawText(Canvas canvas) {
        canvas.save();
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        float textSize = mCenterX/70f*7f;
        textPaint.setTextSize(textSize);

        textBg.setColor(Color.parseColor("#A3E31F"));
        textBg.setStyle(Paint.Style.FILL);

        float α;
        float[] p;
        int mSection = 100;//分几段
        float mStartAngle = 157.5f;//起始角度
        float angle = 225 * 1f / mSection;//刻度分10份
        float mRadius = mCenterX-mCenterX/70f*7f;//字的圆弧半径
        float rectK = mCenterX/70f*7.5f;
        float rectG = mCenterX/70f*4f;

        α = mStartAngle + angle * perPoint;
        p = getCoordinatePoint((int) (mRadius), α);
        int txtH = (int) (textSize/10f*7f);//数字所占的空间
        if (perPoint <= 10 || perPoint >= (mSection-10)) {
            canvas.drawRoundRect(p[0]-rectK, p[1] -rectG, p[0] + rectK, p[1] + rectG, dp2px(2), dp2px(2), textBg);
            canvas.drawText(String.valueOf((int) perPoint), p[0], p[1] + txtH / 2, textPaint);
        } else if (perPoint == 30) {
            canvas.drawRoundRect(p[0] + txtH / 2-rectK, p[1] + txtH/2 -rectG, p[0] + txtH / 2 + rectK, p[1] + txtH/2 + rectG, dp2px(2), dp2px(2), textBg);
            canvas.drawText(String.valueOf((int)perPoint), p[0] + txtH / 2, p[1] + txtH, textPaint);
        } else if (perPoint == (100 - 30)) {
            canvas.drawRoundRect(p[0] - txtH / 2-rectK, p[1] + txtH/2 -rectG, p[0] - txtH / 2 + rectK, p[1] + txtH/2 + rectG, dp2px(2), dp2px(2), textBg);
            canvas.drawText(String.valueOf((int)perPoint), p[0] - txtH / 2, p[1] + txtH, textPaint);
        } else {
            canvas.drawRoundRect(p[0]-rectK, p[1] + txtH/2 -rectG, p[0] + rectK, p[1] + txtH/2 + rectG, dp2px(2), dp2px(2), textBg);
            canvas.drawText(String.valueOf((int)perPoint), p[0], p[1] + txtH, textPaint);
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
        pointerPaint.setColor(Color.parseColor("#333333"));
        float pointLength = mCenterX-outToIn + mCenterX/70f*2.5f;//指针的长度
        float pointWidth = mCenterX/70f*3f;//指针宽度的一半
        float angle = 157.5f + 225f/100f*perPoint;

        //绘制三角形形成指针
        Path path = new Path();
        path.moveTo(mCenterX , mCenterY);
        if (angle <= 180) {
            angle = 180 - angle;
            path.lineTo((float) (mCenterX - pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY - pointWidth * Math.cos(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX + pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY + pointWidth * Math.cos(Math.toRadians(angle))));
        }else if (angle <= 270) {
            angle = angle - 180;
            path.lineTo((float) (mCenterX + pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY - pointWidth * Math.cos(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX - pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX - pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY + pointWidth * Math.cos(Math.toRadians(angle))));
        }else if (angle <= 360) {
            angle = 360 - angle;
            path.lineTo((float) (mCenterX - pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY - pointWidth * Math.cos(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY - pointLength * Math.sin(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX + pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY + pointWidth * Math.cos(Math.toRadians(angle))));
        }else if (angle <= 450) {
            angle = angle - 360;
            path.lineTo((float) (mCenterX + pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY - pointWidth * Math.cos(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX + pointLength * Math.cos(Math.toRadians(angle))), (float) (mCenterY + pointLength * Math.sin(Math.toRadians(angle))));
            path.lineTo((float) (mCenterX - pointWidth * Math.sin(Math.toRadians(angle))), (float) (mCenterY + pointWidth * Math.cos(Math.toRadians(angle))));
        }
        path.close();

        canvas.drawPath(path, pointerPaint);
        canvas.restore();
    }

    /**
     * 画数字显示和标题
     * @param canvas
     */
    private void drawTitleAndPonit(Canvas canvas) {
        canvas.save();
        if (!TextUtils.isEmpty(title)) {
            textPaint.setTypeface(Typeface.DEFAULT);
            textPaint.setColor(Color.parseColor("#333333"));
            textPaint.setTextSize(mCenterX/70f*10f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(title, mCenterX, mCenterY+mCenterX/70f*27, textPaint);
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
