package com.android.tedcoder.wkvideoplayer.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.tedcoder.wkvideoplayer.R;
import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
import com.android.tedcoder.wkvideoplayer.view.MediaController;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;

public class SuperVideoPlayerFragment extends Fragment implements View.OnClickListener {

    private View mContentView;
    private SuperVideoPlayer mSuperVideoPlayer;
    private View mPlayBtnView;
    private ViewGroup mContainer;
    private String mVideoUrl;
    final static String URL_KEY = "videoUrlKey";

    public static SuperVideoPlayerFragment newInstance(String url) {
        SuperVideoPlayerFragment fragment = new SuperVideoPlayerFragment();

        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);
        if(mContentView == null){
            mContainer = container;
            mContentView = inflater.inflate(R.layout.fragment_super_video_player, container, false);
        }

        mSuperVideoPlayer = (SuperVideoPlayer) mContentView.findViewById(R.id.video_player_item_1);
        mPlayBtnView = mContentView.findViewById(R.id.play_btn);
        mPlayBtnView.setOnClickListener(this);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);

        Bundle args = getArguments();
        if(args != null){
            mVideoUrl = args.getString(URL_KEY);
        }

        return mContentView;
    }

    @Override
    public void onClick(View v) {
        mPlayBtnView.setVisibility(View.GONE);
        mSuperVideoPlayer.setVisibility(View.VISIBLE);
        mSuperVideoPlayer.setAutoHideController(false);
        mSuperVideoPlayer.loadLocalVideo(mVideoUrl);
    }


    private float mVideoHeight;
    private float mVideoWidth;
    /**
     * 旋转屏幕之后回调
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == mSuperVideoPlayer) return;
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //全屏前纪录下视频的高宽
            mVideoHeight = mSuperVideoPlayer.getMeasuredHeight();
            mVideoWidth = mSuperVideoPlayer.getMeasuredWidth();

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(getActivity());
            float width = DensityUtil.getHeightInPx(getActivity());
            mSuperVideoPlayer.getLayoutParams().height = (int) width;
            mSuperVideoPlayer.getLayoutParams().width = (int) height;
            mContainer.getLayoutParams().height = (int) width;
            mContainer.getLayoutParams().width = (int) height;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setAttributes(attrs);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mSuperVideoPlayer.getLayoutParams().height = (int) mVideoHeight;
            mSuperVideoPlayer.getLayoutParams().width = (int) mVideoWidth;
            mContainer.getLayoutParams().height = (int) mVideoHeight;
            mContainer.getLayoutParams().width = (int) mVideoWidth;
        }
    }

    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onCloseVideo() {
            mSuperVideoPlayer.close();
            mPlayBtnView.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.setVisibility(View.GONE);
            resetPageToPortrait();
        }

        @Override
        public void onSwitchPageType() {
            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
            } else {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
            }
        }

        @Override
        public void onPlayFinish() {

        }
    };

    /***
     * 恢复屏幕至竖屏
     */
    private void resetPageToPortrait() {
        if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
        }
    }
}
