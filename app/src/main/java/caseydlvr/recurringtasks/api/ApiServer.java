package caseydlvr.recurringtasks.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.util.List;

import caseydlvr.recurringtasks.BuildConfig;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskWithTags;
import caseydlvr.recurringtasks.model.TasksAndTags;
import okhttp3.OkHttpClient;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServer {
    private static final String TAG = ApiServer.class.getSimpleName();

    private DataWebservice mService;

    public ApiServer() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new FirebaseAuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mService = retrofit.create(DataWebservice.class);
    }

    /************* Task methods *************/

    public void getAllTasks() {
        mService.getTasks().enqueue(new Callback<List<Task>>() {

            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    List<Task> tasks = response.body();
                    Log.d(TAG, tasks.toString());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void getAllTasksWithTags() {
        mService.getTasksWithTags().enqueue(new Callback<List<TaskWithTags>>() {

            @Override
            public void onResponse(Call<List<TaskWithTags>> call, Response<List<TaskWithTags>> response) {
                if (response.isSuccessful()) {
                    List<TaskWithTags> tasks = response.body();
                    Log.d(TAG, tasks.toString());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<TaskWithTags>> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void getTask(long id) {
        mService.getTask(id).enqueue(new Callback<Task>() {

            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Task task = response.body();
                    Log.d(TAG, task.toString());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void getTaskWithTags(long id) {
        mService.getTaskWithTags(id).enqueue(new Callback<TaskWithTags>() {
            @Override
            public void onResponse(Call<TaskWithTags> call, Response<TaskWithTags> response) {
                if (response.isSuccessful()) {
                    TaskWithTags task = response.body();
                    Log.d(TAG, task.toString());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<TaskWithTags> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void createTask(Task task) {
        mService.createTask(task).enqueue(new Callback<Task>() {

            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Task task = response.body();
                    Log.d(TAG, "task created: " + task);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void updateTask(long id, Task task) {
        mService.updateTask(id, task).enqueue(new Callback<Task>() {

            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Task task = response.body();
                    Log.d(TAG, "task updated: " + task);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void deleteTask(long id) {
        mService.deleteTask(id).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "delete successful");
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void completeTask(long id) {
        mService.completeTask(id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Task task = response.body();
                Log.d(TAG, "task completed, new task: " + task);
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    /************* Tag methods *************/

    public void getAllTags() {
        mService.getTags().enqueue(new Callback<List<Tag>>() {

            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    List<Tag> tags = response.body();
                    Log.d(TAG, tags.toString());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void createTag(Tag tag) {
        mService.createTag(tag).enqueue(new Callback<Tag>() {

            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                if (response.isSuccessful()) {
                    Tag tag = response.body();
                    Log.d(TAG, "Created tag: " + tag);
                } else {
                    handleErrorResponse(response);
                }

            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void updateTag(int id, Tag tag) {
        mService.updateTag(id, tag).enqueue(new Callback<Tag>() {

            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                if (response.isSuccessful()) {
                    Tag tag = response.body();
                    Log.d(TAG, "tag updated: " + tag);
                } else {
                    handleErrorResponse(response);
                }

            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    public void deleteTag(int id) {
        mService.deleteTag(id).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "delete successful");
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    /************* Full data methods *************/

    public void fullExport(TasksAndTags tasksAndTags) {
        mService.fullExport(tasksAndTags).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "data exported");
                } else {
                    handleErrorResponse(response);
                    logRequestJson(call);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    /************* Error handlers *************/

    private void handleErrorResponse(Response<?> response) {
        Log.e(TAG, response.code() + ": " + response.message());
    }

    private void handleFailure(Throwable t) {
        Log.e(TAG, t.getMessage());
    }

    /************* Logging *************/

    private void logRequestJson(Call<?> call) {
        Buffer buffer = new Buffer();

        try {
            call.request().body().writeTo(buffer);
            Log.d(TAG, buffer.readUtf8());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
