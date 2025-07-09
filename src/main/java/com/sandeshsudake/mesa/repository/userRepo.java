package com.sandeshsudake.mesa.repository;

import com.sandeshsudake.mesa.entity.user;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Make sure this is imported
import java.util.Optional;

@Repository
public interface userRepo extends MongoRepository<user, String> {
    Optional<user> findByUserMail(String userMail);

    // This is the method we need for event-specific registrations
    List<user> findByRegisteredEvent_EventID(String eventId);
}