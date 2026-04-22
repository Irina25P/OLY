package com.example.imagemanagementmicroservice;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ImageControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private MinioClient minioClient;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private ConnectionFactory connectionFactory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // ==================== UPLOAD IMAGE TESTS ====================

    @Test
    void uploadImage_asClient_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image uploaded successfully"))
                .andExpect(jsonPath("$.fileName").value("test-image.png"))
                .andExpect(jsonPath("$.objectName").isNotEmpty());

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_asSeller_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "product-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake jpeg content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "seller1")
                        .header("Role-Header", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image uploaded successfully"))
                .andExpect(jsonPath("$.fileName").value("product-image.jpg"));

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_noAuthenticationHeaders_shouldReturn403() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file))
                .andExpect(status().isForbidden());

        verify(minioClient, never()).putObject(any());
    }

    @Test
    void uploadImage_asAdmin_shouldReturn403() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "admin1")
                        .header("Role-Header", "ADMIN"))
                .andExpect(status().isForbidden());

        verify(minioClient, never()).putObject(any());
    }

    @Test
    void uploadImage_emptyFile_shouldReturn400() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("File is empty"));

        verify(minioClient, never()).putObject(any());
    }

    @Test
    void uploadImage_nonImageFile_shouldReturn400() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "fake pdf content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("File is empty"));

        verify(minioClient, never()).putObject(any());
    }

    @Test
    void uploadImage_jpgImage_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "landscape.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake jpeg content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.fileName").value("landscape.jpg"));

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_gifImage_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "animation.gif",
                "image/gif",
                "fake gif content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "seller1")
                        .header("Role-Header", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.fileName").value("animation.gif"));

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_webpImage_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "modern.webp",
                "image/webp",
                "fake webp content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).putObject(any());
    }

    // ==================== DELETE IMAGE TESTS ====================

    @Test
    void deleteImage_asClient_shouldReturn200() throws Exception {
        // Arrange
        String objectName = UUID.randomUUID() + ".png";
        willAnswer(invocation -> null).given(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image deleted successfully"));

        verify(minioClient).removeObject(any());
    }

    @Test
    void deleteImage_asSeller_shouldReturn200() throws Exception {
        // Arrange
        String objectName = UUID.randomUUID() + ".jpg";
        willAnswer(invocation -> null).given(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName)
                        .header("Username-Header", "seller1")
                        .header("Role-Header", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image deleted successfully"));

        verify(minioClient).removeObject(any());
    }

    @Test
    void deleteImage_noAuthenticationHeaders_shouldReturn403() throws Exception {
        // Arrange
        String objectName = UUID.randomUUID() + ".png";

        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName))
                .andExpect(status().isForbidden());

        verify(minioClient, never()).removeObject(any());
    }

    @Test
    void deleteImage_asAdmin_shouldReturn403() throws Exception {
        // Arrange
        String objectName = UUID.randomUUID() + ".png";

        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName)
                        .header("Username-Header", "admin1")
                        .header("Role-Header", "ADMIN"))
                .andExpect(status().isForbidden());

        verify(minioClient, never()).removeObject(any());
    }

    @Test
    void deleteImage_emptyObjectName_shouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", "")
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString());

        verify(minioClient, never()).removeObject(any());
    }

    @Test
    void deleteImage_nullObjectName_shouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isBadRequest());

        verify(minioClient, never()).removeObject(any());
    }

    @Test
    void deleteImage_multipleValidDeletions_shouldReturn200() throws Exception {
        // Arrange
        String objectName1 = UUID.randomUUID() + ".png";
        String objectName2 = UUID.randomUUID() + ".jpg";
        willAnswer(invocation -> null).given(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert - First deletion
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName1)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Act & Assert - Second deletion
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName2)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient, times(2)).removeObject(any());
    }

    // ==================== EDGE CASES ====================

    @Test
    void uploadImage_largeFileName_shouldReturn200() throws Exception {
        // Arrange
        String longFileName = "very_long_filename_" + "a".repeat(100) + ".png";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                longFileName,
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_specialCharactersInFileName_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image_#@!$%.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_fileWithoutExtension_shouldReturn200() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "imagewithoutext",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "seller1")
                        .header("Role-Header", "SELLER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).putObject(any());
    }

    @Test
    void uploadImage_incorrectContentTypeButImageFile_shouldReturn400() throws Exception {
        // Arrange - File claims to be PDF but is actually an image
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "fake.pdf",
                MediaType.APPLICATION_PDF_VALUE, // Wrong content type
                "fake image content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(minioClient, never()).putObject(any());
    }

    @Test
    void deleteImage_objectNameWithSpecialCharacters_shouldReturn200() throws Exception {
        // Arrange
        String objectName = "image#2024_@special!.png";
        willAnswer(invocation -> null).given(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).removeObject(any());
    }

    @Test
    void uploadImage_differentImageFormats_shouldAllReturn200() throws Exception {
        // Test with SVG
        MockMultipartFile svgFile = new MockMultipartFile(
                "file",
                "vector.svg",
                "image/svg+xml",
                "<svg></svg>".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        mockMvc.perform(multipart("/v1/images")
                        .file(svgFile)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).putObject(any());
    }

    // ==================== AUTHORIZATION ROLE TESTS ====================

    @Test
    void uploadImage_incorrectRoleFormat_shouldStillRequireHeader() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "content".getBytes()
        );

        // Act & Assert - Missing proper headers
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "user1"))
                .andExpect(status().isForbidden());

        verify(minioClient, never()).putObject(any());
    }

    @Test
    void deleteImage_missingRoleHeader_shouldReturn403() throws Exception {
        // Arrange
        String objectName = UUID.randomUUID() + ".png";

        // Act & Assert
        mockMvc.perform(delete("/v1/images")
                        .param("objectName", objectName)
                        .header("Username-Header", "client1"))
                .andExpect(status().isForbidden());

        verify(minioClient, never()).removeObject(any());
    }

    @Test
    void uploadImage_withAdditionalHeaders_shouldStillWork() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake image content".getBytes()
        );

        willAnswer(invocation -> null).given(minioClient).putObject(any(PutObjectArgs.class));

        // Act & Assert
        mockMvc.perform(multipart("/v1/images")
                        .file(file)
                        .header("Username-Header", "client1")
                        .header("Role-Header", "CLIENT")
                        .header("UserId-Header", "user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(minioClient).putObject(any());
    }
}


















