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

## Apps already using AndroidAnnotations

[Your app here](http://groups.google.com/group/androidannotations) | VuzZz | App EasyShare | Capico | Magic 8 Ball |
-------------------------------------------------------------------|----------|---------------|--------|--------------|
[Your app here](http://groups.google.com/group/androidannotations) | [![VuzZz](https://lh3.ggpht.com/P6lH2rtUnKFqZhQbEvV48sr67hqtZ69rGe1mV45swwyhxbyRNk_8r0zRsIUJNAEiJQ=w124)](https://market.android.com/details?id=com.vuzzz.android) | [![App EasyShare Logo](https://github.com/excilys/androidannotations/wiki/img/app-easyshare.png)](https://market.android.com/details?id=info.piwai.marketappshare) | [![Capico Logo](https://github.com/excilys/androidannotations/wiki/img/capico.png)](https://market.android.com/details?id=com.excilys.condor.android.application)| [![Magic 8 Ball Logo](https://github.com/excilys/androidannotations/wiki/img/magic-8-ball.png)](https://market.android.com/details?id=com.mdb.android.magicball) |
Light Saber Jedi | Electric Shaver | SMS Scheduler | Coin Flip | Screen broken |
[![Light Saber Jedi Logo](https://github.com/excilys/androidannotations/wiki/img/light-saber.png)](https://market.android.com/details?id=com.mdb.android.lightsaber) | [![Electric Shaver Logo](https://github.com/excilys/androidannotations/wiki/img/electric-shaver.png)](https://market.android.com/details?id=com.mdb.android.electricshaver) | [![SMS Scheduler Logo](https://github.com/excilys/androidannotations/wiki/img/sms-scheduler.png)](https://market.android.com/details?id=com.bearstouch.smsscheduler) | [![Coin Flip](https://github.com/excilys/androidannotations/wiki/img/coin-flip.png)](https://market.android.com/details?id=com.mdb.android.cointoss) | [![Screen broken](https://lh3.ggpht.com/NMbgvJL0ZdCN-wi5nJVciRhjpp1rpMJmcuCmYcBRq-JPg3SMlfzKyfyvG1Hd7QhYPg=w124)](https://market.android.com/details?id=com.mdb.android.crackscreen) |
Application Du Jour | Air Horn | Tao Po | Report Your Love | Mobilogue |
[![Application Du Jour](https://lh3.ggpht.com/y0YQ64pXzBL6iWhuCWuCm3KmpMhbCRvkTk6WzIm5ROSCubkS5prCepzHkTkEP2VKftZG=w124)](https://market.android.com/details?id=com.gb.android.adj) | [![Air Horn Logo](https://github.com/excilys/androidannotations/wiki/img/air-horn.png)](https://market.android.com/details?id=com.mdb.android.airhorn) | [![Tao Po](https://lh3.ggpht.com/369Utq4GmsXjqchIx2nv5js7gMl51P0Ccc2ZyAD_cSNRAN5hCP85AJgvHe4MCYP94hk=w124)](https://market.android.com/details?id=com.teamcodeflux.taopo) |[![Report Your Love](https://lh3.ggpht.com/peks4PvKXwJ6JjUQ8VDJ-oYGidN-DM6g7zX7F6Ih4BI1_qHRXFiWuo0j7BPkVFcRBro=w124)](https://market.android.com/details?id=it.tetractis.mappquantomipensi) | [![Mobilogue](https://lh4.ggpht.com/jKxx1DvnLzS2kZNesLIaoGH7dDpdUppsxk1ORBOvGji0pRYyotnlub5KWzIAfp9kXEo=w124)](https://market.android.com/details?id=info.collide.android.mobilogue) |
SunShine Horoscope | iLive |
[![SunShine Horoscope](https://lh3.ggpht.com/oj6WLp66P08-SlQZUskVsALNX8DM94eB4cQBSVAcWe00-4k8vM9meqD6YDNY9ax-jMg=w124)](https://market.android.com/details?id=com.solodroid.sunshine) | [![iLive](https://lh5.ggpht.com/JcdihVnq_T-o6PyMLO5NUYKfTx2x6zXIx6gAH82-IKIvbP5DDyklUk5nHesTGMR5viY=w124)](https://market.android.com/details?id=ilive.tetractis.it.activity) |

## Sponsors

[![Built on DEV@cloud](http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png)](https://androidannotations.ci.cloudbees.com) | [![Supported by eBusiness Information (Excilys Group)](https://github.com/excilys/androidannotations/wiki/img/supportedbylogo.png)](http://www.ebusinessinformation.fr) |
-----------|---------------|

***
The project logo is based on the [Android logo](http://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg), created and [shared by Google](http://code.google.com/policies.html) and used according to terms described in the [Creative Commons 3.0 Attribution License](http://creativecommons.org/licenses/by/3.0/).

Android is a trademark of Google Inc. Use of this trademark is subject to [Google Permissions](http://www.google.com/permissions/index.html). 
