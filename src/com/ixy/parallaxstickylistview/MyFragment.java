package com.ixy.parallaxstickylistview;

import java.util.ArrayList;
import java.util.List;

import com.ixy.parallaxstickylistview.R;
import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyFragment extends Fragment {

	private static final String TAG = "MyFragment";
	private ParallaxStickyListView mListView;
	private LinearLayout mLlSticky;
	private View mHeader;
	private View mFakeHeader;
	private View mFakeHeaderSticky;
	private ImageView mFakeHeaderImg;

	public static MyFragment newInstance() {
		MyFragment fragment = new MyFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my, container, false);
		mListView = (ParallaxStickyListView) view.findViewById(R.id.list);
		mHeader = view.findViewById(R.id.rl_header);
		mLlSticky = (LinearLayout) view.findViewById(R.id.ll_sticky);

		ImageView ivHeader = (ImageView) view.findViewById(R.id.iv_header);
		mFakeHeader = inflater.inflate(R.layout.fake_header, null);
		mFakeHeaderSticky = mFakeHeader.findViewById(R.id.fake_header_sticky_holder);
		mFakeHeaderImg = (ImageView) mFakeHeader.findViewById(R.id.fake_header_img);

		mListView.setParallaxView(mFakeHeaderImg, ivHeader, mHeader);
		mListView.addHeaderView(mFakeHeader);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						onScrollChanged();

						ViewTreeObserver obs = mListView.getViewTreeObserver();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							obs.removeOnGlobalLayoutListener(this);
						} else {
							obs.removeGlobalOnLayoutListener(this);
						}
					}
				});

		mListView.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {

					@Override
					public boolean onPreDraw() {
						mListView.setViewsBounds(ParallaxStickyListView.ZOOM_X2);
						ViewTreeObserver obs = mListView.getViewTreeObserver();
						obs.removeOnPreDrawListener(this);
						return true;
					}
				});

		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				onScrollChanged();
			}
		});

		fillData();
	}

	private void fillData() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 20; i++) {
			list.add("Item  " + (i + 1));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, list);
		mListView.setAdapter(adapter);
	}

	private void onScrollChanged() {
		View v = mListView.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		Log.e("first item top====>", top + "");

		Log.e("placeholder top====>", mFakeHeaderSticky.getTop() + "");
		// This check is needed because when the first element reaches the top
		// of the window, the top values from top are not longer valid.
		Log.e("getFirstVisiblePosition===========>", mListView.getFirstVisiblePosition() + "");

		if (mListView.getFirstVisiblePosition() == 0) {
			Log.e("translationY", "!!!!!!!!!!!!!!!!!!!!!!!!");
			Log.e(TAG, "top======>" + top);
			Log.e(TAG, "mPlaceHolderView.getTop() + top=========>" + mFakeHeaderSticky.getTop()
					+ top);

			ViewHelper.setTranslationY(mLlSticky, Math.max(0, mFakeHeaderSticky.getTop() + top));
			ViewHelper.setTranslationY(mHeader, top);

		} else {
			ViewHelper.setTranslationY(mLlSticky, 0);
			ViewHelper.setTranslationY(mHeader, -mHeader.getHeight());
		}

	}

}
