package dk.theori.nestbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.theori.nestbox.env.EnvVariables;
import dk.theori.nestbox.entities.NestBox;
import dk.theori.nestbox.entities.NestBoxFeatureCollection;
import dk.theori.nestbox.entities.Zone;
import dk.theori.nestbox.repositories.NestBoxMongoRepository;
import dk.theori.nestbox.repositories.NestBoxRecordMongoRepository;
import dk.theori.nestbox.repositories.ZoneMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class NestBoxApplication implements CommandLineRunner {


	@Autowired
	private NestBoxMongoRepository nestBoxMongoRepository;

	@Autowired
	private NestBoxRecordMongoRepository nestBoxRecordMongoRepository;

	@Autowired
	private ZoneMongoRepository zoneMongoRepository;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}


	@Bean
	public EnvVariables envVariables() {
		EnvVariables envVariables = new EnvVariables();
		envVariables.addKey(EnvVariables.SECRET, System.getenv(EnvVariables.SECRET));
		envVariables.addKey(EnvVariables.JSON_DIR, System.getenv(EnvVariables.JSON_DIR));
		return envVariables;
	}

	public static void main(String[] args) {
		SpringApplication.run(NestBoxApplication.class, args);

	}

	public void run(String... args){


		if(nestBoxMongoRepository.findAll().isEmpty()){
			try{
				Integer count = insertNestBoxesFromFile();
				System.out.printf("%s nest boxes inserted in Mongo DB\n", count);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}


	}

	private Integer insertNestBoxesFromFile() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		// Read JSON resource file
		File nestboxFile = new File(envVariables().getEnvVariable(EnvVariables.JSON_DIR) + "/nestboxes.geojson");
		FileInputStream inputStream = new FileInputStream(nestboxFile);

		NestBoxFeatureCollection featureCollection= objectMapper.readValue(inputStream,
				new TypeReference<>() {
		});

		// insert nestboxes to "nestbox" collection
		List<NestBox> nestboxes = nestBoxMongoRepository.insert(featureCollection.getFeatures());

		//retrieve and insert zones to DB
		System.out.printf("%d zones found and inserted",insertZones(nestboxes));

		return nestboxes.size();
	}

	private long insertZones(List<NestBox> nestBoxes){
		//clear and insert zones
		zoneMongoRepository.deleteAll();
		HashMap<String,ArrayList<Integer>> zones = new HashMap<>();

		//clumsy way to reduce and finding unique zone numbers, but it works and we add the boxes to a list
		for ( NestBox nb : nestBoxes){
			if(!zones.containsKey(nb.getZone())){
				ArrayList<Integer> boxes = new ArrayList<>();
				boxes.add(nb.getFid());
				zones.put(nb.getZone(), boxes);
			}
			else
			{
				zones.get(nb.getZone()).add(nb.getFid());
			}

		}
		//insert in repository
		for(String zoneNumber : zones.keySet()){
			Zone zone = new Zone();
			zone.setZoneId(zoneNumber);
			zone.setBoxIds(zones.get(zoneNumber));
			zoneMongoRepository.insert(zone);
		}
		return zoneMongoRepository.count();
	}


}
