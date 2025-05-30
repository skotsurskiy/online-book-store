package mate.academy.onlinebookstore.service.user;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.onlinebookstore.dto.user.UserResponseDto;
import mate.academy.onlinebookstore.exception.RegistrationException;
import mate.academy.onlinebookstore.mapper.UserMapper;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.model.User;
import mate.academy.onlinebookstore.repository.role.RoleRepository;
import mate.academy.onlinebookstore.repository.user.UserRepository;
import mate.academy.onlinebookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Email '%s' is already taken. Try another email"
                    .formatted(requestDto.getEmail()));
        }
        User user = userMapper.toUserEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(Set.of(roleRepository.findByRole(Role.RoleName.ROLE_USER)));
        UserResponseDto responseDto = userMapper.toResponseDto(userRepository.save(user));
        shoppingCartService.createShoppingCartForUser(user);
        return responseDto;
    }
}
