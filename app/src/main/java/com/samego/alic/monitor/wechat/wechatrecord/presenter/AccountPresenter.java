package com.samego.alic.monitor.wechat.wechatrecord.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Account;
import com.samego.alic.monitor.wechat.wechatrecord.model.AccountModel;
import com.samego.alic.monitor.wechat.wechatrecord.model.AccountModelImpl;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetAccountListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.NetWorkUtils;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.view.view.AnalysisServiceView;

/**
 * 账号控制器AccountPresenter
 */
public class AccountPresenter {
    private Context context;
    private AccountModel accountModel;
    private Handler handler;
    private AnalysisServiceView analysisServiceView;

    public AccountPresenter(Context context, AnalysisServiceView analysisServiceView) {
        this.context = context;
        this.accountModel = new AccountModelImpl();
        this.handler = new Handler(Looper.getMainLooper());
        this.analysisServiceView = analysisServiceView;
    }

    /**
     * 同步微信账号信息
     */
    public void syncAccount() {
        final String username = SharedPreferencesUtil.get(context, "username", null);
        // 本地还没同步则同步
//        if (null == username) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    accountModel.getAccount(context, new OnGetAccountListener() {
                        @Override
                        public void successful(Account account) {
                            if (NetWorkUtils.isNetworkConnected(context)) {
                                accountModel.syncAccountMessage(context, account);
                                Log.i("alicfeng", "账号信息已经同步 | " + username);
                            } else {
                                analysisServiceView.networkUnavailability();
                            }
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }
            });
//        } else {
//            Log.i("alicfeng", "账号信息已经同步 | " + username);
//        }
    }
}
