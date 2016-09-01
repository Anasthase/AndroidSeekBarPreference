# SeekBarPreference for Android

Android preference which allow users to change a ranged integer setting through a SeekBar, and store the value as `int`.
It supports minimum and maximum values (positive or null), and steps.

![Screenshot](https://raw.githubusercontent.com/Anasthase/AndroidSeekBarPreference/master/screenshot.png)

## Usage

### Initial setup

1. Be sure `jcenter()` is specified as a repository in your project's `build.gradle`:

````
repositories {
    jcenter()
}
````
2. Add `compile''` to the module's `build.gradle` under the `dependencies` section:

````
dependencies {
    {...}
    compile ''
    {...}
}
````

### In your preferences layout

Add `xmlns:app="http://schemas.android.com/apk/res-auto"` along with the `xmlns:android`, then add the following:

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
* `minValue`: The minimum value for the setting.
* `maxValue`: The maximum value for the setting.
* `stepValue`: The step value for for the setting.
* `format`: The formatting string of the current value. If specified, must be a valid format string, as expected by `String.format()`, otherwise only the value will be displayed. If `null`, the current value will not be displayed.

Be aware of the following edge cases:
* If `minValue` is lesser than 0, it will be set to 0
* If `maxValue` is lesser than or equal to `minValue`, it will be set to `minValue + 1`
* If `defaultValue` is lesser than (respectively greater than) `minValue` (respectively `maxValue`), it will be set to `minValue` (respectively `maxValue`)
* If the current stored preference value if lesser than (respectively greater than) `minValue` (respectively `maxValue`), it will be displayed as `minValue` (respectively `maxValue`)

### In code

`SeekBarPreference` store the setting value as `int`. Therefore, `SharedPreferences`'s `getInt()` method must be used to retrieve setting value, and `SharedPreferences.Editor`'s `putInt()` method must be used to set setting value.

## Advanced usage

As for any Android `Preference`, title and summary can be accessed through their accessors. All other parameters (minimum value, maximum value, current value, step value, format string) also have dedicated accessors.

An `SeekBar.OnSeekBarChangeListener` listener can also be registered through the `setOnSeekBarChangeListener()` method to monitor `SeekBar` events.