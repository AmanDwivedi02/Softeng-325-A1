package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.mappers.ConcertMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * Class to implement a simple REST Web service for managing Concerts.
 */
@Path("/concert")
public class ConcertResource {

    @GET
    @Produces({APPLICATION_XML})
    public Response getAllConcerts() {

        Response.ResponseBuilder builder = null;
        EntityManager entityManager = null;
        try {
            entityManager = PersistenceManager.instance().createEntityManager();

            entityManager.getTransaction().begin();
            TypedQuery<Concert> concertQuery =
                    entityManager.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();
            entityManager.getTransaction().commit();
            if (concerts != null) {
                List<ConcertDTO> concertDTOs = new ArrayList<>();
                for (Concert concert : concerts) {
                    concertDTOs.add(ConcertMapper.toDTO(concert));
                }
                GenericEntity<List<ConcertDTO>> concertEntity = new GenericEntity<List<ConcertDTO>>(concertDTOs) {
                };
                builder = Response.ok(concertEntity);
                return builder.build();
            }
            builder = Response.status(Response.Status.NOT_FOUND);
            return builder.build();
        } finally {
            if (entityManager != null && entityManager.isOpen())
                entityManager.close();
        }
    }
}