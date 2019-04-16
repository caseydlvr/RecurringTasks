package caseydlvr.recurringtasks.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private static final String TOKEN_PREFIX = "Bearer ";

    private String mToken;

    public AuthInterceptor(String token) {
        mToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", TOKEN_PREFIX + mToken)
                .build();

        return chain.proceed(request);
    }
}
