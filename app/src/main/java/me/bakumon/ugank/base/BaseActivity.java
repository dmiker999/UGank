package me.bakumon.ugank.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import me.bakumon.ugank.R;
import me.bakumon.ugank.utills.StatusBarUtil;

/**
 * 1.沉浸式状态栏
 * 2.ViewDataBinding 封装
 * 3.滑动返回
 *
 * @author Bakumon
 * @date 18-1-17
 */
public abstract class BaseActivity extends AppCompatActivity implements BGASwipeBackHelper.Delegate {
    protected BGASwipeBackHelper mSwipeBackHelper;
    private ViewDataBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getInstance().onInit(this) 来初始化滑动返回」
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        onInit(savedInstanceState);
    }

    /**
     * 子类必须实现，用于创建 view
     *
     * @return 布局文件 Id
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 获取 ViewDataBinding
     *
     * @param <T> BaseActivity#getLayoutId() 布局创建的 ViewDataBinding
     *            如 R.layout.activity_demo 会创建出 ActivityDemoBinding.java
     * @return T
     */
    protected <T extends ViewDataBinding> T getDataBinding() {
        return (T) dataBinding;
    }

    /**
     * 开始的方法
     *
     * @param savedInstanceState 保存的 Bundle
     */
    protected abstract void onInit(@Nullable Bundle savedInstanceState);

    /**
     * 设置沉浸式状态栏
     */
    private void setImmersiveStatus() {
        View[] views = setImmersiveView();
        if (views == null || views.length < 1) {
            return;
        }
        StatusBarUtil.immersive(this);
        for (View view : views) {
            StatusBarUtil.setPaddingSmart(this, view);
        }
    }

    /**
     * 子类重写该方法设置沉浸式状态栏
     *
     * @return null 或 view[]大小为0,则不启用沉浸式
     */
    protected abstract View[] setImmersiveView();

    /**
     * 是否已经设置了沉浸式状态栏
     */
    private boolean isSetupImmersive;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isSetupImmersive) {
            setImmersiveStatus();
            isSetupImmersive = true;
        }
    }

    /**
     * https://github.com/bingoogolapple/BGASwipeBackLayout-Android
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);

        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackManager.getInstance().onInit(this) 来初始化滑动返回」
        // 下面几项可以不配置，这里只是为了讲述接口用法。

        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        // 设置是否是微信滑动返回样式。默认值为 true
        // 选用微信滑动返回样式会有两个问题：
        // 1.正在滑动时按下返回键 view 显示异常
        // 2. 界面有明显的抖动
        mSwipeBackHelper.setIsWeChatStyle(false);
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true);
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return true：支持，否则不支持
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }

}