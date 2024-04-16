package dk.theori.nestbox;

import dk.theori.nestbox.entities.UserInfo;
import dk.theori.nestbox.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserMongoRepository userMongoRepository;

    @Autowired
    private ApplicationContext context;

    @PostMapping("save")
    public String saveUser(@RequestBody UserInfo user){
        BCryptPasswordEncoder bCryptPasswordEncoder = context.getBean(BCryptPasswordEncoder.class);
        user.setPassword(bCryptPasswordEncoder
                .encode(user.getPassword()));
        return userMongoRepository.save(user).getUsername();
    }




}
