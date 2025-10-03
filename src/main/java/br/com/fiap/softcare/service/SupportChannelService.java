package br.com.fiap.softcare.service;

import br.com.fiap.softcare.model.SupportChannel;
import br.com.fiap.softcare.repository.SupportChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Support Channel operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupportChannelService {
    
    private final SupportChannelRepository supportChannelRepository;
    private final AuditLogService auditLogService;
    
    /**
     * Create a new support channel
     */
    public SupportChannel createSupportChannel(SupportChannel supportChannel) {
        log.info("Creating support channel: {}", supportChannel.getName());
        
        SupportChannel savedChannel = supportChannelRepository.save(supportChannel);
        
        log.info("Support channel created with ID: {}", savedChannel.getId());
        return savedChannel;
    }
    
    /**
     * Find support channel by ID
     */
    public Optional<SupportChannel> findById(String id) {
        return supportChannelRepository.findById(id);
    }
    
    /**
     * Find all support channels
     */
    public List<SupportChannel> findAllSupportChannels() {
        return supportChannelRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Update support channel
     */
    public SupportChannel updateSupportChannel(String id, SupportChannel updatedChannel) {
        log.info("Updating support channel: {}", id);
        
        SupportChannel existingChannel = supportChannelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Canal de suporte não encontrado: " + id));
        
        // Update fields
        existingChannel.setName(updatedChannel.getName());
        existingChannel.setDescription(updatedChannel.getDescription());
        existingChannel.setPhoneNumber(updatedChannel.getPhoneNumber());
        existingChannel.setEmail(updatedChannel.getEmail());
        existingChannel.setWebsite(updatedChannel.getWebsite());
        
        SupportChannel savedChannel = supportChannelRepository.save(existingChannel);
        
        log.info("Support channel updated: {}", savedChannel.getId());
        return savedChannel;
    }
    
    /**
     * Delete support channel
     */
    public void deleteSupportChannel(String id) {
        log.info("Deleting support channel: {}", id);
        
        supportChannelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Canal de suporte não encontrado: " + id));
        
        supportChannelRepository.deleteById(id);
        
        log.info("Support channel deleted: {}", id);
    }
    
    /**
     * Search support channels by name
     */
    public List<SupportChannel> searchByName(String name) {
        return supportChannelRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Search support channels by description
     */
    public List<SupportChannel> searchByDescription(String text) {
        return supportChannelRepository.findByDescriptionContainingIgnoreCase(text);
    }
    
    /**
     * Find channels with phone number
     */
    public List<SupportChannel> findChannelsWithPhone() {
        return supportChannelRepository.findChannelsWithPhoneNumber();
    }
    
    /**
     * Find channels with email
     */
    public List<SupportChannel> findChannelsWithEmail() {
        return supportChannelRepository.findChannelsWithEmail();
    }
    
    /**
     * Find channels with website
     */
    public List<SupportChannel> findChannelsWithWebsite() {
        return supportChannelRepository.findChannelsWithWebsite();
    }
    
    /**
     * Log access to support channel
     */
    public void logChannelAccess(String channelId, String userId) {
        Optional<SupportChannel> channel = supportChannelRepository.findById(channelId);
        
        if (channel.isPresent()) {
            auditLogService.logSupportChannelAccessed(userId, channelId);
            log.info("User {} accessed support channel: {}", userId, channel.get().getName());
        } else {
            log.warn("Attempt to access non-existent support channel: {}", channelId);
        }
    }
}