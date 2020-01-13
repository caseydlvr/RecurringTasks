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
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DataWebservice {

    /******* Full data routes *******/

    @POST("./")
    Call<TasksAndTags> fullExport(@Body TasksAndTags tasksAndTags);


    /******* Task routes *******/

    @GET("tasks")
    Call<List<TaskWithTags>> getTasksWithTags();

    @GET("tasks/{uuid}")
    Call<TaskWithTags> getTaskWithTags(@Path("uuid") String taskUuid);

    @POST("tasks/{uuid}/complete")
    Call<TaskWithTags> completeTask(@Path("uuid") String taskUuid);

    @PUT("tasks/{uuid}")
    Call<TaskWithTags> createTask(@Path("uuid") String taskUuid, @Body TaskWithTags task);

    @PUT("tasks/{uuid}")
    Call<TaskWithTags> updateTask(@Path("uuid") String taskUuid, @Body TaskWithTags task);

    @DELETE("tasks/{uuid}")
    Call<Void> deleteTask(@Path("uuid") String taskUuid);


    /******* Tag routes *******/

    @GET("tags")
    Call<List<Tag>> getTags();

    @PUT("tags/{uuid}")
    Call<Tag> createTag(@Path("uuid") String tagUuid, @Body Tag tag);

    @PUT("tags/{uuid}")
    Call<Tag> updateTag(@Path("uuid") String tagUuid, @Body Tag tag);

    @DELETE("tags/{uuid}")
    Call<Void> deleteTag(@Path("uuid") String tagUuid);
}
