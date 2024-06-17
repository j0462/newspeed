package com.example.newspeed.controller;

import com.example.newspeed.dto.ProfileRequestDto;
import com.example.newspeed.dto.ProfileResponseDto;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.ProfileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable(name = "id") long id) {
        return ResponseEntity.ok().body(profileService.getProfile(id));
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDto> update(@RequestBody ProfileRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl authentication) {
        return ResponseEntity.ok().body(profileService.update(authentication, requestDto));
    }

    @PutMapping("/password")
    public String updatePassword(@AuthenticationPrincipal UserDetailsImpl authentication, @Valid @RequestBody ProfileRequestDto requestDto) {
        profileService.updatePassword(authentication, requestDto);
        return "비밀번호가 변경되었습니다.";
    }
}