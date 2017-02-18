package zyzx.linke.views;

import java.util.Timer;
import java.util.TimerTask;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import zyzx.linke.R;

//可以进行透明度渐变的ImageView
public class AlphaImageView extends ImageView {
    //图像透明度每次改变的大小
    private int perAlpha = 0;
    //当前图像的透明度
    private int curAlpha = 0;
    //每隔多长时间改变一次透明度
    private int SPEED = 300;


    public AlphaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        //找到自定义的属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.AlphaImageView);
        //获取duration参数
        int duration = typedArray.getInt(R.styleable.AlphaImageView_duration, 0);
        //计算透明度改变的大小
        perAlpha = 255 * SPEED / duration;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                curAlpha += perAlpha;
                if (curAlpha > 255) {
                    curAlpha = 255;

                }
                AlphaImageView.this.setAlpha(curAlpha);
            }
        }

        ;
    };

    @Override
    protected void onDraw(Canvas canvas) {


        this.setAlpha(curAlpha);
        super.onDraw(canvas);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
// TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = 0x123;
                if (curAlpha > 255) {
                    timer.cancel();
                } else {
                    handler.sendMessage(msg);
                }
            }
        }, 0, SPEED);

    }


}