package com.ms.clab.security;

import com.ms.clab.entity.User;
import com.ms.clab.repository.UserRepository;
import com.ms.clab.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AESUtil aesUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디의 사용자를 찾을 수 없습니다"));

        try {
            // 데이터베이스에 저장된 암호화된 비밀번호 복호화
            String decryptedPassword = aesUtil.decrypt(user.getUserPw());

            // 사용자가 입력한 비밀번호와 복호화된 비밀번호를 비교
            if (!decryptedPassword.equals(password)) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("비밀번호를 복호화할 수 없습니다.");
        }

        return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}