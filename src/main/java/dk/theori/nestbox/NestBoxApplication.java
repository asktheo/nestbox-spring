package dk.theori.nestbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;
import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxFeatureCollection;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class NestBoxApplication implements CommandLineRunner {

	@Autowired
	private NestBoxMongoRepository nestBoxMongoRepository;

	private boolean isProduction = true;

	public static void main(String[] args) {
		SpringApplication.run(NestBoxApplication.class, args);

	}

	public void run(String... args){

		if(nestBoxMongoRepository.findAll().size()==0){
			try{
				Integer count = insertNestBoxesFromFile();
				System.out.printf("%s nest boxes inserted in Mongo DB", count);
			}
			catch(Exception e){
				System.out.println(e);
			}
			System.out.println();
		}
	}

	private Integer insertNestBoxesFromFile() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		// Read JSON resource file
		InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("nestboxes.json");
		NestBoxFeatureCollection  featureCollection= objectMapper.readValue(inputStream, new TypeReference<>() {
		});
		List<NestBox> nestboxes = nestBoxMongoRepository.insert(featureCollection.getFeatures());
		return nestboxes.size();
	}

}
