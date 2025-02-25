package mate.academy.onlinebookstore.mapper;

import mate.academy.onlinebookstore.config.MapperConfig;
import mate.academy.onlinebookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.onlinebookstore.dto.user.UserResponseDto;
import mate.academy.onlinebookstore.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toResponseDto(User user);

    User toUserEntity(UserRegistrationRequestDto requestDto);

    @Named("getUserById")
    default User getUserById(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
