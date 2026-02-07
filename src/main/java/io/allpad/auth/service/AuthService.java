package io.allpad.auth.service;

import io.allpad.auth.dto.AuthDTO;
import io.allpad.auth.dto.TokenDTO;
import io.allpad.auth.dto.UserDTO;

public interface AuthService {
    AuthDTO signUp(UserDTO userDTO);

    AuthDTO login(UserDTO userDTO);

    void logout(TokenDTO tokenDTO);

    AuthDTO refreshToken(TokenDTO tokenDTO);

    void recoverPassword(UserDTO userDTO);

    void updatePassword(TokenDTO tokenDTO);
}
