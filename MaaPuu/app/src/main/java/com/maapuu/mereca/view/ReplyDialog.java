package com.maapuu.mereca.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maapuu.mereca.R;
import com.maapuu.mereca.util.EmojiUtil;


/**
 * 回复对话框
 *
 * @author Jaiky
 * @date 2015-3-30 PS: Not easy to write code, please indicate.
 */
public class ReplyDialog extends Dialog {

    private EditText etContent;
    private LinearLayout llBtnReply;
    private TextView txtSend;

    private Context mContext;

    public ReplyDialog(Context context) {
        super(context, R.style.MyNoFrame_Dialog);
        mContext = context;
        init();
    }

    private ReplyDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_replyform);

        // 设置宽度
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        LayoutParams lp = window.getAttributes();
        lp.width = LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        etContent = (EditText) findViewById(R.id.dialog_reply_etContent);
        llBtnReply = (LinearLayout) findViewById(R.id.dialog_reply_llBtnReply);
        txtSend = (TextView) findViewById(R.id.txt_send);
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(final Editable editable) {
                if(editable.toString().length() == 0){
                    txtSend.setSelected(false);
                }else {
                    txtSend.setSelected(true);
                }
            }
        });
//        etContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_SEND){
//                }
//                return false;
//            }
//        });

        // 弹出键盘
        etContent.setFocusable(true);
        etContent.setFocusableInTouchMode(true);
        etContent.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) mContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etContent, 0);
            }
        }, 50);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ReplyDialog setContent(String content) {
        etContent.setText(content);
        return this;
    }

    public ReplyDialog setHintText(String hint) {
        etContent.setHint(hint);
        return this;
    }

    public String getContent() {
        return EmojiUtil.stringToUtf8(etContent.getText().toString()).replace("+","%20");
    }

    public ReplyDialog setOnBtnCommitClickListener(
            android.view.View.OnClickListener onClickListener) {
        llBtnReply.setOnClickListener(onClickListener);
        return this;
    }

    @Override
    public void onBackPressed() {
        if (isShowing()){
            dismiss();
        }
        super.onBackPressed();
    }
}
