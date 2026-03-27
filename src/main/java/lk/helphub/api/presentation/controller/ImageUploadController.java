package lk.helphub.api.presentation.controller;

import lk.helphub.api.application.services.ImageUploadService;
import lk.helphub.api.domain.enums.ResponseStatusCode;
import lk.helphub.api.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "User profile management APIs")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile picture",
               description = "Accepts JPEG, PNG, or WebP images up to 10MB. The server resizes images to a maximum of 800x800 pixels and compresses at 75% JPEG quality before storing. Returns the public URL of the processed image.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile picture uploaded successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file type or size",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfilePicture(
            Principal principal,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String imageUrl = imageUploadService.uploadProfilePicture(principal.getName(), file);
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Profile picture uploaded successfully")
                .data(Map.of("url", imageUrl))
                .build());
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images",
               description = "Accepts multiple JPEG, PNG, or WebP images. Returns a list of public URLs.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Images uploaded successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file type or size")
    })
    @PreAuthorize("hasAuthority('profile_update')")
    public ResponseEntity<ApiResponse<java.util.List<String>>> uploadImages(
            Principal principal,
            @RequestParam("files") java.util.List<MultipartFile> files,
            @RequestParam(value = "type", defaultValue = "general") String type,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) throws IOException {
        java.util.List<String> urls = imageUploadService.uploadImages(principal.getName(), files, type, folder);
        return ResponseEntity.ok(ApiResponse.<java.util.List<String>>builder()
                .status(true)
                .statusCode(ResponseStatusCode.SUCCESS)
                .message("Images uploaded successfully")
                .data(urls)
                .build());
    }
}
