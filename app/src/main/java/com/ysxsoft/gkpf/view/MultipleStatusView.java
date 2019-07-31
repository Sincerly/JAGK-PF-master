package com.ysxsoft.gkpf.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ysxsoft.gkpf.R;


/**
 * 类描述：  一个方便在多种状态切换的view
 * <p>
 * 创建人:   续写经典
 * 创建时间: 2016/1/15 10:20.
 */
public class MultipleStatusView extends RelativeLayout {
    public static final int STATUS_CONTENT = 0x00;
    public static final int STATUS_LOADING = 0x01;

    private View mLoadingView;
    private int mLoadingViewResId;
    private int mViewStatus;

    private LayoutInflater mInflater;
    private final ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public MultipleStatusView(Context context) {
        this(context, null);
    }

    public MultipleStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.MultipleStatusView, defStyleAttr, 0);

        mLoadingViewResId =
                a.getResourceId(R.styleable.MultipleStatusView_loadingView, R.layout.loading_view);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mInflater = LayoutInflater.from(getContext());
    }

    /**
     * 显示加载中视图
     */
    public final void showLoading() {
        mViewStatus = STATUS_LOADING;
        if (null == mLoadingView) {
            mLoadingView = mInflater.inflate(mLoadingViewResId, null);
            addView(mLoadingView, 1, mLayoutParams);
        }
        showViewByStatus(mViewStatus);
    }

    /**
     * 隐藏加载中视图
     */
    public final void hideLoading() {
        if (null != mLoadingView && mViewStatus == STATUS_LOADING) {
            mViewStatus = STATUS_CONTENT;
            try {
                mLoadingView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void showViewByStatus(int viewStatus) {
        if (null != mLoadingView) {
            mLoadingView.setVisibility(viewStatus == STATUS_LOADING ? View.VISIBLE : View.GONE);
        }
    }

}
