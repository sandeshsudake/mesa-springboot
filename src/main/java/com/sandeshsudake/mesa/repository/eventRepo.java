package com.sandeshsudake.mesa.repository;

import com.sandeshsudake.mesa.entity.event;

import com.sandeshsudake.mesa.entity.user;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface eventRepo extends MongoRepository<event,String> {
    Optional<event> findById(String eventId);

}
