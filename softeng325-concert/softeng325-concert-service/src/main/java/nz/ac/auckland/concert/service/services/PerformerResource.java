package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.service.domain.Performer;
import nz.ac.auckland.concert.service.mappers.PerformerMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("/performer")
public class PerformerResource {

    @GET
    @Produces(APPLICATION_XML)
    public Response getPerformers(){
        Response.ResponseBuilder builder = null;
        EntityManager entityManager = null;
        try {
            entityManager = PersistenceManager.instance().createEntityManager();

            entityManager.getTransaction().begin();
            TypedQuery<Performer> performerQuery =
                    entityManager.createQuery("select p from Performer p", Performer.class);
            List<Performer> performers = performerQuery.getResultList();
            entityManager.getTransaction().commit();

            if (performers != null) {
                List<PerformerDTO> performerDTOs = new ArrayList<>();
                for (Performer performer : performers) {
                    performerDTOs.add(PerformerMapper.toDTO(performer));
                }
                GenericEntity<List<PerformerDTO>> performerEntity = new GenericEntity<List<PerformerDTO>>(performerDTOs){};
                builder = Response.ok(performerEntity);
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
