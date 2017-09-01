package com.transport.transpotation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.R.attr.radius;

public class CircleView extends View {

    private int circleWidth = 100; // 圆环直径
    private int circleColor = Color.argb(200, 191, 191, 191);
    private int innerCircleColor = Color.rgb(72, 61, 139);
    private int backgroundColor = Color.argb(1,0, 0, 0);
    private Paint paint = new Paint();
    int center = 0;
    int innerRadius = 0;
    private float innerCircleRadius = 0;
    private float smallCircle = 10;

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public Dir dir = Dir.UP;


    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context) {
        super(context);
//        setWillNotDraw(false);

        // paint = new Paint();
    }
    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);

        center = 700 / 2;//最开始SingleCarActivity加载的时候控件没有显示，是因为getWidth()初值为0，
        //问题成功解决
        innerRadius = (center - circleWidth / 2 - 10);// 圆环
        innerCircleRadius = center / 3;
        // this.setOnTouchListener(onTouchListener);
    }

    /**
     * 测量宽度
     *
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == View.MeasureSpec.AT_MOST) {
            result = getWidth();
        } else if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 测量高度
     *
     * @param measureSpec
     * @return
     */
    private int measureHeight(int measureSpec) {

        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        int result = 0;

        if (specMode == View.MeasureSpec.AT_MOST) {

            result = specSize;
        } else if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    /**
     * 开始绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initBackGround(canvas);
        drawDirTriangle(canvas, dir);

    }

    /**
     * 绘制方向小箭头
     *
     * @param canvas
     */
    private void drawDirTriangle(Canvas canvas, Dir dir) {
        paint.setColor(innerCircleColor);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        switch (dir) {
            case UP:
                drawUpTriangle(canvas);
                break;
            case DOWN:
                // drawDownTriangle(canvas);
                break;
            case LEFT:
                drawLeftTriangle(canvas);
                break;
            case RIGHT:
                drawRightTriangle(canvas);
                break;
            case CENTER:
                invalidate();
                break;
            default:
                break;
        }

        paint.setColor(backgroundColor);
        canvas.drawCircle(center, center, smallCircle, paint);
        // canvas.drawText(text, center, center+40, paint);

    }

    /**
     * 绘制向右的小箭头
     *
     * @param canvas
     */
    private void drawRightTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);
        path.lineTo((float) (center + sqrt2), (float) (center - sqrt2));
        path.lineTo((float) (center + pow05), center);
        path.lineTo((float) (center + sqrt2), (float) (center + sqrt2));
        canvas.drawPath(path, paint);
        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center + innerCircleRadius, center, paint);

        drawOnclickColor(canvas, Dir.RIGHT);
    }

    /**
     * 绘制想左的小箭头
     *
     * @param canvas
     */
    private void drawLeftTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);
        path.lineTo((float) (center - sqrt2), (float) (center - sqrt2));
        path.lineTo((float) (center - pow05), center);
        path.lineTo((float) (center - sqrt2), (float) (center + sqrt2));
        canvas.drawPath(path, paint);

        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center - innerCircleRadius, center, paint);

        drawOnclickColor(canvas, Dir.LEFT);

    }

    /**
     * 绘制向下的小箭头
     *
     * @param canvas
     */
//    private void drawDownTriangle(Canvas canvas) {
//        Path path = new Path();
//        path.moveTo(center, center);
//        double sqrt2 = innerCircleRadius / Math.sqrt(2);
//        double pow05 = innerCircleRadius * Math.sqrt(2);
//        path.lineTo((float) (center - sqrt2), (float) (center + sqrt2));
//        path.lineTo(center, (float) (center + pow05));
//        path.lineTo((float) (center + sqrt2), (float) (center + sqrt2));
//        canvas.drawPath(path, paint);
//
//        paint.setColor(backgroundColor);
//        canvas.drawLine(center, center, center, center + innerCircleRadius, paint);
//
//        drawOnclickColor(canvas, Dir.DOWN);
//    }

    /**
     * 点击的时候绘制紫色的扇形
     *
     * @param canvas
     * @param dir
     */
    private void drawOnclickColor(Canvas canvas, Dir dir) {
        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(100);
        switch (dir) {
              case UP:
                  canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                          + innerRadius), 225, 90, false, paint);
                  break;
            //    case DOWN:
            //        canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
            //                + innerRadius), 45, 90, false, paint);
            //        break;
            case LEFT:
                canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                        + innerRadius), 135, 90, false, paint);
                break;
            case RIGHT:
                canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                        + innerRadius), -45, 90, false, paint);
                break;

            default:
                break;
        }

        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * 绘制像向上的箭头
     *
     * @param canvas
     */
    private void drawUpTriangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(center, center);
        double sqrt2 = innerCircleRadius / Math.sqrt(2);
        double pow05 = innerCircleRadius * Math.sqrt(2);

        path.lineTo((float) (center - sqrt2), (float) (center - sqrt2));
        path.lineTo(center, (float) (center - pow05));
        path.lineTo((float) (center + sqrt2), (float) (center - sqrt2));
        canvas.drawPath(path, paint);

        paint.setColor(backgroundColor);
        canvas.drawLine(center, center, center, center - innerCircleRadius, paint);

        drawOnclickColor(canvas, Dir.UP);
    }

    /**
     * 绘制基本的背景， 这包括了三个步骤：1.清空画布 2.绘制外圈的圆 3.绘制内圈的圆
     *
     * @param canvas
     */
    private void initBackGround(Canvas canvas) {
        clearCanvas(canvas);
        drawBackCircle(canvas);
        drawInnerCircle(canvas);

    }

    /**
     * 绘制中心紫色小圆
     *
     * @param canvas
     */
    private void drawInnerCircle(Canvas canvas) {
        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        canvas.drawCircle(center, center, innerCircleRadius, paint);
    }

    /**
     * 绘制背景的圆圈和隔线
     *
     * @param canvas
     */
    private void drawBackCircle(Canvas canvas) {

        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(100);
        canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                + innerRadius), 135, 90, false, paint);

        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(100);
        canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                + innerRadius), -45, 90, false, paint);

        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(100);
        canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                + innerRadius), 225, 90, false, paint);

        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(100);
        canvas.drawArc(new RectF(center - innerRadius, center - innerRadius, center + innerRadius, center
                + innerRadius), 45, 90, false, paint);


        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(4);
        canvas.drawLine(center, center, 0, 0, paint);
        canvas.drawLine(center, center, center * 2, 0, paint);
        canvas.drawLine(center, center, 0, center * 2, paint);
        canvas.drawLine(center, center, center * 2, center * 2, paint);

    }

    /**
     * 清空画布
     *
     * @param canvas
     */
    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(backgroundColor);
    }

    // View.OnTouchListener onTouchListener = new View.OnTouchListener() {

    //    @Override
    //    public boolean onTouch(View view, MotionEvent event) {
    //     Dir tmp = Dir.UNDEFINE;
    //  if ((tmp = checkDir(event.getX(), event.getY())) != Dir.UNDEFINE) {
    //      dir = tmp;
    //      invalidate();
    //  }
    //   return true;
    //}

    // /**
    //   * 检测方向
    //  *
    //    * @param x
    //    * @param y
    //    * @return
    //   */
    //  private Dir checkDir(float x, float y) {
    //     Dir dir = Dir.UNDEFINE;
    //    if (Math.sqrt(Math.pow(y - center, 2) + Math.pow(x - center, 2)) < innerCircleRadius) {// 判断在中心圆圈内
    //         dir = Dir.CENTER;
    //        System.out.println("----中央");
    //   } else if (y < x && y + x < 2 * center) {
    //       dir = Dir.UP;
    //       System.out.println("----向上");
    //  } else if (y < x && y + x > 2 * center) {
    //      dir = Dir.RIGHT;
    //      System.out.println("----向右");
    //  } else if (y > x && y + x < 2 * center) {
    //      dir = Dir.LEFT;
    //     System.out.println("----向左");
    // } else if (y > x && y + x > 2 * center) {
    //     dir = Dir.DOWN;
    //      System.out.println("----向下");
    //  }

    //     return dir;
    //  }

    //  };

    /**
     * 关于方向的枚举
     *
     * @author Administrator
     *
     */
    public enum Dir {
        UP, DOWN, LEFT, RIGHT, CENTER, UNDEFINE
    }
}
