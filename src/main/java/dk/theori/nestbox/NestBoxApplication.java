package dk.theori.nestbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxFeatureCollection;
import dk.theori.nestbox.entities.NestBoxRecord;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import dk.theori.nestbox.repositories.NestBoxRecordMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@SpringBootApplication
public class NestBoxApplication implements CommandLineRunner {

	@Autowired
	private NestBoxMongoRepository nestBoxMongoRepository;

	@Autowired
	private NestBoxRecordMongoRepository nestBoxRecordMongoRepository;

	private static final String JSON_DIR = System.getenv("JSON_DIR");

	public static void main(String[] args) {
		SpringApplication.run(NestBoxApplication.class, args);

	}

	public void run(String... args){

		if(nestBoxMongoRepository.findAll().isEmpty()){
			try{
				Integer count = insertNestBoxesFromFile();
				System.out.printf("%s nest boxes inserted in Mongo DB", count);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(nestBoxRecordMongoRepository.findAll().isEmpty()){
			try{
				Integer count = insertNestBoxRecordsFromFile();
				System.out.printf("%s records inserted in Mongo DB", count);

			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}

	}

	private Integer insertNestBoxesFromFile() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		// Read JSON resource file
		File nestboxFile = new File(JSON_DIR + "/nestboxes.json");
		FileInputStream inputStream = new FileInputStream(nestboxFile);

		NestBoxFeatureCollection  featureCollection= objectMapper.readValue(inputStream,
				new TypeReference<>() {
		});

		// insert nestboxes to "nestbox" collection
		List<NestBox> nestboxes = nestBoxMongoRepository.insert(featureCollection.getFeatures());
		return nestboxes.size();
	}

	private Integer insertNestBoxRecordsFromFile() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		File nestboxrecordFile = new File(JSON_DIR + "/nestboxrecords.json");
		FileInputStream inputStream = new FileInputStream(nestboxrecordFile);

		List<NestBoxRecord> nestBoxRecords = mapper.readValue(inputStream,
                new TypeReference<>() {

                });

		//insert records to "records" collection.
		List<NestBoxRecord> inserted = nestBoxRecordMongoRepository.insert(nestBoxRecords);
		return inserted.size();
	}

}
