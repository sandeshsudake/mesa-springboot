package com.sandeshsudake.mesa.entity;

import lombok.Data;
import org.bson.types.ObjectId; // This is good, indicates you're thinking about ObjectId
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList; // Added for initialization

@Document
@Data
public class user {

    @Id
    String userID; // MongoDB will populate this if it's null when saving a new document
    String userName;
    String userMail;
    String userCollege;
    String UTR;
    List<event> registeredEvent; // This field exists but isn't being used in the POST method
    String role="user";

    // Constructor to ensure registeredEvent list is initialized
    public user() {
        this.registeredEvent = new ArrayList<>();
    }
}