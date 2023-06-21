package shotmaniacs.group2.di.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.Date;

@Provider
@Priority(1)
public class RoleAuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Retrieve the required roles from the resource's annotations
        RolesAllowed annotation = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);

        if (annotation == null) {
            return;
        }
        String[] requiredRoles = annotation.value();
        // Retrieve the authentication token from the request headers
        String token = requestContext.getHeaderString("Authorization");

        if (token == null || token.equals("")) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }

        // Retrieve the authenticated user's roles from session or security context
        String userRoles = verifyTokenAndGetRole(token);

        // Check if the user has at least one required role
        boolean hasRole = Arrays.asList(requiredRoles).contains(userRoles);

        if (!hasRole) {
            System.out.println("Access denied");
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }
    private String verifyTokenAndGetRole(String token) {

        Claims claims = TokenManager.decodeTokens(token);
        String role;
        Date expiration;
        if (claims == null) {
            return null;
        }
        role = claims.get("role", String.class);
        expiration = claims.getExpiration();

        if (expiration.before(new Date())) {
            return null;
        } else {
            return role;
        }
    }
}