# [The 2.5.1 release is out!](https://github.com/excilys/androidannotations/wiki/ReleaseNotes#wiki-2.5.1)


"_The ratio of time spent reading [code] versus writing is well over 10 to 1 [therefore] making it easy to read makes it easier to write._" - **Robert C. Martin**

#Is your Android code easy to write, read, and maintain?

```java
@EActivity(R.layout.translate) // Sets content view to R.layout.translate
public class TranslateActivity extends Activity {

    @ViewById // Injects R.id.textInput
    EditText textInput;

    @ViewById(R.id.myTextView) // Injects R.id.myTextView
    TextView result;

    @AnimationRes // Injects android.R.anim.fade_in
    Animation fadeIn;

    @Click // When R.id.doTranslate button is clicked 
    void doTranslate() {
         translateInBackground(textInput.getText().toString());
    }

    @Background // Executed in a background thread
    void translateInBackground(String textToTranslate) {
         String translatedText = callGoogleTranslate(textToTranslate);
         showResult(translatedText);
    }
   
    @UiThread // Executed in the ui thread
    void showResult(String translatedText) {
         result.setText(translatedText);
         result.startAnimation(fadeIn);
    }

    // [...]
}
```

AndroidAnnotations provide those good things for **less than 50kb**, without any [perf impact](https://github.com/excilys/androidannotations/wiki/FAQ#wiki-perf-impact)!

[![Android Annotations Logo](https://github.com/excilys/androidannotations/wiki/img/aa-logo.png)](https://github.com/excilys/androidannotations/wiki/GettingStarted) | [**Get Started**](https://github.com/excilys/androidannotations/wiki/GettingStarted), then [**read the cookbook**](https://github.com/excilys/androidannotations/wiki/Cookbook) |
-----------|---------------|

## Sponsors

[![Built on DEV@cloud](http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png)](https://androidannotations.ci.cloudbees.com) | [![Supported by eBusiness Information (Excilys Group)](https://github.com/excilys/androidannotations/wiki/img/supportedbylogo.png)](http://www.ebusinessinformation.fr) |
-----------|---------------|

***
The project logo is based on the [Android logo](http://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg), created and [shared by Google](http://code.google.com/policies.html) and used according to terms described in the [Creative Commons 3.0 Attribution License](http://creativecommons.org/licenses/by/3.0/).

Android is a trademark of Google Inc. Use of this trademark is subject to [Google Permissions](http://www.google.com/permissions/index.html). 
