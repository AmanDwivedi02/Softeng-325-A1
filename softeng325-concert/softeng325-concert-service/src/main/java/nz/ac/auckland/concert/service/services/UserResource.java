package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.domain.CreditCard;
import nz.ac.auckland.concert.service.domain.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import nz.ac.auckland.concert.service.mappers.CreditCardMapper;
import nz.ac.auckland.concert.service.mappers.UserMapper;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("/user")
public class UserResource {

    @POST
    @Consumes(APPLICATION_XML)
    @Produces(APPLICATION_XML)
    public Response postUser(UserDTO userDTO){

        ResponseBuilder builder;

        if (userDTO.getFirstname() == null || userDTO.getFirstname().equals("") ||
                userDTO.getUsername() == null || userDTO.getUsername().equals("") ||
                userDTO.getPassword() == null || userDTO.getPassword().equals("") ||
                userDTO.getLastname() == null || userDTO.getLastname().equals("")) {
            builder = Response.status(Response.Status.PARTIAL_CONTENT);
            return builder.build();
        }

        EntityManager entityManager = null;

        try {
            entityManager = PersistenceManager.instance().createEntityManager();
            entityManager.getTransaction().begin();
            User dbUser = entityManager.find(User.class, userDTO.getUsername());

            if (dbUser != null) {
                entityManager.getTransaction().commit();
                builder = Response.status(Response.Status.CONFLICT);
                return builder.build();
            }

            User user = UserMapper.toDomain(userDTO);
            user.setAuthToken(UUID.randomUUID().toString());
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            builder = Response.created(URI.create("/user/"+user.getUsername())).entity(userDTO).cookie(makeCookie(user));
            return builder.build();
        } finally {
            if (entityManager != null && entityManager.isOpen())
            entityManager.close();
        }
    }

    @Path("/{username}")
    @GET
    @Produces(APPLICATION_XML)
    public Response getUser(@PathParam("username") String username){
        ResponseBuilder builder;

        EntityManager entityManager = null;

        try {
            entityManager = PersistenceManager.instance().createEntityManager();
            entityManager.getTransaction().begin();

            User dbUser = entityManager.find(User.class, username);

            if (dbUser == null) {
                builder = Response.status(Response.Status.NOT_FOUND);
                return builder.build();
            }

            entityManager.getTransaction().commit();

            builder = Response.ok(dbUser);
            return builder.build();


        } finally {
            if (entityManager != null && entityManager.isOpen())
                entityManager.close();
        }
    }

    @Path("/authenticate")
    @POST
    @Consumes(APPLICATION_XML)
    @Produces(APPLICATION_XML)
    public Response authenticateUser(UserDTO userDTO){

        ResponseBuilder builder;

        if (userDTO.getUsername() == null || userDTO.getUsername().equals("") ||
                userDTO.getPassword() == null || userDTO.getPassword().equals("")){
            builder = Response.status(Response.Status.PARTIAL_CONTENT);
            return builder.build();
        }

        EntityManager entityManager = null;

        try{
            entityManager = PersistenceManager.instance().createEntityManager();

            entityManager.getTransaction().begin();
            User dbUser = entityManager.find(User.class, userDTO.getUsername());
            entityManager.getTransaction().commit();

            if (dbUser == null){
                builder = Response.status(Response.Status.NOT_FOUND);
                return builder.build();
            } else if (dbUser.getPassword().equals(userDTO.getPassword())) {
                builder = Response.ok(UserMapper.toDTO(dbUser));
                builder.cookie(makeCookie(dbUser));
                return builder.build();
            } else {
                builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            }
        } finally {
            if (entityManager != null && entityManager.isOpen())
            entityManager.close();
        }
    }

    @Path("/creditcard")
    @POST
    @Consumes(APPLICATION_XML)
    @Produces(APPLICATION_XML)
    public Response postCreditCard(CreditCardDTO dtoCreditCard, @CookieParam(Config.CLIENT_COOKIE) Cookie authToken){
        ResponseBuilder builder;
        if (authToken == null) {
            builder = Response.status(Response.Status.UNAUTHORIZED);
            return builder.build();
        } else if (authToken.getValue().equals("")){
            builder = Response.status(Response.Status.UNAUTHORIZED);
            return builder.build();
        }
        EntityManager entityManager = null;

        try {
            entityManager = PersistenceManager.instance().createEntityManager();

            entityManager.getTransaction().begin();
            List<User> dbUsers = entityManager.createQuery("SELECT u FROM User u WHERE u._authToken = :authToken", User.class).setParameter("authToken", authToken.getValue()).getResultList();

            if (dbUsers == null || dbUsers.isEmpty()){
                entityManager.getTransaction().commit();
                builder = Response.status(Response.Status.NOT_FOUND);
                return builder.build();
            }

            CreditCard creditCard = CreditCardMapper.toDomain(dtoCreditCard);
            creditCard.setUser(dbUsers.get(0));

            entityManager.persist(creditCard);
            entityManager.getTransaction().commit();

            builder = Response.status(Response.Status.CREATED);

            return builder.build();
        } catch (Exception e){
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen())
            entityManager.close();
        }
    }

    private NewCookie makeCookie(User user){
        return new NewCookie(Config.CLIENT_COOKIE, user.getAuthToken());
    }
}
