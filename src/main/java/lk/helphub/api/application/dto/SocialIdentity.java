package lk.helphub.api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialIdentity implements Serializable {
    private String email;
    private String googleId;
    private String appleId;
    private String firstName;
    private String lastName;
    private String pictureUrl;
}
