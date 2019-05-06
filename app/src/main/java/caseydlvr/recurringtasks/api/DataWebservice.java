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
    @POST("/")
    Call<Void> fullExport(@Body TasksAndTags tasksAndTags);

    @GET("/tasks")
    Call<List<Task>> getTasks();

    @GET("/tasks")
    Call<List<TaskWithTags>> getTasksWithTags();

    @GET("/tasks/{id}")
    Call<Task> getTask(@Path("id") long taskId);

    @GET("/tasks/{id}")
    Call<TaskWithTags> getTaskWithTags(@Path("id") long taskId);

    @POST("/tasks")
    Call<Task> createTask(@Body Task task);

    @POST("/tasks/{id}/complete")
    Call<Task> completeTask(@Path("id") long taskId);

    @PATCH("/tasks/{id}")
    Call<Task> updateTask(@Path("id") long taskId, @Body Task task);

    @DELETE("/tasks/{id}")
    Call<Void> deleteTask(@Path("id") long taskId);

    @GET("/tags")
    Call<List<Tag>> getTags();

    @POST("/tags")
    Call<Tag> createTag(@Body Tag tag);

    @PATCH("/tags/{id}")
    Call<Tag> updateTag(@Path("id") int tagId, @Body Tag tag);

    @DELETE("/tags/{id}")
    Call<Void> deleteTag(@Path("id") int tagId);
}
