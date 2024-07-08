# lqr-float-input-android

- 文章：[Android - 云游戏本地悬浮输入框实现](https://juejin.cn/post/7389077092137205812)

## 效果

|                                                            Demo 效果                                                            |                                                            线上效果                                                             |
| :-----------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------: |
| <img src="https://cdn.jsdelivr.net/gh/FullStackAction/PicBed@resource20230813121546/image/202407082349052.gif" height="500px"/> | <img src="https://cdn.jsdelivr.net/gh/FullStackAction/PicBed@resource20230813121546/image/202407082150048.gif" height="500px"/> |

## 集成

```gradle
// root/build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

- **latest_version**：![](https://img.shields.io/github/v/release/GitLqr/lqr-float-input-android.svg)

```gradle
// app/build.gradle
dependencies {
    implementation "com.github.GitLqr:lqr-float-input-android:${latest_version}"
}
```

## 使用

```kotlin
class MainActivity : AppCompatActivity() {

    private val floatInput: IFloatInput by lazy { FloatInputDialog(this) }

    override fun doSomething() {
        ...
        // 显示
        floatInput.show()
        // 隐藏
        floatInput.dismiss()
    }
}
```

## 自定义

继承 [AbsFloatInputDialog](https://github.com/GitLqr/lqr-float-input-android/blob/main/float-input/src/main/java/com/gitlqr/float_input/AbsFloatInputDialog.java)，实现对应的抽象方法即可，具体参考 [FloatInputDialog.java](https://github.com/GitLqr/lqr-float-input-android/blob/main/float-input/src/main/java/com/gitlqr/float_input/FloatInputDialog.java) ：

```java
/**
 * 默认的悬浮输入框实现
 *
 * @author LQR
 * @since 2024/7/7
 */
public class FloatInputDialog extends AbsFloatInputDialog {

    private EditText inputView;

    public FloatInputDialog(Context context) {
        super(context);
    }

    public FloatInputDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_float_input;
    }

    @Override
    public EditText getInputView() {
        if (inputView == null && rootView != null) {
            inputView = rootView.findViewById(R.id.etInput);
        }
        return inputView;
    }
}

```

|                                                            支持我                                                             |                                                     关注我                                                      |
| :---------------------------------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------: |
| <img height="272" src="https://cdn.jsdelivr.net/gh/FullStackAction/PicBed@resource20230813121546/image/202406172130257.jpg"/> | <img height="272" width="100%" src="https://github.com/LinXunFeng/LinXunFeng/raw/master/static/img/FSAQR.png"/> |
