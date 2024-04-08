package dk.theori.nestbox.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.theori.nestbox.entities.*;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import dk.theori.nestbox.repositories.NestBoxStatusRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ImportUtilTest {

    @Autowired
    NestBoxStatusRepository statusMongoRepository;

    @Autowired
    private NestBoxMongoRepository nestBoxMongoRepository;

    private static String TSVInput = "Tidsstempel\tKasse nummer\tStatus\tDato\tBemærkninger\tMailadresse\tUnger\tÆg\tArt\tTilstand\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "29/03/2024 10.21.42\t1\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.22.01\t2\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.22.29\t3\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.24.42\t8\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.28.19\t402\tTom\t29/03/2024\tNyt tag\tassistent_xyz@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.29.09\t9\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.31.29\t12\tTom\t29/03/2024\tSkruelåg\tassistent_xyz@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.32.14\t201\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.34.33\t13\tRede\t29/03/2024\tMetalplade til hul\tassistent_xyz@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.36.18\t14\tRede\t29/03/2024\tKastanjer\tassistent_xyz@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 10.37.58\t15\tTom\t29/03/2024\tMedtaget kasse\tassistent_xyz@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 10.38.40\t16\tTom\t29/03/2024\tMedtaget\tassistent_xyz@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 10.40.26\t19\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.40.56\t24\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.41.47\t17\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.42.33\t18\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.45.22\t21\tTom\t29/03/2024\t\tassistent_xyz@gmail.com\t\t\t\tGod";

    private static String json;
    private static List<RecordFromTSV> recordsFromTSV;

    private static List<NestBox> nestboxesFromFile;

    private static final String JSON_DIR = System.getenv("JSON_DIR");

    @BeforeAll
    public static void testTSV2Object() throws IOException {
        json = ImportUtil.TSV2Json(TSVInput);
        ObjectMapper mapper = new ObjectMapper();

        recordsFromTSV = mapper.readValue(json, new TypeReference<>(){});
        assertFalse(recordsFromTSV.isEmpty());

    }

    @Test
    public void recordsFromTSV2NestBoxRecords() {
       List<NestBoxRecord> nestboxrecords = recordsFromTSV.stream().map(p-> {
            NestBoxRecord r = new NestBoxRecord();
            NestingDetails nd = new NestingDetails();
            nd.setChicks(p.getUnger() == null || p.getUnger().isEmpty()? null : Integer.parseInt(p.getUnger()));
            nd.setEggs(p.getÆg() == null || p.getÆg().isEmpty()? null : Integer.parseInt(p.getÆg()));
            nd.setSpecies(p.getArt());
            r.setNesting(nd);

            Optional<NestBox> nb = nestBoxMongoRepository.findAll().stream().filter(n -> n.getBoxId().equals(p.getKasse_nummer())).findAny();
            nb.ifPresent(nestBox -> r.setFid(nestBox.getFid()));
            NestBoxStatus status = new NestBoxStatus();
            status.setStatusName(p.getStatus());
            List<NestBoxStatus> statuses = statusMongoRepository.findAll().stream().toList();
            Optional<NestBoxStatus> foundStatus = statuses.stream().filter(s -> s.getStatusName().equalsIgnoreCase(p.getStatus())).findAny();
            foundStatus.ifPresent(nestBoxStatus -> status.setIntervalInDaysSelected(nestBoxStatus.getIntervalInDaysSelected()));
            r.setStatus(status);
            r.setComment(p.getBemærkninger());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH.mm.ss").localizedBy(new Locale("da", "DK"));
            LocalDateTime dateTime = LocalDateTime.parse(p.getTidsstempel(), formatter);
            r.setDatetime(dateTime);
            r.setUserEmail(p.getMailadresse());
            System.out.println(r.toString());

            return r;
        }).toList();
       assertTrue(nestboxrecords.size() > 16);
       assertEquals(2, nestboxrecords.stream().filter(nbr -> nbr.getStatus() != null && "Rede".equals(nbr.getStatus().getStatusName())).count());

    }


}