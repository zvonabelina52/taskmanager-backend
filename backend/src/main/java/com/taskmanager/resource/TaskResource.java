package com.taskmanager.resource;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class TaskResource {

    @Inject
    SecurityIdentity securityIdentity;

    private User getCurrentUser() {
        String username = securityIdentity.getPrincipal().getName();
        return User.findByUsername(username);
    }

    @GET
    public List<Task> list() {
        User currentUser = getCurrentUser();
        return Task.list("user", currentUser);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        User currentUser = getCurrentUser();
        Task task = Task.findById(id);

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!task.user.id.equals(currentUser.id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(task).build();
    }

    @POST
    @Transactional
    public Response create(Task task) {
        User currentUser = getCurrentUser();
        task.user = currentUser;
        task.persist();
        return Response.status(Response.Status.CREATED).entity(task).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Task updatedTask) {
        User currentUser = getCurrentUser();
        Task task = Task.findById(id);

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!task.user.id.equals(currentUser.id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        task.title = updatedTask.title;
        task.description = updatedTask.description;
        task.status = updatedTask.status;
        task.priority = updatedTask.priority;

        return Response.ok(task).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        User currentUser = getCurrentUser();
        Task task = Task.findById(id);

        if (task == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!task.user.id.equals(currentUser.id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        task.delete();
        return Response.noContent().build();
    }
}