package shotmaniacs.group2.di.tests;

import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import shotmaniacs.group2.di.model.Announcement;
import shotmaniacs.group2.di.resources.AdministratorsResource;
import shotmaniacs.group2.di.resources.AnnouncementResource;
import shotmaniacs.group2.di.resources.LoginResource;

public class AnnouncementTest extends JerseyTest {
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(AnnouncementResource.class);
        return config;
    }

//    @Test
//    public Announcement getAccouncementTest() {
//
//    }

}
