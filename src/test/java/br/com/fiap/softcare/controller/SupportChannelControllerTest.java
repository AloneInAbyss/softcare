package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.model.SupportChannel;
import br.com.fiap.softcare.repository.SupportChannelRepository;
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

/**
 * Testes completos para SupportChannelController com segurança mock
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.database=softcare_test"
})
class SupportChannelControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SupportChannelRepository supportChannelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        supportChannelRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldCreateSupportChannelSuccessfully() throws Exception {
        // Given
        SupportChannel supportChannel = SupportChannel.builder()
                .name("Centro de Valorização da Vida")
                .description("Apoio emocional e prevenção do suicídio")
                .phoneNumber("188")
                .email("contato@cvv.org.br")
                .website("https://www.cvv.org.br")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/support-channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supportChannel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Centro de Valorização da Vida")))
                .andExpect(jsonPath("$.description", is("Apoio emocional e prevenção do suicídio")))
                .andExpect(jsonPath("$.phoneNumber", is("188")))
                .andExpect(jsonPath("$.email", is("contato@cvv.org.br")))
                .andExpect(jsonPath("$.website", is("https://www.cvv.org.br")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser
    void shouldGetAllSupportChannels() throws Exception {
        // Given - Create support channels first
        SupportChannel channel1 = SupportChannel.builder()
                .name("CVV")
                .description("Centro de Valorização da Vida")
                .phoneNumber("188")
                .build();

        SupportChannel channel2 = SupportChannel.builder()
                .name("CAPS")
                .description("Centro de Atenção Psicossocial")
                .phoneNumber("11999999999")
                .email("caps@saude.gov.br")
                .build();

        supportChannelRepository.save(channel1);
        supportChannelRepository.save(channel2);

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", anyOf(is("CVV"), is("CAPS"))))
                .andExpect(jsonPath("$[1].name", anyOf(is("CVV"), is("CAPS"))));
    }

    @Test
    @WithMockUser
    void shouldGetSupportChannelById() throws Exception {
        // Given - Create a support channel first
        SupportChannel supportChannel = SupportChannel.builder()
                .name("Test Channel")
                .description("Test Description")
                .phoneNumber("11999999999")
                .build();

        SupportChannel savedChannel = supportChannelRepository.save(supportChannel);

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels/" + savedChannel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedChannel.getId())))
                .andExpect(jsonPath("$.name", is("Test Channel")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.phoneNumber", is("11999999999")));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForNonExistentSupportChannel() throws Exception {
        // Given - Non-existent channel ID
        String nonExistentId = "507f1f77bcf86cd799439011";

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateSupportChannelSuccessfully() throws Exception {
        // Given - Create a support channel first
        SupportChannel originalChannel = SupportChannel.builder()
                .name("Original Channel")
                .description("Original Description")
                .phoneNumber("11111111111")
                .build();

        SupportChannel savedChannel = supportChannelRepository.save(originalChannel);

        // Prepare update data
        SupportChannel updateChannel = SupportChannel.builder()
                .name("Updated Channel")
                .description("Updated Description")
                .phoneNumber("22222222222")
                .email("updated@example.com")
                .website("https://updated.com")
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/support-channels/" + savedChannel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateChannel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedChannel.getId())))
                .andExpect(jsonPath("$.name", is("Updated Channel")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.phoneNumber", is("22222222222")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.website", is("https://updated.com")));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenUpdatingNonExistentChannel() throws Exception {
        // Given - Non-existent channel ID
        String nonExistentId = "507f1f77bcf86cd799439011";
        
        SupportChannel updateChannel = SupportChannel.builder()
                .name("Updated Channel")
                .description("Updated Description")
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/support-channels/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateChannel)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldDeleteSupportChannelSuccessfully() throws Exception {
        // Given - Create a support channel first
        SupportChannel supportChannel = SupportChannel.builder()
                .name("Channel To Delete")
                .description("This channel will be deleted")
                .build();

        SupportChannel savedChannel = supportChannelRepository.save(supportChannel);

        // When & Then - Delete the channel
        mockMvc.perform(delete("/api/v1/support-channels/" + savedChannel.getId()))
                .andExpect(status().isNoContent());

        // Verify channel is deleted
        mockMvc.perform(get("/api/v1/support-channels/" + savedChannel.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenDeletingNonExistentChannel() throws Exception {
        // Given - Non-existent channel ID
        String nonExistentId = "507f1f77bcf86cd799439011";

        // When & Then
        mockMvc.perform(delete("/api/v1/support-channels/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldSearchChannelsByName() throws Exception {
        // Given - Create support channels with different names
        SupportChannel channel1 = SupportChannel.builder()
                .name("CVV Centro")
                .description("Centro de Valorização da Vida")
                .build();

        SupportChannel channel2 = SupportChannel.builder()
                .name("CAPS Centro")
                .description("Centro de Atenção Psicossocial")
                .build();

        SupportChannel channel3 = SupportChannel.builder()
                .name("Hospital Público")
                .description("Hospital para atendimento público")
                .build();

        supportChannelRepository.save(channel1);
        supportChannelRepository.save(channel2);
        supportChannelRepository.save(channel3);

        // When & Then - Search by name containing "Centro"
        mockMvc.perform(get("/api/v1/support-channels/search/name")
                .param("name", "Centro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void shouldSearchChannelsByDescription() throws Exception {
        // Given - Create support channels with different descriptions
        SupportChannel channel1 = SupportChannel.builder()
                .name("CVV")
                .description("Apoio emocional e prevenção do suicídio")
                .build();

        SupportChannel channel2 = SupportChannel.builder()
                .name("CAPS")
                .description("Atenção psicossocial e apoio mental")
                .build();

        SupportChannel channel3 = SupportChannel.builder()
                .name("Clínica")
                .description("Atendimento médico geral")
                .build();

        supportChannelRepository.save(channel1);
        supportChannelRepository.save(channel2);
        supportChannelRepository.save(channel3);

        // When & Then - Search by description containing "apoio"
        mockMvc.perform(get("/api/v1/support-channels/search/description")
                .param("text", "apoio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void shouldGetChannelsWithPhone() throws Exception {
        // Given - Create channels with and without phone
        SupportChannel channelWithPhone = SupportChannel.builder()
                .name("CVV")
                .description("Centro de Valorização da Vida")
                .phoneNumber("188")
                .build();

        SupportChannel channelWithoutPhone = SupportChannel.builder()
                .name("Online Support")
                .description("Apoio apenas online")
                .email("support@online.com")
                .build();

        supportChannelRepository.save(channelWithPhone);
        supportChannelRepository.save(channelWithoutPhone);

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels/with-phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("CVV")))
                .andExpect(jsonPath("$[0].phoneNumber", is("188")));
    }

    @Test
    @WithMockUser
    void shouldGetChannelsWithEmail() throws Exception {
        // Given - Create channels with and without email
        SupportChannel channelWithEmail = SupportChannel.builder()
                .name("Email Support")
                .description("Apoio por email")
                .email("support@email.com")
                .build();

        SupportChannel channelWithoutEmail = SupportChannel.builder()
                .name("Phone Support")
                .description("Apoio apenas por telefone")
                .phoneNumber("11999999999")
                .build();

        supportChannelRepository.save(channelWithEmail);
        supportChannelRepository.save(channelWithoutEmail);

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels/with-email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Email Support")))
                .andExpect(jsonPath("$[0].email", is("support@email.com")));
    }

    @Test
    @WithMockUser
    void shouldGetChannelsWithWebsite() throws Exception {
        // Given - Create channels with and without website
        SupportChannel channelWithWebsite = SupportChannel.builder()
                .name("Web Support")
                .description("Apoio com website")
                .website("https://support.com")
                .build();

        SupportChannel channelWithoutWebsite = SupportChannel.builder()
                .name("Phone Support")
                .description("Apoio apenas por telefone")
                .phoneNumber("11999999999")
                .build();

        supportChannelRepository.save(channelWithWebsite);
        supportChannelRepository.save(channelWithoutWebsite);

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels/with-website"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Web Support")))
                .andExpect(jsonPath("$[0].website", is("https://support.com")));
    }

    @Test
    @WithMockUser
    void shouldLogChannelAccess() throws Exception {
        // Given - Create a support channel first
        SupportChannel supportChannel = SupportChannel.builder()
                .name("Test Channel")
                .description("Channel for access logging")
                .build();

        SupportChannel savedChannel = supportChannelRepository.save(supportChannel);
        String userId = "user123";

        // When & Then
        mockMvc.perform(post("/api/v1/support-channels/" + savedChannel.getId() + "/access")
                .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Access logged successfully"));
    }

    @Test
    @WithMockUser
    void shouldFailToCreateChannelWithInvalidData() throws Exception {
        // Given - Channel with invalid data
        SupportChannel invalidChannel = SupportChannel.builder()
                .name("") // Invalid name
                .description("") // Invalid description
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/support-channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidChannel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldValidateRequiredFields() throws Exception {
        // Given - Channel with missing required fields
        SupportChannel invalidChannel = SupportChannel.builder()
                .phoneNumber("11999999999") // Only phone, missing name and description
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/support-channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidChannel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldValidateNameLength() throws Exception {
        // Given - Channel with name too short
        SupportChannel invalidChannel = SupportChannel.builder()
                .name("A") // Too short
                .description("Valid description")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/support-channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidChannel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldValidateDescriptionLength() throws Exception {
        // Given - Channel with description too long
        String longDescription = "A".repeat(501); // Exceeds 500 characters
        
        SupportChannel invalidChannel = SupportChannel.builder()
                .name("Valid Name")
                .description(longDescription)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/support-channels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidChannel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListWhenNoChannelsExist() throws Exception {
        // Given - No channels in database (cleaned in setUp)

        // When & Then
        mockMvc.perform(get("/api/v1/support-channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListWhenSearchFindsNoResults() throws Exception {
        // Given - Create a channel that won't match search
        SupportChannel channel = SupportChannel.builder()
                .name("CVV")
                .description("Centro de Valorização da Vida")
                .build();

        supportChannelRepository.save(channel);

        // When & Then - Search for something that doesn't exist
        mockMvc.perform(get("/api/v1/support-channels/search/name")
                .param("name", "NonExistentChannel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListWhenNoChannelsHaveSpecificContactMethod() throws Exception {
        // Given - Create channel without phone
        SupportChannel channel = SupportChannel.builder()
                .name("Email Only")
                .description("Only has email contact")
                .email("contact@example.com")
                .build();

        supportChannelRepository.save(channel);

        // When & Then - Search for channels with phone
        mockMvc.perform(get("/api/v1/support-channels/with-phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}