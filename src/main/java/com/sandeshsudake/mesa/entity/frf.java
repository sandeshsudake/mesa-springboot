package com.sandeshsudake.mesa.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Data
public class frf {

    @Id
    String frfID;
    String userName;
    String userMail;
    String userCollege;
    String featureRequestMessage;

}
