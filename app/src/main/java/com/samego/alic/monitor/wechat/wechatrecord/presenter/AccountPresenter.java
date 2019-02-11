package com.samego.alic.monitor.wechat.wechatrecord.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;
import com.samego.alic.monitor.wechat.wechatrecord.model.AccountModel;
import com.samego.alic.monitor.wechat.wechatrecord.model.AccountModelImpl;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetAccountListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;

public class AccountPresenter {
    private Context context;
    private AccountModel accountModel;
    private Handler handler;

    public AccountPresenter(Context context) {
        this.context = context;
        this.accountModel = new AccountModelImpl();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void syncAccount() {
        String username = SharedPreferencesUtil.get(context, "username", null);
        // 本地还没同步则同步
        if (null == username) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    accountModel.getAccount(context, new OnGetAccountListener() {
                        @Override
                        public void successful(Account account) {
                            accountModel.syncAccountMessage(context, account);
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
        } else {
            Log.i("alicfeng", "账号信息已经同步 | break");
        }
    }
}
