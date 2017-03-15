package zyzx.linke.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * 检查网络的工具类
 */
public class NetworkUtil {
    /**
     * 检查网络
     */
    public static boolean checkNetwork(Context context) {
        // 判断手机端利用的通信渠道

        // ①判断WIFI可以连接
        boolean isWIFI = isWIFICon(context);
        // ②判断MOBILE可以连接
        boolean isMOBILE = isMOBILECon(context);
        // 如果都无法使用——提示用户
        if (!isWIFI && !isMOBILE) {
            return false;
        }
        // 如果有可以利用的通信渠道，是不是MOBILE
        if (isMOBILE) {
            // 如果是，是否是wap方式
            // 读取APN配置信息，如果发现代理信息非空
            // readAPN(context);// 读取联系人信息
        }
        return true;
    }

    /**
     * 判断WIFI可以连接
     *
     * @param context
     * @return
     */
    public static boolean isWIFICon(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 判断MOBILE可以连接
     *
     * @param context
     * @return
     */
    private static boolean isMOBILECon(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;
    }


    //API版本23及以上时调用此方法进行网络的检测
    public static boolean checkState_21orNew(Context context){
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取所有网络连接的信息
        Network[] networks = new Network[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            networks = connMgr.getAllNetworks();
        }
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        for (int i=0; i < networks.length; i++){
            //获取ConnectivityManager对象对应的NetworkInfo对象
            NetworkInfo networkInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                networkInfo = connMgr.getNetworkInfo(networks[i]);
                if(networkInfo.isConnected()){
                    return true;
                }
            }
        }
        return false;
    }
}