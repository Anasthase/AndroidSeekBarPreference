/*
 * SeekBarPreference for Android
 * 
 * Copyright (C) 2016 - to infinity and beyond J. Devauchelle and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.anasthase.androidseekbarpreference;

import java.util.IllegalFormatException;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * <p>A preference which display a SeekBar, and store the value as <code>int</code>.</p>
 * <p>Support minimum values greater than 0, and "step" values.</p>
 * 
 * <p>Minimum values greater than 0 are made by translating values, e.g. setting the max of the SeekBar to (max - min).
 * Steps are done by dividing min and max values by the value of the step. The real value is then equals to <code>(SeekBar value + "stepped" minimum value) * step value</code>.</p>
 *
 * <p>Usage:</p>
 * <p>In your preferences layout, add <code>xmlns:app="http://schemas.android.com/apk/res-auto"</code> along with the <code>xmlns:android</code>, then add the following:</p>
 * <pre>
 * {@code
 * <org.anasthase.androidseekbarpreference.SeekBarPreference
 *          android:defaultValue="15"
 *          android:key="PREFERENCE_KEY"
 *          android:summary="@string/PreferenceSummary"
 *          android:title="@string/PreferenceTitle"
 *          app:format="@string/PreferenceFormat"
 *          app:maxValue="50"
 *          app:minValue="5"
 *          app:stepValue="5" />
 * }
 * </pre>
 * 
 * <p>Where:</p>
 * <ul>
 * <li><code>minValue</code>: The minimum value for the setting.</li>
 * <li><code>maxValue</code>: The maximum value for the setting.</li>
 * <li><code>stepValue</code>: The step value for for the setting.</li>
 * <li><code>format</code>: The formatting string of the current value. If specified, must be a valid format string, as expected by <code>String.format()</code>, otherwise only the value will be displayed. If <code>null</code>, the current value will not be displayed.</li>
 * </ul>
 *
 * <p>Edge cases:</p>
 * <ul>
 *     <li>If <code>minValue</code> is lesser than 0, it will be set to 0</li>
 *     <li>If <code>maxValue</code> is lesser than or equal to <code>minValue</code>, it will be set to <code>minValue + 1</code></li>
 *     <li>If <code>defaultValue</code> is lesser than (respectively greater than) <code>minValue</code> (respectively <code>maxValue</code>), it will be set to <code>minValue</code> (respectively <code>maxValue</code>)</li>
 *     <li>If the current stored preference value if lesser than (respectively greater than) <code>minValue</code> (respectively <code>maxValue</code>), it will be displayed as <code>minValue</code> (respectively <code>maxValue</code>)</li>
 * </ul>
 */
public class SeekBarPreference extends Preference {

	private View mContainer;
	
	private int mDefaultValue;
	private int mMinValue;
	private int mMaxValue;
	private int mStepValue;	

	private String mFormat;

	private int mSteppedMinValue;
	private int mSteppedMaxValue;

	private TextView mTitle;
	private TextView mSummary;
	private TextView mValue;
	private SeekBar mSeekBar;

	private OnSeekBarChangeListener mListener;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContainer = null;
		mListener = null;

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);

			mMinValue = a.getInt(R.styleable.SeekBarPreference_minValue, 0);
			mMaxValue = a.getInt(R.styleable.SeekBarPreference_maxValue, 10);
			mStepValue = a.getInt(R.styleable.SeekBarPreference_stepValue, 1);

			mDefaultValue = a.getInt(R.styleable.SeekBarPreference_android_defaultValue, 0);

            if (mMinValue < 0) {
                mMinValue = 0;
            }

			if (mMaxValue <= mMinValue) {
				mMaxValue = mMinValue + 1;
			}

			if (mDefaultValue < mMinValue) {
				mDefaultValue = mMinValue;
			} else if (mDefaultValue > mMaxValue) {
				mDefaultValue = mMaxValue;
			}

			if (mStepValue <= 0) {
				mStepValue = 1;
			}

			mFormat = a.getString(R.styleable.SeekBarPreference_format);

			a.recycle();
		} else {
			mMinValue = 0;
			mMaxValue = 10;
			mStepValue = 1;
			mDefaultValue = 0;
		}

		mSteppedMinValue = Math.round(mMinValue / mStepValue);
		mSteppedMaxValue = Math.round(mMaxValue / mStepValue);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		if (mContainer == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			mContainer = inflater.inflate(R.layout.seekbar_preference, null);

			mTitle = (TextView) mContainer.findViewById(R.id.SeekBarPreferenceTitle);
			mTitle.setText(getTitle());

			mSummary = (TextView) mContainer.findViewById(R.id.SeekBarPreferenceSummary);
			if (!TextUtils.isEmpty(getSummary())) {
				mSummary.setText(getSummary());
			} else {
				mSummary.setVisibility(View.GONE);
			}

			mValue = (TextView) mContainer.findViewById(R.id.SeekBarPreferenceValue);

			mSeekBar = (SeekBar) mContainer.findViewById(R.id.SeekBarPreferenceSeekBar);
			mSeekBar.setMax(mSteppedMaxValue - mSteppedMinValue);

			setValue(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getKey(), mDefaultValue));

			mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					if (mListener != null) {
						mListener.onStopTrackingTouch(seekBar);
					}

					saveValue();
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					if (mListener != null) {
						mListener.onStartTrackingTouch(seekBar);
					}
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					if (mListener != null) {
						mListener.onProgressChanged(seekBar, getValue(), fromUser);
					}

					updateDisplay(progress);
				}
			});
		}

		return mContainer;
	}

	/**
	 * Set a listener for events from the SeekBar.
	 * @param listener The listener.
	 */
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		mListener = listener;
	}

	/**
	 * Get the minimum value.
	 * @return The minimum value.
	 */
	public int getMinValue() {
		return mMinValue;
	}

	/**
	 * Set the minimum value.
	 * @param value The minimum value.
	 */
	public void setMinValue(int value) {
		mMinValue = value;
		updateAllValues();
	}

	/**
	 * Get the maximum value.
	 * @return The maximum value.
	 */
	public int getMaxValue() {
		return mMaxValue;
	}

	/**
	 * Set the maximum value.
	 * @param value The maximum value.
	 */
	public void setMaxValue(int value) {
		mMaxValue = value;
		updateAllValues();
	}

	/**
	 * Get the step value.
	 * @return The step value.
	 */
	public int getStepValue() {
		return mStepValue;
	}

	/**
	 * Set the step value.
	 * @param value The step value.
	 */
	public void setStepValue(int value) {
		mStepValue = value;
		updateAllValues();
	}

	/**
	 * Get the display format.
	 * @return The display format.
	 */
	public String getFormat() {
		return mFormat;
	}

	/**
	 * Set the display format. Should be a valid format string, as <code>String.format()</code> expect it.
	 * If this is not the case, only the value will be displayed.
     * If <code>null</code>, the current value will not be displayed.
	 * @param format The format, as a String.
	 */
	public void setFormat(String format) {
		mFormat = format;
		updateDisplay();
	}

	/**
	 * Set the display format. Should be a valid format string, as <code>String.format()</code> expect it.
	 * If this is not the case, or if null, only the value will be displayed.
	 * @param formatResId The format, as a string resource identifier.
	 */
	public void setFormat(int formatResId) {
		setFormat(getContext().getResources().getString(formatResId));
	}

	/**
	 * Get the current value.
	 * @return The current value.
	 */
	public int getValue() {
		return (mSeekBar.getProgress() + mSteppedMinValue) * mStepValue;
	}

	/**
	 * Set the current value.
	 * @param value The current value.
	 */
	public void setValue(int value) {
		value = getBoundedValue(value) - mSteppedMinValue;

		mSeekBar.setProgress(value);
		updateDisplay(value);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		mTitle.setText(title);
	}

	@Override
	public void setTitle(int titleResId) {
		super.setTitle(titleResId);
		mTitle.setText(titleResId);
	}

	@Override
	public void setSummary(CharSequence summary) {
		super.setSummary(summary);
		mSummary.setText(summary);
	}

	@Override
	public void setSummary(int summaryResId) {
		super.setSummary(summaryResId);
		mSummary.setText(summaryResId);
	}

	private void updateAllValues() {
		int currentValue = getValue();

		if (mMaxValue <= mMinValue) {
			mMaxValue = mMinValue + 1;
		}

		mSteppedMinValue = Math.round(mMinValue / mStepValue);
		mSteppedMaxValue = Math.round(mMaxValue / mStepValue);

		mSeekBar.setMax(mSteppedMaxValue - mSteppedMinValue);

		currentValue = getBoundedValue(currentValue) - mSteppedMinValue;

		mSeekBar.setProgress(currentValue);
		updateDisplay(currentValue);
	}

	private int getBoundedValue(int value) {

		value = Math.round(value / mStepValue);

		if (value < mSteppedMinValue) {
			value = mSteppedMinValue;
		}

		if (value > mSteppedMaxValue) {
			value = mSteppedMaxValue;
		}

		return value;
	}

	private void updateDisplay() {
		updateDisplay(mSeekBar.getProgress());
	}

	private void updateDisplay(int value) {

        if (!TextUtils.isEmpty(mFormat)) {
            mValue.setVisibility(View.VISIBLE);

            value = (value + mSteppedMinValue) * mStepValue;

            String text;

            try {
                text = String.format(mFormat, value);
            } catch (IllegalFormatException e) {
                text = Integer.toString(value);
            }

            mValue.setText(text);
        } else {
            mValue.setVisibility(View.GONE);
        }
	}

	private void saveValue() {		
		PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putInt(getKey(), getValue())
                .apply();
	}

}
