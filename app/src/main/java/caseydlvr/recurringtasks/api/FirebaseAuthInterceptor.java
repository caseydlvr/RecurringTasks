package caseydlvr.recurringtasks.api;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseAuthInterceptor implements Interceptor {
    private static final String TOKEN_PREFIX = "Bearer ";

    FirebaseAuthInterceptor() {}

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                Task<GetTokenResult> idTokenTask = firebaseUser.getIdToken(false);
                GetTokenResult idToken = Tasks.await(idTokenTask);

                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", TOKEN_PREFIX + idToken.getToken())
                        .build();

                return chain.proceed(request);
            } else {
                throw new Exception("User is not logged in");
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
