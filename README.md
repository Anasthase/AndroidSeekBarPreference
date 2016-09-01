# SeekBarPreference for Android

A preference which display a SeekBar, and store the value as `int`.
Support minimum values greater than 0, and "step" values.

Minimum values greater than 0 are made by translating values, e.g. setting the max of the SeekBar to `(max - min)`.
Steps are done by dividing min and max values by the value of the step. The real value is then equals to `(SeekBar value + "stepped" minimum value) * step value`.

## Usage

In your preferences layout, add `xmlns:app="http://schemas.android.com/apk/res-auto"` along with the `xmlns:android`, then add the following:

```xml
<org.anasthase.androidseekbarpreference.SeekBarPreference
    android:defaultValue="15"
    android:key="PREFERENCE_KEY"
    android:summary="@string/PreferenceSummary"
    android:title="@string/PreferenceTitle"
    app:format="@string/PreferenceFormat"
    app:maxValue="50"
    app:minValue="5"
    app:stepValue="5" />
```

Where:
* `minValue`: The minimum value of your preference.
* `maxValue`: The maximum value of your preference.
* `stepValue`: The value of the steps allowed for your preference.
* `format`: The formatting string of the current value. If specified, must be a valid format string, as expected by `String.format()`. If not, only the value will be displayed. If `null`, the current value will not be displayed.

Edge cases:
* If `defaultValue` is lesser than `minValue` or greater than `maxValue`, it will be equal to `minValue`
* If the current stored preference value if lesser than (respectively greater than) `minValue` (respectively `maxValue`), it will be displayed as `minValue` (respectively `maxValue`)