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
##[Get Started](https://github.com/excilys/androidannotations/wiki/GettingStarted), then [read the cookbook](https://github.com/excilys/androidannotations/wiki/Cookbook)

AndroidAnnotations provide those good things for **less than 50kb**, without any [perf impact](https://github.com/excilys/androidannotations/wiki/FAQ#wiki-perf-impact)!

[![Android Annotations Logo](https://github.com/excilys/androidannotations/wiki/img/aa-logo.png)](https://github.com/excilys/androidannotations/wiki/GettingStarted) | [![Built on DEV@cloud](http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png)](https://androidannotations.ci.cloudbees.com) | [![Supported by eBusiness Information (Excilys Group)](https://github.com/excilys/androidannotations/wiki/img/supportedbylogo.png)](http://www.ebusinessinformation.fr) |
---|-----------|---------------|

Any question? Please ask them on the dedicated [mailing list](http://groups.google.com/group/androidannotations).


## Apps already using AndroidAnnotations

[Your app here](http://groups.google.com/group/androidannotations) | Air Horn | App EasyShare | Capico | Magic 8 Ball |
-------------------------------------------------------------------|----------|---------------|--------|--------------|
[Your app here](http://groups.google.com/group/androidannotations) | [![Air Horn Logo](https://github.com/excilys/androidannotations/wiki/img/air-horn.png)](https://market.android.com/details?id=com.mdb.android.airhorn) | [![App EasyShare Logo](https://github.com/excilys/androidannotations/wiki/img/app-easyshare.png)](https://market.android.com/details?id=info.piwai.marketappshare) | [![Capico Logo](https://github.com/excilys/androidannotations/wiki/img/capico.png)](https://market.android.com/details?id=com.excilys.condor.android.application)| [![Magic 8 Ball Logo](https://github.com/excilys/androidannotations/wiki/img/magic-8-ball.png)](https://market.android.com/details?id=com.mdb.android.magicball) |
Light Saber Jedi | Electric Shaver | SMS Scheduler | Coin Flip | Screen broken |
[![Light Saber Jedi Logo](https://github.com/excilys/androidannotations/wiki/img/light-saber.png)](https://market.android.com/details?id=com.mdb.android.lightsaber) | [![Electric Shaver Logo](https://github.com/excilys/androidannotations/wiki/img/electric-shaver.png)](https://market.android.com/details?id=com.mdb.android.electricshaver) | [![SMS Scheduler Logo](https://github.com/excilys/androidannotations/wiki/img/sms-scheduler.png)](https://market.android.com/details?id=com.bearstouch.smsscheduler) | [![Coin Flip](https://github.com/excilys/androidannotations/wiki/img/coin-flip.png)](https://market.android.com/details?id=com.mdb.android.cointoss) | [![Screen broken](https://lh3.ggpht.com/NMbgvJL0ZdCN-wi5nJVciRhjpp1rpMJmcuCmYcBRq-JPg3SMlfzKyfyvG1Hd7QhYPg=w124)](https://market.android.com/details?id=com.mdb.android.crackscreen) |
Application Du Jour |
[![Application Du Jour](https://lh3.ggpht.com/y0YQ64pXzBL6iWhuCWuCm3KmpMhbCRvkTk6WzIm5ROSCubkS5prCepzHkTkEP2VKftZG=w124)](https://market.android.com/details?id=com.gb.android.adj) |

***
The project logo is based on the [Android logo](http://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg), created and [shared by Google](http://code.google.com/policies.html) and used according to terms described in the [Creative Commons 3.0 Attribution License](http://creativecommons.org/licenses/by/3.0/).

Android is a trademark of Google Inc. Use of this trademark is subject to [Google Permissions](http://www.google.com/permissions/index.html). 
