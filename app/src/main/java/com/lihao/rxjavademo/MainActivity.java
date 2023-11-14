package com.lihao.rxjavademo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.lihao.rxjavademo.card.CardType;
import com.lihao.rxjavademo.card.ValidationUtils;
import com.lihao.rxjavademo.circle4_game.view.Circle4GameActivity;
import com.lihao.rxjavademo.filebrowser.FileBrowserActivity;
import com.lihao.rxjavademo.gobang_game.view.GobangGameActivity;
import com.lihao.rxjavademo.ox_game.view.OXGameActivity;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private EditText textInput;
    private EditText textCheck;
    private EditText textCVC;
    private TextView textTip;
    private Button testBtn1;
    private Button testBtn2;
    private Button testBtn3;
    private Button switchFileBrowser;
    private Button switchGameOX;
    private Button switchGameCircle4;
    private Button switchGameGobang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = findViewById(R.id.text_input);
        textCheck = findViewById(R.id.text_check);
        textCVC = findViewById(R.id.text_cvc);
        textTip = findViewById(R.id.text_tip);
        testBtn1 = findViewById(R.id.test_btn1);
        testBtn2 = findViewById(R.id.test_btn2);
        testBtn3 = findViewById(R.id.test_btn3);

        switchFileBrowser = findViewById(R.id.switch_file_browser);
        RxView.clicks(switchFileBrowser).subscribe(o -> {
            Intent intent = new Intent(this, FileBrowserActivity.class);
            startActivity(intent);
        });

        switchGameOX = findViewById(R.id.switch_game_ox);
        RxView.clicks(switchGameOX).subscribe(o -> {
            Intent intent = new Intent(this, OXGameActivity.class);
            startActivity(intent);
        });

        switchGameCircle4 = findViewById(R.id.switch_game_circle4);
        RxView.clicks(switchGameCircle4).subscribe(o -> {
            Intent intent = new Intent(this, Circle4GameActivity.class);
            startActivity(intent);
        });

        switchGameGobang = findViewById(R.id.switch_game_gobang);
        RxView.clicks(switchGameGobang).subscribe(o -> {
            Intent intent = new Intent(this, GobangGameActivity.class);
            startActivity(intent);
        });

//        firstChanges();
//        combines();
//        verifyCode();
//        reactSingleLink();
        reactAddLink();
    }

    private void firstChanges() {
        RxTextView.textChanges(textInput)
                .filter(text -> text.length() >= 7)
                .debounce(150, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> textTip.setText("您输入的信息 " + text + " 长度已经超过了7"));
    }

    private void combines() {
        Observable<CharSequence> inputObs = RxTextView.textChanges(textInput)
                .retry(3)
                .onErrorReturn(error -> "默认字符串1");
        Observable<CharSequence> checkObs = RxTextView.textChanges(textCheck)
                .retry(3)
                .onErrorReturn(error -> "默认字符串2");
        Observable<CharSequence> combinedObs = Observable
                .combineLatest(inputObs, checkObs, (text1, text2) -> text1 + "" + text2);
        combinedObs.debounce(150, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        text -> textTip.setText("字符串合并：" + text),
                        error -> textTip.setText("发生了异常：" + error.getMessage()));
    }

    private void verifyCode() {
        // 1检查有效期。
        Observable<CharSequence> expirationDateObservable = RxTextView.textChanges(textCheck);
        Observable<Boolean> isExpirationDateValid = expirationDateObservable
                .map(ValidationUtils::checkExpirationDate);

        // 2A检查卡类型。
        Observable<CharSequence> creditCardNumberBservable = RxTextView.textChanges(textInput);
        Observable<CardType> cardTypeObservable = creditCardNumberBservable
                .map(CardType::fromString);
        Observable<Boolean> isCardTypeValid = cardTypeObservable
                .map(cardType -> cardType != CardType.UNKNOWN);
        // 2B检查校验和。
        Observable<Boolean> isCheckSumValid = creditCardNumberBservable
                .map(ValidationUtils::convertFromStringToIntArray)
                .map(ValidationUtils::checkCardChecksum);
        // 2综合。
        Observable<Boolean> isCreditCardNumberValid =
                ValidationUtils.and(isCardTypeValid, isCheckSumValid);

        // 3验证cvc代码。
        Observable<Integer> requiredCvcLength = cardTypeObservable
                .map(CardType::getCvcLength);
        Observable<CharSequence> cvcCodeBservable = RxTextView.textChanges(textCVC);
        Observable<Integer> cvcInputLength = cvcCodeBservable
                .map(chars -> chars.toString().length());
        Observable<Boolean> isCVCCodeValid =
                ValidationUtils.equals(requiredCvcLength, cvcInputLength);

        // 4设置结果。
        Observable<Boolean> isFormValidObservable =
                ValidationUtils.and(isExpirationDateValid, isCreditCardNumberValid, isCVCCodeValid);
        isFormValidObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(flag -> textTip.setText("表单校验结果：" + flag));
    }

    private void reactSingleLink() {
        Observable<String> obs = Observable.interval(1, TimeUnit.SECONDS)
                .map(value -> String.valueOf(value.longValue()))
                .observeOn(AndroidSchedulers.mainThread());
        SerialDisposable sd = new SerialDisposable();
        RxView.clicks(testBtn1).subscribe(event -> sd.set(obs.subscribe(value -> textInput.setText(value))));
        RxView.clicks(testBtn2).subscribe(event -> sd.set(obs.subscribe(value -> textCheck.setText(value))));
        RxView.clicks(testBtn3).subscribe(event -> sd.set(obs.subscribe(value -> textCVC.setText(value))));
    }

    private void reactAddLink() {
        Observable<String> obs = Observable.interval(1, TimeUnit.SECONDS)
                .map(value -> String.valueOf(value.longValue()))
                .observeOn(AndroidSchedulers.mainThread());
        CompositeDisposable cd = new CompositeDisposable();
        RxView.clicks(testBtn1).subscribe(event -> cd.add(obs.subscribe(value -> textInput.setText(value))));
        RxView.clicks(testBtn2).subscribe(event -> cd.add(obs.subscribe(value -> textCheck.setText(value))));
        RxView.clicks(testBtn3).subscribe(event -> cd.add(obs.subscribe(value -> textCVC.setText(value))));
        // 取消累加订阅。
        Consumer<Object> clearTask = o -> cd.clear();
        RxView.longClicks(testBtn1).subscribe(clearTask);
        RxView.longClicks(testBtn2).subscribe(clearTask);
        RxView.longClicks(testBtn3).subscribe(clearTask);
    }
}