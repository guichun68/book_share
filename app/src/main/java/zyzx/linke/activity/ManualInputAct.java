package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import zyzx.linke.R;

/**
 * Created by austin on 2017/3/10.
 * Desc: 手动录入书籍Act
 */

public class ManualInputAct extends BaseActivity {

    private TextView tvSave;
    private AppCompatEditText acetBookName,acetISBN,acetAuthor,acetPublisher,acetIntro;
    private AppCompatImageView acivCover;//

    @Override
    protected int getLayoutId() {
        return R.layout.act_manual_input;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        tvSave = (TextView) findViewById(R.id.tv_add_mylib);
        acetBookName = (AppCompatEditText) findViewById(R.id.acet_book_name);
        acetISBN = (AppCompatEditText) findViewById(R.id.acet_isbn);
        acetAuthor = (AppCompatEditText) findViewById(R.id.acet_author);
        acetPublisher = (AppCompatEditText) findViewById(R.id.acet_publisher);
        acetIntro = (AppCompatEditText) findViewById(R.id.acet_intro);
        acivCover = (AppCompatImageView) findViewById(R.id.aciv_cover);

        mTitleText.setText("书籍录入");
        tvSave.setText("保存");
        tvSave.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.tv_add_mylib:
                //保存按钮
                checkInput();
                break;
        }
    }

    private void checkInput() {

    }
}
