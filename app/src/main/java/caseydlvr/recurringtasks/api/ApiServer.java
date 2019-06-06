package caseydlvr.recurringtasks.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.util.List;

import caseydlvr.recurringtasks.BuildConfig;
import caseydlvr.recurringtasks.DataRepository;
import caseydlvr.recurringtasks.model.Deletion;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskWithTags;
import caseydlvr.recurringtasks.model.TasksAndTags;
import okhttp3.OkHttpClient;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ApiServer {

    private DataWebservice mService;
    private DataRepository mRepository;

    public ApiServer(DataRepository repository) {
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
        mRepository = repository;
    }

    /************* Task methods *************/

    public void getAllTasks() {
        mService.getTasks().enqueue(new Callback<List<Task>>() {

            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    List<Task> tasks = response.body();
                    Timber.d("%s", tasks);
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
                    Timber.d("%s", tasks);
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

    public void getTask(int id) {
        mService.getTask(id).enqueue(new Callback<Task>() {

            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    Task task = response.body();
                    Timber.d("%s", task);
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

    public void getTaskWithTags(int id) {
        mService.getTaskWithTags(id).enqueue(new Callback<TaskWithTags>() {
            @Override
            public void onResponse(Call<TaskWithTags> call, Response<TaskWithTags> response) {
                if (response.isSuccessful()) {
                    TaskWithTags task = response.body();
                    Timber.d("%s", task);
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

    public void createTask(TaskWithTags task) {
        try {
            Response<TaskWithTags> response = mService.createTask(task).execute();

            if (response.isSuccessful()) {
                Task responseTask = response.body();

                if (responseTask != null) {
                    responseTask.setId(task.getId());
                    responseTask.setSynced(true);

                    mRepository.syncTask(responseTask);

                    Timber.d("task created: %s", responseTask);
                }
            } else {
                handleErrorResponse(response);
            }
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    public void updateTask(int id, TaskWithTags task) {
        try {
            Response<TaskWithTags> response = mService.updateTask(id, task).execute();

            if (response.isSuccessful()) {
                Task responseTask = response.body();

                if (responseTask != null) {
                    responseTask.setId(task.getId());
                    responseTask.setSynced(true);

                    mRepository.syncTask(responseTask);

                    Timber.d("task updated: %s", responseTask);
                }
            } else {
                handleErrorResponse(response);
            }
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    public void deleteTask(int id) {
        try {
            Response<Void> response = mService.deleteTask(id).execute();

            if (response.isSuccessful()) {
                Timber.d("task %d delete successful", id);
            } else {
                handleErrorResponse(response);
            }

            mRepository.deleteDeletion(Deletion.taskDeletion(id));
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    public void completeTask(int id) {
        mService.completeTask(id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                Task task = response.body();
                Timber.d("task completed, new task: %s", task);
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
                    Timber.d("%s", tags);
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
        try {
            Response<Tag> response = mService.createTag(tag).execute();

            if (response.isSuccessful()) {
                Tag responseTag = response.body();

                if (responseTag != null) {
                    responseTag.setId(tag.getId());
                    responseTag.setSynced(true);

                    Timber.d("tag created: %s", responseTag);

                    mRepository.syncTag(responseTag);
                } else {
                    Timber.e("bad data returned from create");
                }
            } else {
                handleErrorResponse(response);
            }
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    public void updateTag(int id, Tag tag) {
        try {
            Response<Tag> response = mService.updateTag(id, tag).execute();

            if (response.isSuccessful()) {
                Tag responseTag = response.body();

                if (responseTag != null) {
                    responseTag.setId(tag.getId());
                    responseTag.setSynced(true);

                    Timber.d("tag updated: %s", responseTag);

                    mRepository.syncTag(responseTag);
                } else {
                    Timber.e("bad data returned from update");
                }
            } else {
                handleErrorResponse(response);
            }
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    public void deleteTag(int id) {
        try {
            Response<Void> response = mService.deleteTag(id).execute();

            if (response.isSuccessful()) {
                Timber.d("tag %d delete successful", id);
            } else {
                handleErrorResponse(response);
            }

            mRepository.deleteDeletion(Deletion.tagDeletion(id));
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    /************* Full data methods *************/

    public TasksAndTags fullExport(TasksAndTags tasksAndTags) throws IOException, HttpException {
        Response<TasksAndTags> response = mService.fullExport(tasksAndTags).execute();

        if (response.isSuccessful()) {
            Timber.d("data exported");

            TasksAndTags responseTasksAndTags = response.body();

            for (Tag tag : responseTasksAndTags.getTags()) {
                tag.setId(tasksAndTags.getTags()
                        .get(responseTasksAndTags.getTags().indexOf(tag))
                        .getId());
            }

            for (TaskWithTags task : responseTasksAndTags.getTaskWithTags()) {
                task.setId(tasksAndTags.getTaskWithTags()
                        .get(responseTasksAndTags.getTaskWithTags().indexOf(task))
                        .getId());
            }

            return responseTasksAndTags;
        } else {
            handleErrorResponse(response);

            throw new HttpException(response);
        }
    }

    /************* Error handlers *************/

    private void handleErrorResponse(Response<?> response) {
        Timber.e("%d: %s", response.code(), response.message());
    }

    private void handleFailure(Throwable t) {
        t.printStackTrace();
        Timber.e(t);
    }

    /************* Logging *************/

    private void logRequestJson(Call<?> call) {
        Buffer buffer = new Buffer();

        try {
            call.request().body().writeTo(buffer);
            Timber.d(buffer.readUtf8());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
