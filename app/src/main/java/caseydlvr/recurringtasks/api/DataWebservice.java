package caseydlvr.recurringtasks.api;

import java.util.List;

import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskWithTags;
import caseydlvr.recurringtasks.model.TasksAndTags;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DataWebservice {
    @POST("./")
    Call<TasksAndTags> fullExport(@Body TasksAndTags tasksAndTags);

    @GET("tasks")
    Call<List<Task>> getTasks();

    @GET("tasks")
    Call<List<TaskWithTags>> getTasksWithTags();

    @GET("tasks/{id}")
    Call<Task> getTask(@Path("id") int taskId);

    @GET("tasks/{id}")
    Call<TaskWithTags> getTaskWithTags(@Path("id") int taskId);

    @POST("tasks")
    Call<TaskWithTags> createTask(@Body TaskWithTags task);

    @POST("tasks/{id}/complete")
    Call<Task> completeTask(@Path("id") int taskId);

    @PATCH("tasks/{id}")
    Call<TaskWithTags> updateTask(@Path("id") int taskId, @Body TaskWithTags task);

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(@Path("id") int taskId);

    @GET("tags")
    Call<List<Tag>> getTags();

    @POST("tags")
    Call<Tag> createTag(@Body Tag tag);

    @PATCH("tags/{id}")
    Call<Tag> updateTag(@Path("id") int tagId, @Body Tag tag);

    @DELETE("tags/{id}")
    Call<Void> deleteTag(@Path("id") int tagId);
}
