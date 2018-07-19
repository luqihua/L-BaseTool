package com.lu.tool.fragment;

/**
 * @author lqh
 * @time 2018/3/13 上午10:15
 * @description
 */

public abstract class BaseFragment extends RxLifeFragment {
//    private static final String STATE_SAVE_IS_HIDDEN = "is_hidden";
//    private View mViewContainer;
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            boolean isHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            if (isHidden) {
//                ft.hide(this);
//            } else {
//                ft.show(this);
//            }
//            ft.commit();
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (mViewContainer == null) {
//            mViewContainer = inflater.inflate(getLayoutId(), container, false);
//        }
//        return mViewContainer;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
//    }
//
//    protected abstract int getLayoutId();
//
//    private void initView() {
//
//    }
}
