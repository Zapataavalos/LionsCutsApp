package com.example.Usuario.Dto;

public record PasswordChangeRequest(
        String currentPassword,
        String newPassword
) {}