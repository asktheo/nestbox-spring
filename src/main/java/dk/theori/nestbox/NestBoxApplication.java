package dk.theori.nestbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxFeatureCollection;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
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
	}

	private Integer insertNestBoxesFromFile() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		// Read JSON resource file
		File nestboxFile = new File(JSON_DIR + "/nestboxes.json");
		FileInputStream inputStream = new FileInputStream(nestboxFile);

		NestBoxFeatureCollection  featureCollection= objectMapper.readValue(inputStream, new TypeReference<>() {
		});

		// insert nestboxes to "nestbox" repository
		List<NestBox> nestboxes = nestBoxMongoRepository.insert(featureCollection.getFeatures());
		return nestboxes.size();
	}

}
