package com.taskmanager.resource;

import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.LoginRequest;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.entity.User;
import com.taskmanager.service.TokenService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.PermitAll;  // ← ADD THIS
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    TokenService tokenService;

    @POST
    @Path("/register")
    @Transactional
    public Response register(RegisterRequest request) {
        // Validate input
        if (request.username == null || request.username.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Username is required\"}").build();
        }

        if (request.email == null || request.email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email is required\"}").build();
        }

        if (request.password == null || request.password.length() < 6) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Password must be at least 6 characters\"}").build();
        }

        // Check if user already exists
        if (User.findByUsername(request.username) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Username already exists\"}").build();
        }

        if (User.findByEmail(request.email) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Email already exists\"}").build();
        }

        // Create new user
        User user = new User();
        user.username = request.username;
        user.email = request.email;
        user.password = BcryptUtil.bcryptHash(request.password); // Hash password!
        user.persist();

        // Generate token
        String token = tokenService.generateToken(user.username, user.email);
        return Response.status(Response.Status.CREATED)
                .entity(new AuthResponse(token, user.username, user.email))
                .build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        // Find user
        User user = User.findByUsername(request.username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username or password\"}").build();
        }

        // Verify password
        if (!BcryptUtil.matches(request.password, user.password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username or password\"}").build();
        }

        // Generate token
        String token = tokenService.generateToken(user.username, user.email);
        return Response.ok(new AuthResponse(token, user.username, user.email)).build();
    }
}
