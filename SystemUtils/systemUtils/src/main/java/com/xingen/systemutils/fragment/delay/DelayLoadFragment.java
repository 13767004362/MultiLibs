package com.xingen.systemutils.fragment.delay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author HeXinGen
 * date 2018/12/27.
 * <p>
 * 一个懒加载的fragemnt基类,结合ViewPager来使用.
 * <p>
 * 延迟加载的原理：
 * <p>
 * 1. 避免重复创建视图，节省内存
 * 2. 只当Fragment可见的时候才去初始化这个Fragment或者刷新这个Fragment的数据
 */
public abstract class DelayLoadFragment extends Fragment {

    protected View rootView;
    /**
     * 是否显示
     */
    private boolean isVisible;
    /**
     * 是否创建view
     */
    private boolean isCreateView;
    /**
     * 是否懒加载过
     */
    private boolean isDelayLoad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            isCreateView = true;
            rootView = inflater.inflate(getLayout(), container, false);
            init(savedInstanceState);
            onVisible();
        }
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            onVisible();
        }else {
            onInvisible();
        }
    }

    private void onVisible() {
        isVisible=true;
        if (isDelayLoad){//已经懒加载过，刷新数据
            refreshData();
        }
        //当满足，用户可见，视图已经被创建，才处罚懒加载
        if (!isDelayLoad&&isCreateView&&getUserVisibleHint()){
            isDelayLoad=true;
            delayLoadData();
        }
    }

    /**
     * 不显示状态
     */
    private void onInvisible() {
        isVisible = false;
    }


    protected final <T extends View> T findViewById(int id) {
        return rootView.findViewById(id);
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    public abstract void init(Bundle savedInstanceState);

    /**
     * 获取布局id
     *
     * @return
     */
    public abstract int getLayout();

    /**
     * 已经懒加载过，viewpager滑动当前页面，刷新动作
     */
    public abstract  void refreshData();

    /**
     * 第一次可见，做懒加载刷新数据
     */
    public abstract  void delayLoadData();

    /**
     * ViewPager使用的Adapter
     *
     * @param <T>
     */
    public  static class DelayLoadFragmentAdapter<T extends DelayLoadFragment> extends FragmentPagerAdapter {
        private List<T> fragmentList;

        public DelayLoadFragmentAdapter(FragmentManager fm, List<T> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

        /*
         * 重写该方法，取消调用父类该方法
         * 可以避免在viewpager切换，fragment不可见时执行到onDestroyView，可见时又从onCreateView重新加载视图
         * 因为父类的destroyItem方法中会调用detach方法，将fragment与view分离，（detach()->onPause()->onStop()->onDestroyView()）
         * 然后在instantiateItem方法中又调用attach方法，此方法里判断如果fragment与view分离了，
         * 那就重新执行onCreateView，再次将view与fragment绑定（attach()->onCreateView()->onActivityCreated()->onStart()->onResume()）
         * */
        @Override
        public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
            //   super.destroyItem(container, position, object);
        }
    }
}
