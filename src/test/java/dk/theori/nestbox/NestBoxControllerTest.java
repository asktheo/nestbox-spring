package dk.theori.nestbox;

import dk.theori.nestbox.entities.NestBoxProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
class NestBoxControllerTest {

    @Test
    void nestBoxFeatureById() {
    }
    @Test
    void setOffLine() {
        NestBoxController controller = new NestBoxController();
        NestBoxProperties boxprops = controller.setOffLine(11, true);

        NestBoxProperties boxPropsFromDB = controller.nestBoxFeatureById(11).getProperties();
        assertTrue(boxPropsFromDB.getIsOffline());
        assert(boxPropsFromDB.getIsOffline() == boxprops.getIsOffline());
    }
}