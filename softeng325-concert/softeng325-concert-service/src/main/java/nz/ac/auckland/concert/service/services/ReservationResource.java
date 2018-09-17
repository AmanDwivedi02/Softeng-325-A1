package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.*;
import nz.ac.auckland.concert.service.mappers.BookingMapper;
import nz.ac.auckland.concert.service.mappers.SeatMapper;
import nz.ac.auckland.concert.service.util.TheatreUtility;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("/reservation")
public class ReservationResource {

    @POST
    @Consumes(APPLICATION_XML)
    @Produces(APPLICATION_XML)
    public Response reserveSeats(ReservationRequestDTO dtoReservationRequest, @CookieParam(Config.CLIENT_COOKIE) Cookie authToken){
        ResponseBuilder builder;
        if (authToken == null || authToken.getValue().equals("")) {
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
            } else if (dtoReservationRequest.getConcertId() == null || dtoReservationRequest.getDate() == null ||
                    dtoReservationRequest.getSeatType() == null) {
                entityManager.getTransaction().commit();
                builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                return builder.build();
            }

            Concert dbConcert = entityManager.find(Concert.class, dtoReservationRequest.getConcertId());

            if (!dbConcert.getDates().contains(dtoReservationRequest.getDate())){
                entityManager.getTransaction().commit();
                builder = Response.status(Response.Status.BAD_REQUEST);
                return builder.build();
            }

            List<Booking> bookings = entityManager.createQuery("SELECT b FROM Booking b WHERE b._concert._id = :concertId and b._dateTime = :concertDate and b._priceBand = :concertPrice", Booking.class)
                    .setParameter("concertId", dbConcert.getId())
                    .setParameter("concertDate", dtoReservationRequest.getDate())
                    .setParameter("concertPrice", dtoReservationRequest.getSeatType())
                    .getResultList();

            Set<SeatDTO> bookedSeats = new HashSet<>();
            for (Booking booking : bookings) {
                for (Seat seat : booking.getSeats()) {
                    bookedSeats.add(SeatMapper.toDTO(seat));
                }
            }
            Set<SeatDTO> seatToBook = TheatreUtility.findAvailableSeats(dtoReservationRequest.getNumberOfSeats(), dtoReservationRequest.getSeatType(), bookedSeats);
            if (seatToBook.isEmpty()){
                entityManager.getTransaction().commit();
                builder = Response.status(Response.Status.NOT_ACCEPTABLE);
                return builder.build();
            }

            Set<Seat> seatSet = new HashSet<>();
            for (SeatDTO seat : seatToBook) {
                seatSet.add(SeatMapper.toDomain(seat));
            }

            Booking booking = new Booking(
                    dbConcert,
                    dbUsers.get(0),
                    dtoReservationRequest.getDate(),
                    seatSet,
                    dtoReservationRequest.getSeatType(),
                    LocalDateTime.now().plusSeconds(5),
                    false);

            entityManager.persist(booking);
            entityManager.getTransaction().commit();

            ReservationDTO reservation = new ReservationDTO(booking.getId(), dtoReservationRequest, seatToBook);

            builder = Response.status(Response.Status.CREATED).entity(reservation);

            return builder.build();
        } catch (Exception e){
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    @Path("/confirm")
    @POST
    @Consumes(APPLICATION_XML)
    public Response confirmBooking(ReservationDTO dtoReservation, @CookieParam(Config.CLIENT_COOKIE) Cookie authToken){
        ResponseBuilder builder;
        if (authToken == null) {
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

            Booking booking = entityManager.find(Booking.class, dtoReservation.getId());

            if (booking == null || booking.getExpiryTime().isBefore(LocalDateTime.now())){
                entityManager.getTransaction().commit();
                builder = Response.status(Response.Status.BAD_REQUEST);
                return builder.build();
            }

            List<CreditCard> creditCards = entityManager.createQuery("SELECT c FROM CreditCard c WHERE c._user = :user", CreditCard.class)
                    .setParameter("user", dbUsers.get(0))
                    .getResultList();

            if (creditCards == null || creditCards.isEmpty()){
                entityManager.getTransaction().commit();
                builder = Response.status(422);
                return builder.build();
            }

            booking.setBooked(true);

            entityManager.merge(booking);
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

    @GET
    @Produces(APPLICATION_XML)
    public Response getBookings(@CookieParam(Config.CLIENT_COOKIE) Cookie authToken){
        ResponseBuilder builder;
        if (authToken == null) {
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

            List<Booking> bookings = entityManager.createQuery("SELECT b FROM Booking b WHERE b._user._username = :userId AND b._booked = TRUE", Booking.class)
                    .setParameter("userId", dbUsers.get(0)
                            .getUsername()).getResultList();
            entityManager.getTransaction().commit();

            Set<BookingDTO> bookingSet = new HashSet<>();
            for (Booking booking : bookings) {
                bookingSet.add(BookingMapper.toDTO(booking));
            }

            GenericEntity<Set<BookingDTO>> bookingEntity = new GenericEntity<Set<BookingDTO>>(bookingSet){};
            builder = Response.ok(bookingEntity);

            return builder.build();
        } catch (Exception e){
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen())
                entityManager.close();
        }
    }
}
