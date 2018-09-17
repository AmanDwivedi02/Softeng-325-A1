package nz.ac.auckland.concert.client.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.common.dto.*;
import nz.ac.auckland.concert.common.message.Messages;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

public class DefaultService implements ConcertService {

    private static final String URI = "http://localhost:10000/services";

    private String cookieValue;

    // AWS S3 access credentials for concert images.
    private static final String AWS_ACCESS_KEY_ID = "AKIAJOG7SJ36SFVZNJMQ";
    private static final String AWS_SECRET_ACCESS_KEY = "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";

    // Name of the S3 bucket that stores images.
    private static final String AWS_BUCKET = "concert2.aucklanduni.ac.nz";

    // Download directory - a directory named "images" in the user's home
    // directory.
    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");
    private static final String USER_DIRECTORY = System
            .getProperty("user.home");
    private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
            + FILE_SEPARATOR + "images";

    @Override
    public Set<ConcertDTO> getConcerts() throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/concert").request(APPLICATION_XML).accept(APPLICATION_XML);
            Response response = builder.get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<Set<ConcertDTO>>(){});
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public Set<PerformerDTO> getPerformers() throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/performer").request(APPLICATION_XML).accept(APPLICATION_XML);
            Response response = builder.get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<Set<PerformerDTO>>(){});
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public UserDTO createUser(UserDTO newUser) throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/user")
                    .request(APPLICATION_XML).accept(APPLICATION_XML);
            Response response = builder.post(Entity.entity(newUser, APPLICATION_XML));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                cookieValue = response.getCookies().get(Config.CLIENT_COOKIE).getValue();
                return response.readEntity(UserDTO.class);
            } else if (response.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode()) {
                throw new ServiceException(Messages.CREATE_USER_WITH_MISSING_FIELDS);
            } else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new ServiceException(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME);
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public UserDTO authenticateUser(UserDTO user) throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/user/authenticate").request(APPLICATION_XML).accept(APPLICATION_XML);
            Response response = builder.post(Entity.entity(user, APPLICATION_XML));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                cookieValue = response.getCookies().get(Config.CLIENT_COOKIE).getValue();
                return response.readEntity(UserDTO.class);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ServiceException(Messages.AUTHENTICATE_NON_EXISTENT_USER);
            } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new ServiceException(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD);
            } else if (response.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode()) {
                throw new ServiceException(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS);
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
        // Create download directory if it doesn't already exist.
        File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
        downloadDirectory.mkdir();

        try {
            File imageFile = new File(downloadDirectory, performer.getImageName());
            return ImageIO.read(imageFile);
        } catch (Exception e){}

        // Create an AmazonS3 object that represents a connection with the
        // remote S3 service.
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_SOUTHEAST_2)
                .withCredentials(
                        new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        // Find images names stored in S3.
        List<String> imageNames = getImageNames(s3);


        if (imageNames.contains(performer.getImageName())) {
            download(s3, performer.getImageName());
        } else {
            throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
        }
        try {
            File imageFile = new File(downloadDirectory, performer.getImageName());
            return ImageIO.read(imageFile);
        } catch (Exception e) {
            throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
        }
    }

    @Override
    public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/reservation").request(APPLICATION_XML).accept(APPLICATION_XML).cookie(Config.CLIENT_COOKIE, cookieValue);
            Response response = builder.post(Entity.entity(reservationRequest, APPLICATION_XML));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return response.readEntity(ReservationDTO.class);
            } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ServiceException(Messages.BAD_AUTHENTICATON_TOKEN);
            } else if (response.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode()) {
                throw new ServiceException(Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS);
            } else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                throw new ServiceException(Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE);
            } else if (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode()) {
                throw new ServiceException(Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION);
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public void confirmReservation(ReservationDTO reservation) throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/reservation/confirm").request(APPLICATION_XML).accept(APPLICATION_XML).cookie(Config.CLIENT_COOKIE, cookieValue);
            Response response = builder.post(Entity.entity(reservation, APPLICATION_XML));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return;
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ServiceException(Messages.BAD_AUTHENTICATON_TOKEN);
            } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
            } else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                throw new ServiceException(Messages.EXPIRED_RESERVATION);
            } else if (response.getStatus() == 422){
                throw new ServiceException(Messages.CREDIT_CARD_NOT_REGISTERED);
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/user/creditcard").request(APPLICATION_XML).accept(APPLICATION_XML).cookie(Config.CLIENT_COOKIE, cookieValue);
            Response response = builder.post(Entity.entity(creditCard, APPLICATION_XML));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return;
            } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ServiceException(Messages.BAD_AUTHENTICATON_TOKEN);
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    @Override
    public Set<BookingDTO> getBookings() throws ServiceException {
        Client client = ClientBuilder.newClient();
        try {
            Builder builder = client.target(URI + "/reservation").request(APPLICATION_XML).accept(APPLICATION_XML).cookie(Config.CLIENT_COOKIE, cookieValue);
            Response response = builder.get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<Set<BookingDTO>>(){});
            } else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()){
                throw new ServiceException(Messages.UNAUTHENTICATED_REQUEST);
            } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()){
                throw new ServiceException(Messages.BAD_AUTHENTICATON_TOKEN);
            } else {
                throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
            }
        } finally {
            client.close();
        }
    }

    private static List<String> getImageNames(AmazonS3 s3) {

        List<String> imageNames = new ArrayList<>();
        ObjectListing listing = s3.listObjects(AWS_BUCKET);
        List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        for (S3ObjectSummary summary : summaries) {
            imageNames.add(summary.getKey());
        }
        return imageNames;
    }

    private static void download(AmazonS3 s3, String imageName) {

        File downloadDirectory = new File(DOWNLOAD_DIRECTORY);


        File imageFile = new File(downloadDirectory, imageName);

        GetObjectRequest req = new GetObjectRequest(AWS_BUCKET, imageName);
        s3.getObject(req, imageFile);
    }

}
