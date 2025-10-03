package br.com.fiap.softcare.repository;

import br.com.fiap.softcare.model.SupportChannel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for SupportChannel entity
 */
@Repository
public interface SupportChannelRepository extends MongoRepository<SupportChannel, String> {
    
    /**
     * Find support channels by name (case insensitive)
     */
    @Query("{'name': {$regex: ?0, $options: 'i'}}")
    List<SupportChannel> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find support channels by description containing text (case insensitive)
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    List<SupportChannel> findByDescriptionContainingIgnoreCase(String text);
    
    /**
     * Find support channels that have phone number
     */
    @Query("{'phoneNumber': {$exists: true, $ne: null}}")
    List<SupportChannel> findChannelsWithPhoneNumber();
    
    /**
     * Find support channels that have email
     */
    @Query("{'email': {$exists: true, $ne: null}}")
    List<SupportChannel> findChannelsWithEmail();
    
    /**
     * Find support channels that have website
     */
    @Query("{'website': {$exists: true, $ne: null}}")
    List<SupportChannel> findChannelsWithWebsite();
    
    /**
     * Find all support channels ordered by name
     */
    List<SupportChannel> findAllByOrderByNameAsc();
}