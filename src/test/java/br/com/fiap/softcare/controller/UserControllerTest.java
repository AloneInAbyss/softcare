package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.dto.CreateUserRequest;
import br.com.fiap.softcare.enums.UserRole;
import br.com.fiap.softcare.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.database=softcare_test"
})
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.EMPLOYEE);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser
    void shouldGetAllUsers() throws Exception {
        // Given - Create a user first
        CreateUserRequest request = new CreateUserRequest();
        request.setName("List Test User");
        request.setEmail("listtest@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.MANAGER);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("List Test User")))
                .andExpect(jsonPath("$[0].email", is("listtest@example.com")));
    }

    @Test
    @WithMockUser
    void shouldFailToCreateUserWithInvalidData() throws Exception {
        // Given - Request without required fields
        CreateUserRequest request = new CreateUserRequest();
        request.setName(""); // Invalid name
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("123"); // Too short password

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldGetUserById() throws Exception {
        // Given - Create a user first
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Test User By ID");
        request.setEmail("testbyid@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.SYSTEM_ADMIN);

        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from response
        String userId = objectMapper.readTree(response).get("id").asText();

        // When & Then
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Test User By ID")))
                .andExpect(jsonPath("$.email", is("testbyid@example.com")))
                .andExpect(jsonPath("$.role", is("SYSTEM_ADMIN")));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        // Given - Non-existent user ID
        String nonExistentId = "507f1f77bcf86cd799439011";

        // When & Then
        mockMvc.perform(get("/api/v1/users/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given - Create a user first
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setName("Original User");
        createRequest.setEmail("original@example.com");
        createRequest.setPassword("password123");
        createRequest.setRole(UserRole.EMPLOYEE);

        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = objectMapper.readTree(response).get("id").asText();

        // Prepare update request
        CreateUserRequest updateRequest = new CreateUserRequest();
        updateRequest.setName("Updated User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPassword("newpassword123");
        updateRequest.setRole(UserRole.MANAGER);

        // When & Then
        mockMvc.perform(put("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
    }

    @Test
    @WithMockUser
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given - Create a user first
        CreateUserRequest request = new CreateUserRequest();
        request.setName("User To Delete");
        request.setEmail("todelete@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.EMPLOYEE);

        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = objectMapper.readTree(response).get("id").asText();

        // When & Then - Delete the user
        mockMvc.perform(delete("/api/v1/users/" + userId))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldGetAllUsersWhenFilterNotImplemented() throws Exception {
        // Given - Create users with different roles
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Admin User");
        adminRequest.setEmail("admin@example.com");
        adminRequest.setPassword("password123");
        adminRequest.setRole(UserRole.SYSTEM_ADMIN);

        CreateUserRequest employeeRequest = new CreateUserRequest();
        employeeRequest.setName("Employee User");
        employeeRequest.setEmail("employee@example.com");
        employeeRequest.setPassword("password123");
        employeeRequest.setRole(UserRole.EMPLOYEE);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequest)));

        // When & Then - Since filter by role is not implemented, should return all users
        mockMvc.perform(get("/api/v1/users?role=SYSTEM_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        // Given - No users in database (cleaned in setUp)

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void shouldValidateRequiredFields() throws Exception {
        // Given - Request with missing required fields
        CreateUserRequest request = new CreateUserRequest();
        // All fields are null/empty

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser 
    void shouldValidateEmailFormat() throws Exception {
        // Given - Request with invalid email format
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Valid Name");
        request.setEmail("not-an-email"); // Invalid format
        request.setPassword("password123");
        request.setRole(UserRole.EMPLOYEE);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldValidatePasswordLength() throws Exception {
        // Given - Request with too short password
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Valid Name");
        request.setEmail("valid@example.com");
        request.setPassword("123"); // Too short
        request.setRole(UserRole.EMPLOYEE);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}