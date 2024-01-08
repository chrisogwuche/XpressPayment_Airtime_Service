package com.xpresPayment.Airtimeservice.service.serviceImpl;

import com.xpresPayment.Airtimeservice.dto.response.UserDto;
import com.xpresPayment.Airtimeservice.model.User;
import com.xpresPayment.Airtimeservice.repository.UserRepository;
import com.xpresPayment.Airtimeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserUtils userUtils;

    @Override
    public ResponseEntity<UserDto> getCurrentUser() {
        User user = userUtils.currentUser();
        return ResponseEntity.ok(mapToUserDto(user));
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().name());
        userDto.setFull_name(user.getFullName());
        userDto.setReg_status(user.getRegStatus().name());
        userDto.setPhone_number(user.getPhoneNumber());
        return userDto;
    }
}
