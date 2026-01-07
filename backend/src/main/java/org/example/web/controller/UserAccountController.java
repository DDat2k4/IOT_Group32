package org.example.web.controller;

import lombok.AllArgsConstructor;
import org.example.web.data.entity.UserAccount;
import org.example.web.data.pojo.UserAccountDTO;
import org.example.web.data.request.UserInfo;
import org.example.web.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/")
@AllArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping("/me")
    public ResponseEntity<UserAccountDTO> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                userAccountService.findByUsername(userDetails.getUsername())
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserAccountDTO> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserInfo request
    ) {
        UserAccountDTO currentUser =
                userAccountService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(
                userAccountService.updateInfo(currentUser.getId(), request)
        );
    }
}
