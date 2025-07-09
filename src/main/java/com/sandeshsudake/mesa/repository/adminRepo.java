package com.sandeshsudake.mesa.repository;

import com.sandeshsudake.mesa.entity.admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface adminRepo extends MongoRepository<admin,String> {

    admin findByadminUserName(String username);

}
