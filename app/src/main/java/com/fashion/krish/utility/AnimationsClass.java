package com.fashion.krish.utility;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.fashion.krish.R;


public class AnimationsClass {

	Activity activity;
	View viewToanimate;
	
	Animation animation;
	
	public AnimationsClass(Activity activity, View viewToanimate) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.viewToanimate = viewToanimate;
		
	}
	
	public Animation slideOutToLeft(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_from_left);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}
	
	public Animation slideInFromLeft(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_left);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}
	
	public Animation slideOutToRight(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_from_right);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}
	
	public Animation slideInFromRight(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_right);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}
	
	public Animation slideInFromBottom(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_bottom);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}

	public Animation slideInFromTop(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_from_top);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}
	
	public Animation slideOutFromTop(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_from_top);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}

	public Animation slideOutFromBottom(int visibilityAfterAnim)
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_from_bottom);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		return animation;
	}
	
	public Animation fadeOutView(int visibilityAfterAnim)
	{
		animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(300);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(visibilityAfterAnim);
		
		return animation;
	}
	
	public Animation fadeInView()
	{
		animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(400);
		viewToanimate.startAnimation(animation);
		viewToanimate.setVisibility(View.VISIBLE);
		
		return animation;
	}

	public Animation zoomInView()
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_in);
		viewToanimate.startAnimation(animation);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				zoomOutView();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		return animation;
	}

	public Animation zoomOutView()
	{
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.zoom_out);
		viewToanimate.startAnimation(animation);
		return animation;
	}
	
}
