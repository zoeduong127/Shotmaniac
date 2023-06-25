package shotmaniacs.group2.di.tests;

import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import shotmaniacs.group2.di.resources.AnnouncementResource;

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
