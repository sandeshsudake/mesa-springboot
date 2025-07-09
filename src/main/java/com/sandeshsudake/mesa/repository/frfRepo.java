package com.sandeshsudake.mesa.repository;

import com.sandeshsudake.mesa.entity.frf;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface frfRepo extends MongoRepository<frf,String> {
}
