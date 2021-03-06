package com.taopao.baseapp.ui.viewmodel;

import android.databinding.ObservableField;

import com.taopao.baseapp.ui.activity.MainActivity;
import com.taopao.baseapp.ui.activity.RefreshActivity;
import com.taopao.mvvmbase.base.BaseMVVMActivity;
import com.taopao.mvvmbase.base.BaseMVVMViewModel;
import com.taopao.mvvmbase.binding.command.BindingAction;
import com.taopao.mvvmbase.binding.command.BindingCommand;
import com.taopao.mvvmbase.utils.RxUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author： 淘跑
 * @Date: 2018/7/17 11:15
 * @Use：
 */
public class LoginViewModel extends BaseMVVMViewModel {
    public LoginViewModel(BaseMVVMActivity activity) {
        super(activity);
    }

    //用户名
    public ObservableField<String> userName = new ObservableField<>("");
    //密码
    public ObservableField<String> password = new ObservableField<>("");

    //登录按钮的点击事件
    public BindingCommand login = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            showDialog("用户名:" + userName.get() + "\n密码:" + password.get());
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .compose(RxUtils.<Long>schedulersTransformer())//timer 默认在新线程，所以需要切换回主线程
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            HideDialog();
                            startActivity(RefreshActivity.class);
                        }
                    });
        }
    });

}
