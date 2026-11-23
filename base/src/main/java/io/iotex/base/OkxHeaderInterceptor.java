package io.iotex.base;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.util.Base64;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class OkxHeaderInterceptor implements Interceptor {

    private static final String OKX_API_BASE_URL = "https://web3.okx.com";
    private static final String OKX_API_SECRET_KEY = "0F9B630B9B021086C068B6578161114B";

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        HttpUrl httpUrl = request.url();
        if(httpUrl.toString().contains(OKX_API_BASE_URL)){
            if (METHOD_GET.equals(request.method())) { // GET

                TreeMap<String, Object> newParam = new TreeMap<>();
                Set<String> paramKeys = httpUrl.queryParameterNames();

                for (String key : paramKeys) {
                    String value = httpUrl.queryParameter(key);
                    newParam.put(key, value);
                }

                HttpUrl.Builder urlBuilder = httpUrl.newBuilder();

                for (String key : newParam.keySet()) {
                    urlBuilder.setQueryParameter(key, (String) newParam.get(key));
                }
                httpUrl = urlBuilder.build();

                try {
                    String path = httpUrl.toString().replace(OKX_API_BASE_URL,"");
                    Mac mac = Mac.getInstance("HmacSHA256");
                    SecretKeySpec secretKeySpec = new SecretKeySpec(OKX_API_SECRET_KEY.getBytes(), "HmacSHA256");
                    mac.init(secretKeySpec);
                    String time = requestBuilder.getHeaders$okhttp().get("OK-ACCESS-TIMESTAMP");
                    String data = time + "GET" + path;
                    LogUtils.i("intercept,date:"+data);
                    byte[] hmacBytes = mac.doFinal(data.getBytes());

                    String sign = Base64.encodeToString(hmacBytes, android.util.Base64.NO_WRAP);
                    LogUtils.i("intercept.sign:"+sign);
                    requestBuilder.addHeader("OK-ACCESS-SIGN", sign);
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                requestBuilder.url(httpUrl);

            } else if (METHOD_POST.equals(request.method())) { // POST

                requestBuilder.addHeader("Content-Type", "application/json");

                RequestBody requestBody = request.body();
                String paramStr = "";
                if (requestBody != null && !(requestBody instanceof MultipartBody)) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    String strOldBody = buffer.readUtf8();

                    TreeMap bodyMap = new Gson().fromJson(strOldBody, new TypeToken<TreeMap>() {}.getType());
                    if (bodyMap == null) {
                        bodyMap = new TreeMap();
                    }

                    paramStr = new Gson().toJson(bodyMap);
                    RequestBody newRequstBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(bodyMap));
                    requestBuilder.post(newRequstBody);
                }
                try {
                    String path = httpUrl.toString().replace(OKX_API_BASE_URL,"");
                    Mac mac = Mac.getInstance("HmacSHA256");
                    SecretKeySpec secretKeySpec = new SecretKeySpec(OKX_API_SECRET_KEY.getBytes(), "HmacSHA256");
                    mac.init(secretKeySpec);
                    String time = requestBuilder.getHeaders$okhttp().get("OK-ACCESS-TIMESTAMP");
                    String data = time + "POST" + path + paramStr;
                    LogUtils.i("intercept,date:"+data);
                    byte[] hmacBytes = mac.doFinal(data.getBytes());

                    String sign = Base64.encodeToString(hmacBytes, android.util.Base64.NO_WRAP);
                    LogUtils.i("intercept.sign:"+sign);
                    requestBuilder.addHeader("OK-ACCESS-SIGN", sign);
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return chain.proceed(requestBuilder.build());
    }
}
