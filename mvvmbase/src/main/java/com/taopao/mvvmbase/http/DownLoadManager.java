package com.taopao.mvvmbase.http;

import com.taopao.mvvmbase.http.download.DownLoadSubscriber;
import com.taopao.mvvmbase.http.download.ProgressCallBack;
import com.taopao.mvvmbase.http.interceptor.ProgressInterceptor;
import com.taopao.mvvmbase.utils.NetworkUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @Author： 淘跑
 * @Date: 2018/7/5 11:43
 * @Use：文件下载管理，封装一行代码实现下载
 */
public class DownLoadManager {
    private static DownLoadManager instance;

    private static Retrofit retrofit;

    private DownLoadManager() {
        buildNetWork();
    }

    /**
     * 单例模式
     *
     * @return DownLoadManager
     */
    public static DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    public static void load(String downUrl, final ProgressCallBack callBack) {
        retrofit.create(DownloadApiService.class)
                .download(downUrl)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        callBack.saveFile(responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(new DownLoadSubscriber<ResponseBody>(callBack));
    }

    private void buildNetWork() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ProgressInterceptor())
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(DownloadApiService.baseDownloadUrl)
                .build();
    }

    private interface DownloadApiService {
        public final static String baseDownloadUrl = "";

        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }
}
