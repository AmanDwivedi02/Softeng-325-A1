package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.Seat;

import javax.persistence.EntityManager;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static nz.ac.auckland.concert.service.mappers.SeatMapper.toDomain;
import static nz.ac.auckland.concert.service.util.TheatreUtility.findAvailableSeats;

/**
 * JAX-RS Application subclass for the Concert Web service.
 *
 *
 *
 */
@ApplicationPath("/services")
public class ConcertApplication extends Application {

    // This property should be used by your Resource class. It represents the
    // period of time, in seconds, that reservations are held for. If a
    // reservation isn't confirmed within this period, the reserved seats are
    // returned to the pool of seats available for booking.
    //
    // This property is used by class ConcertServiceTest.

    private Set<Object> _singletons = new HashSet<>();
    private Set<Class<?>> _classes = new HashSet<>();

    public ConcertApplication() {

        EntityManager entityManager = null;

        try {

            Set<SeatDTO> seatDtoA = findAvailableSeats(Config.BAND_A_SEATS, PriceBand.PriceBandA, new HashSet<SeatDTO>());
            Set<SeatDTO> seatDtoB = findAvailableSeats(Config.BAND_B_SEATS, PriceBand.PriceBandB, new HashSet<SeatDTO>());
            Set<SeatDTO> seatDtoC = findAvailableSeats(Config.BAND_C_SEATS, PriceBand.PriceBandC, new HashSet<SeatDTO>());

            PersistenceManager pm = new PersistenceManager();
            entityManager = pm.instance().createEntityManager();

            entityManager.getTransaction().begin();
            for (SeatDTO seatDTO : seatDtoA){
                entityManager.persist(toDomain(seatDTO));
            }
            entityManager.getTransaction().commit();

            entityManager.getTransaction().begin();
            for (SeatDTO seatDTO : seatDtoB){
                entityManager.persist(toDomain(seatDTO));
            }
            entityManager.getTransaction().commit();

            entityManager.getTransaction().begin();
            for (SeatDTO seatDTO : seatDtoC){
                entityManager.persist(toDomain(seatDTO));
            }
            entityManager.getTransaction().commit();

            _classes.add(ConcertResource.class);
            _classes.add(PerformerResource.class);
            _classes.add(UserResource.class);
            _classes.add(ReservationResource.class);
            _singletons.add(pm);

        } finally {
            if (entityManager == null || entityManager.isOpen()) {
            } else {
                entityManager.close();
            }
        }
    }

    @Override
    public Set<Object> getSingletons() { return _singletons;}

    @Override
    public Set<Class<?>> getClasses() {return _classes;}
}