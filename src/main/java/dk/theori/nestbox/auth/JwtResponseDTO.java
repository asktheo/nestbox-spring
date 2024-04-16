package dk.theori.nestbox.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Zeeshan Adil, <a href="https://www.linkedin.com/in/zeeshan-adil-a94b3867/">...</a>
 * <a href="https://medium.com/spring-boot/spring-boot-3-spring-security-6-jwt-authentication-authorization-98702d6313a5">...</a>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponseDTO {
    private String accessToken;

}
