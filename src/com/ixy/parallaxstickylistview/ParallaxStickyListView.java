package com.ixy.parallaxstickylistview;

import com.ixy.parallaxstickylistview.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;


public class ParallaxStickyListView extends ListView implements OnScrollListener {
	public final static double NO_ZOOM = 1;
	public final static double ZOOM_X2 = 2;
	private static final String TAG = "ParallaxScollListView";

	private ImageView mFakeImageView;
	private ImageView mHeaderImageView;
	private View mHeaderView;
	private int mDrawableMaxHeight = -1;
	private int mImageViewHeight = -1;
	private int mDefaultImageViewHeight = 0;

	private interface OnOverScrollByListener {
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
				int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
				boolean isTouchEvent);
	}

	private interface OnTouchEventListener {
		public void onTouchEvent(MotionEvent ev);
	}

	public ParallaxStickyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ParallaxStickyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ParallaxStickyListView(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		mDefaultImageViewHeight = context.getResources().getDimensionPixelSize(
				R.dimen.profile_header_height);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
			int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
			boolean isTouchEvent) {
		boolean isCollapseAnimation = false;

		isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent)
				|| isCollapseAnimation;

		return isCollapseAnimation ? true : super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (mFakeImageView == null) {
			return;
		}
		View firstView = (View) mFakeImageView.getParent();
		// firstView.getTop < getPaddingTop means mImageView will be covered by
		// top padding,
		// so we can layout it to make it shorter
		if (firstView.getTop() < getPaddingTop() && mFakeImageView.getHeight() > mImageViewHeight) {
			mFakeImageView.getLayoutParams().height = Math.max(mFakeImageView.getHeight()
					- (getPaddingTop() - firstView.getTop()), mImageViewHeight);
			// to set the firstView.mTop to 0,
			// maybe use View.setTop() is more easy, but it just support from
			// Android 3.0 (API 11)
			firstView.layout(firstView.getLeft(), 0, firstView.getRight(), firstView.getHeight());
			mFakeImageView.requestLayout();

			
			mHeaderImageView.getLayoutParams().height=mFakeImageView.getLayoutParams().height;
			mHeaderImageView.requestLayout();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		touchListener.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}


	
	public void setParallaxView(ImageView fakeIv,ImageView headerIv,View header){
		mFakeImageView=fakeIv;
		mHeaderImageView=headerIv;
		mHeaderView=header;
	}
	public void setViewsBounds(double zoomRatio) {
		if (mImageViewHeight == -1) {
			mImageViewHeight = mFakeImageView.getHeight();
			if (mImageViewHeight <= 0) {
				mImageViewHeight = mDefaultImageViewHeight;
			}
			double ratio = ((double) mFakeImageView.getDrawable().getIntrinsicWidth())
					/ ((double) mFakeImageView.getWidth());

			if (mFakeImageView.getWidth()==0) {
				ratio=1;
			}
			mDrawableMaxHeight = (int) ((mFakeImageView.getDrawable().getIntrinsicHeight() / ratio) * (zoomRatio > 1 ? zoomRatio
					: 1));

		}
	}

	private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
		@Override
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
				int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY,
				boolean isTouchEvent) {
			if (mFakeImageView.getHeight() <= mDrawableMaxHeight && isTouchEvent) {
				if (deltaY < 0) {
					Log.e(TAG,
							"mImageView.getHeight() - deltaY / 2 ====>"
									+ (mFakeImageView.getHeight() - deltaY / 2));
					Log.e(TAG, "mImageViewHeight=========>" + mImageViewHeight);
					if (mFakeImageView.getHeight() - deltaY / 2 >= mImageViewHeight) {
						mFakeImageView.getLayoutParams().height = mFakeImageView.getHeight() - deltaY/2 < mDrawableMaxHeight ? mFakeImageView
								.getHeight() - deltaY/2
								: mDrawableMaxHeight;
						mFakeImageView.requestLayout();
						ParallaxStickyListView.this.requestLayout();

						mHeaderView.getLayoutParams().height = mFakeImageView.getLayoutParams().height;
						Log.e(TAG, "mExImageView.getLayoutParams().height========>"
								+ mHeaderImageView.getLayoutParams().height);
						mHeaderImageView.getLayoutParams().height=mFakeImageView.getLayoutParams().height;
						mHeaderImageView.requestLayout();
					}
				} else {
					if (mFakeImageView.getHeight() > mImageViewHeight) {
						mFakeImageView.getLayoutParams().height = mFakeImageView.getHeight() - deltaY/2 > mImageViewHeight ? mFakeImageView
								.getHeight() - deltaY/2
								: mImageViewHeight;
						mFakeImageView.requestLayout();
						mHeaderImageView.getLayoutParams().height = mFakeImageView.getLayoutParams().height;
						mHeaderImageView.requestLayout();
						return true;
					}
				}
			}
			return false;
		}
	};

	private OnTouchEventListener touchListener = new OnTouchEventListener() {
		@Override
		public void onTouchEvent(MotionEvent ev) {
			Log.e(TAG, "onTouchEventListener!!!!!!!!!!!!!!!!!!!!!!");
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				Log.e(TAG, "mImageViewHeight==========>" + mImageViewHeight);
				Log.e(TAG, "mImageView.getHeight()========>" + mFakeImageView.getHeight());
				if (mImageViewHeight - 1 < mFakeImageView.getHeight()) {
					ResetAnimimation animation = new ResetAnimimation(mFakeImageView, mImageViewHeight);
					animation.setDuration(300);
					mFakeImageView.startAnimation(animation);

					ResetAnimimation animation2 = new ResetAnimimation(mHeaderImageView, mImageViewHeight);
					animation2.setDuration(300);
					mHeaderImageView.startAnimation(animation2);
				}
			}
		}
	};

	public class ResetAnimimation extends Animation {
		int targetHeight;
		int originalHeight;
		int extraHeight;
		View mView;

		protected ResetAnimimation(View view, int targetHeight) {
			this.mView = view;
			this.targetHeight = targetHeight;
			originalHeight = view.getHeight();
			extraHeight = this.targetHeight - originalHeight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {

			int newHeight;
			newHeight = (int) (targetHeight - extraHeight * (1 - interpolatedTime));
			mView.getLayoutParams().height = newHeight;
			mView.requestLayout();
			ParallaxStickyListView.this.requestLayout();
		}
	}
}
