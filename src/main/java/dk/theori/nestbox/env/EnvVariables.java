package dk.theori.nestbox.env;

import java.util.HashMap;
import java.util.Map;

public class EnvVariables {

    //environment variables which can and should be declared
    public static final String SECRET = "JWT_SECRET";
    public static final String JSON_DIR = "JSON_DIR";

    private Map<String, String> envVariables = new HashMap<>();

    public void addKey(String key, String value) {
        this.envVariables.put(key,value);
    }

    public String getEnvVariable(String key){
        return this.envVariables.get(key);
    }

}