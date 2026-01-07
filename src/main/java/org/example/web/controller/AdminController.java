package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Device;
import org.example.web.data.entity.UserAccount;
import org.example.web.data.pojo.UserAccountDTO;
import org.example.web.data.response.DeviceResponse;
import org.example.web.mapper.DeviceMapper;
import org.example.web.security.CustomUserDetails;
import org.example.web.service.DeviceService;
import org.example.web.service.UserAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserAccountService userAccountService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserAccountDTO>> filter(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userAccountService.filter(username, fullname, email, role, pageable));
    }

    @GetMapping("/user/{Userid}")
    public ResponseEntity<UserAccount> getUser(@PathVariable Long Userid){
        return ResponseEntity.ok(userAccountService.findById(Userid));
    }

    @PutMapping("/user/{Userid}")
    public ResponseEntity<UserAccount> updateUser(@RequestBody UserAccount userAccount, @PathVariable Long Userid) {
        return ResponseEntity.ok(userAccountService.update(Userid, userAccount));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userAccountService.deleteById(userId);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
