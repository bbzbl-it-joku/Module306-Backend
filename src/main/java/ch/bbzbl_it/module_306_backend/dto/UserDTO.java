package ch.bbzbl_it.module_306_backend.dto;

import ch.bbzbl_it.module_306_backend.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    private String name;
    private String vorname;
    private String role;

    public static UserDTO toDTO(User user) {
        if (user == null)
            return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setVorname(user.getVorname());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public static User toUser(UserDTO userDTO) {
        if (userDTO == null)
            return null;

        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setVorname(userDTO.getVorname());
        user.setRole(userDTO.getRole());

        return user;
    }
}
