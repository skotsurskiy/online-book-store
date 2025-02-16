package mate.academy.onlinebookstore.security;

import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.user.UserLoginRequestDto;
import mate.academy.onlinebookstore.dto.user.UserLoginResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
                                requestDto.getPassword())
        );

        return new UserLoginResponseDto(jwtUtil.generateToken(authenticate.getName()));
    }
}
