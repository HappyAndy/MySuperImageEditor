package com.dyzs.conciseimageeditor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.dyzs.conciseimageeditor.R;
import com.dyzs.conciseimageeditor.utils.BitmapUtils;
import com.dyzs.conciseimageeditor.utils.DensityUtils;

/**
 * Created by maidou on 2016/2/19.
 */
public class MovableTextView2 extends EditText{
    private Context mContext;
    public MovableTextView2(Context context) {
        this(context, null);
    }
    public MovableTextView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MovableTextView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }
    private void init() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        );
        this.setLayoutParams(layoutParams);
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.pic_bg_edit_text_9patch_3x3));
//        this.setHint("在此输入标注文字");
//        this.setHintTextColor(Color.WHITE);
        this.setBackgroundResource(R.drawable.shape_dotted);
        this.setText("在此输入标注文字");
        this.setTextColor(Color.WHITE);
        this.setClickable(true);
        this.setFocusable(true);
        this.setTextSize(DensityUtils.px2sp(mContext, getResources().getDimension(R.dimen.movable_text_view_default_text_size)));
        this.setTypefaceName("default");
        this.setTypeface(Typeface.DEFAULT);
        this.setColorR(255);
        this.setColorG(255);
        this.setColorB(255);
        this.setMaxEms(12);
        this.setColorSeekBarProgress(89);   // progress 对应颜色的 seekBar 的值，因为 colorSeekBar 并没有颜色，只是显示的一张颜色背景
    }

    public enum OperateState{
        STATE_MOVING, STATE_SELECTED, STATE_UNSELECTED
    }

    private OperateState operateState = OperateState.STATE_UNSELECTED;
    public void setBackgroundRes() {
        // 判断如果是在选中状态或者是滑动状态，则添加背景
        if(operateState == OperateState.STATE_MOVING || operateState == OperateState.STATE_SELECTED) {
            this.setBackgroundResource(R.drawable.shape_dotted);
        } else if (operateState == OperateState.STATE_UNSELECTED) {
            this.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private int startX;
    private int startY;
    private int checkStartX;
    private int checkStartY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startX = (int) event.getRawX();
            startY = (int) event.getRawY();
            checkStartX = (int) event.getRawX();
            checkStartY = (int) event.getRawY();
            operateState = OperateState.STATE_SELECTED;
            return true;
        case MotionEvent.ACTION_MOVE:
            int movingX = (int) event.getRawX();
            int movingY = (int) event.getRawY();
            // System.out.println("moving:" + movingX + "," + movingY);
            // 开始计算移动时的偏移量
            int offsetX = movingX - startX;
            int offsetY = movingY - startY;
            // 获取控件没有重新绘制时控件距离父控件左边框和顶边框的距离
            int l = this.getLeft();
            int t = this.getTop();
            // 移动相应的偏移量
            l += offsetX;
            t += offsetY;
            int r = l + this.getWidth();
            int b = t + this.getHeight();
            // 允许超过屏幕,要不然会出现卡顿的现象
//            if( l < 0 || r > mParentWidth || t < 0 || b > mParentHeight){
//                break;
//            }
            this.layout(l, t, r, b);
            startX = movingX;
            startY = movingY;
            operateState = OperateState.STATE_MOVING;
            return true;
        case MotionEvent.ACTION_UP:
            getParentParams();
//            if (mOnActionUpListener != null) {
//                mOnActionUpListener.getLtrb(getLeft(), getTop(), getRight(), getBottom());
//            }
            operateState = OperateState.STATE_UNSELECTED;
            int upX = (int) event.getRawX();
            int upY = (int) event.getRawY();
            if (Math.abs(upX - checkStartX) < 15 && Math.abs(upY - checkStartY) < 15) {
                if (mOnCustomClickListener != null) {
                    mOnCustomClickListener.onCustomClick();
                }
                // 不让滑动事件继续消费当前事件
                return false;
            }
            break;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    private void getParentParams() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.gravity = -1;
        lp.leftMargin = getLeft();
        lp.topMargin = getTop();
        lp.rightMargin = BitmapUtils.getScreenPixels(getContext()).widthPixels - getLeft() - this.getMeasuredWidth();
        lp.bottomMargin = 0;
        setLayoutParams(lp);
    }
    /**
     * 用来代替点击事件
     */
    private OnCustomClickListener mOnCustomClickListener;
    public interface OnCustomClickListener {
        void onCustomClick();
    }
    public void setOnCustomClickListener(OnCustomClickListener listener) {
        if (listener != null) {
            this.mOnCustomClickListener = listener;
        }
    }


    private OnActionUpListener mOnActionUpListener;
    public interface OnActionUpListener {
        void getLtrb(int left, int top, int right, int bottom);
    }
    public void setOnActionUpListener(OnActionUpListener listener) {
        if (listener != null) {
            this.mOnActionUpListener = listener;
        }
    }


    // 标记当前控件是否有文本内容
    private boolean hasContent;
    public boolean isHasContent() {
        return hasContent;
    }
    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    private String typefaceName;
    public String getTypefaceName() {
        return typefaceName;
    }
    public void setTypefaceName(String typeface) {
        this.typefaceName = typeface;
    }

    private int ColorR;
    private int ColorG;
    private int ColorB;

    public int getColorR() {
        return ColorR;
    }

    public void setColorR(int colorR) {
        ColorR = colorR;
    }

    public int getColorG() {
        return ColorG;
    }

    public void setColorG(int colorG) {
        ColorG = colorG;
    }

    public int getColorB() {
        return ColorB;
    }

    public void setColorB(int colorB) {
        ColorB = colorB;
    }

    // 保存当前的 mtv 对象用于只是在第一次点击的时候才自动弹出虚拟键盘
    private boolean firstClick;   // 当前 mtv 是否已经被第一次点击了，用来判断当前view的第一次点击是否被消费了
    public boolean getFirstClick() { return firstClick; }
    public void setFirstClick(boolean firstClick) { this.firstClick = firstClick; }


    private int colorSeekBarProgress;
    public int getColorSeekBarProgress() {
        return colorSeekBarProgress;
    }
    public void setColorSeekBarProgress(int progress) {
        colorSeekBarProgress = progress;
    }

//    public void tv_btnnnnnnnnnnn(View v){
//        /**
//         * src the source array to copy the content.      拷贝的原数组 srcPos the
//         * starting index of the content in src.          从原数组的那个位置开始拷贝 dst the
//         * destination array to copy the data into.       拷贝目标数组 dstPos the
//         * starting index for the copied content in dst.  从目标数组的那个位置去写
//         * length the number of elements to be copied.    拷贝的长度
//         */
//        //表示从原数组的 1 拷贝给 0 的位置，拷贝一个，所以长度为 length -1
//        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
//        //获取离开机的时间，毫秒值，不包含手机休眠时间
//        mHits[mHits.length-1] = SystemClock.uptimeMillis();
//        //判断数组的第一个元素去是否再次获取离开机的时间再减去300毫秒大，如果大于执行点击操作，小于则不执行
//        if(mHits[0] >= (SystemClock.uptimeMillis() - 300)){
//            System.out.println("我被点击了");
//            if(longClickCount == 0){
//                tv.setVisibility(View.INVISIBLE);
//                longClickCount ++ ;
//            }else{
//                tv.setVisibility(View.VISIBLE);
//                longClickCount -- ;
//            }
//        }
//    }






//    private int mParentWidth;
//    private int mParentHeight;
//    private int pLeft, pRight, pTop, pBottom;
//    private ViewGroup parent;
//    private void getParentParams() {
//        ViewParent parent = this.getParent();
//        this.parent = (ViewGroup) parent;
//        mParentWidth = this.parent.getWidth();
//        mParentHeight = this.parent.getHeight();
//        pLeft = this.parent.getLeft();
//        pRight = this.parent.getRight();
//        pTop = this.parent.getTop();
//        pBottom = this.parent.getBottom();
//        // System.out.println("坐上右下:"+pLeft+"/"+pTop+"/"+pRight+"/"+pBottom);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }


}
