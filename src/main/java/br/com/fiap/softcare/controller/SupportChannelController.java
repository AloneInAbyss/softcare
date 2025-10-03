package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.model.SupportChannel;
import br.com.fiap.softcare.service.SupportChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Support Channel operations
 */
@Slf4j
@RestController
@RequestMapping("/support-channels")
@RequiredArgsConstructor
public class SupportChannelController {
    
    private final SupportChannelService supportChannelService;
    
    /**
     * Create a new support channel
     */
    @PostMapping
    public ResponseEntity<SupportChannel> createSupportChannel(@Valid @RequestBody SupportChannel supportChannel) {
        log.info("Creating support channel: {}", supportChannel.getName());
        
        SupportChannel createdChannel = supportChannelService.createSupportChannel(supportChannel);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }
    
    /**
     * Get support channel by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupportChannel> getSupportChannelById(@PathVariable String id) {
        log.info("Getting support channel by ID: {}", id);
        
        Optional<SupportChannel> channel = supportChannelService.findById(id);
        
        return channel.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all support channels
     */
    @GetMapping
    public ResponseEntity<List<SupportChannel>> getAllSupportChannels() {
        log.info("Getting all support channels");
        
        List<SupportChannel> channels = supportChannelService.findAllSupportChannels();
        
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Update support channel
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupportChannel> updateSupportChannel(@PathVariable String id,
                                                                @Valid @RequestBody SupportChannel supportChannel) {
        log.info("Updating support channel: {}", id);
        
        try {
            SupportChannel updatedChannel = supportChannelService.updateSupportChannel(id, supportChannel);
            return ResponseEntity.ok(updatedChannel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete support channel
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportChannel(@PathVariable String id) {
        log.info("Deleting support channel: {}", id);
        
        try {
            supportChannelService.deleteSupportChannel(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Search support channels by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<SupportChannel>> searchByName(@RequestParam String name) {
        log.info("Searching support channels by name: {}", name);
        
        List<SupportChannel> channels = supportChannelService.searchByName(name);
        
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Search support channels by description
     */
    @GetMapping("/search/description")
    public ResponseEntity<List<SupportChannel>> searchByDescription(@RequestParam String text) {
        log.info("Searching support channels by description: {}", text);
        
        List<SupportChannel> channels = supportChannelService.searchByDescription(text);
        
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Get channels with phone number
     */
    @GetMapping("/with-phone")
    public ResponseEntity<List<SupportChannel>> getChannelsWithPhone() {
        log.info("Getting support channels with phone number");
        
        List<SupportChannel> channels = supportChannelService.findChannelsWithPhone();
        
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Get channels with email
     */
    @GetMapping("/with-email")
    public ResponseEntity<List<SupportChannel>> getChannelsWithEmail() {
        log.info("Getting support channels with email");
        
        List<SupportChannel> channels = supportChannelService.findChannelsWithEmail();
        
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Get channels with website
     */
    @GetMapping("/with-website")
    public ResponseEntity<List<SupportChannel>> getChannelsWithWebsite() {
        log.info("Getting support channels with website");
        
        List<SupportChannel> channels = supportChannelService.findChannelsWithWebsite();
        
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Log access to support channel
     */
    @PostMapping("/{id}/access")
    public ResponseEntity<String> logChannelAccess(@PathVariable String id, @RequestParam String userId) {
        log.info("Logging access to support channel {} by user {}", id, userId);
        
        supportChannelService.logChannelAccess(id, userId);
        
        return ResponseEntity.ok("Access logged successfully");
    }
}