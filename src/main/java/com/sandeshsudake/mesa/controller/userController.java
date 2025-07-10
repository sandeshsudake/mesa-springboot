package com.sandeshsudake.mesa.controller;

import com.sandeshsudake.mesa.entity.event; // Make sure 'event' entity is correctly imported
import com.sandeshsudake.mesa.entity.user;
import com.sandeshsudake.mesa.repository.eventRepo;
import com.sandeshsudake.mesa.repository.userRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // Import @RequestParam for eventID

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Import Optional

@Controller
public class userController {

    @Autowired
    private eventRepo eventRepo;

    @Autowired
    private userRepo userRepo;

    @GetMapping("")
    public String homePage(Model model){
        List<event> eventList = eventRepo.findAll();
        model.addAttribute("eventList", eventList);
        return "index";
    }

    @PostMapping("/registerEvents/")
    public String registerEvents(user newUserDetails, @RequestParam("eventID") String eventId) {
        // 1. Try to find an existing user by email
        Optional<user> existingUserOptional = userRepo.findByUserMail(newUserDetails.getUserMail());

        user userToSave;
        if (existingUserOptional.isPresent()) {
            // User exists, update their details and registered events
            userToSave = existingUserOptional.get();
            // Update other details that might have changed (e.g., if user changed college name)
            userToSave.setUserName(newUserDetails.getUserName());
            userToSave.setUserCollege(newUserDetails.getUserCollege());
            userToSave.setUTR(newUserDetails.getUTR()); // Update UTR for this specific registration

        } else {
            // New user, create a new user document
            userToSave = newUserDetails;
            // Ensure the registeredEvent list is initialized (Lombok @Data might do this, but explicit is safer)
            if (userToSave.getRegisteredEvent() == null) {
                userToSave.setRegisteredEvent(new ArrayList<>());
            }
        }

        // 2. Fetch the event object based on the eventID from the form
        Optional<event> eventToRegisterOptional = eventRepo.findById(eventId);

        if (eventToRegisterOptional.isPresent()) {
            event eventToRegister = eventToRegisterOptional.get();

            // 3. Add the event to the user's registeredEvent list if not already present
            // This is a simple check; for production, you might want more robust duplicate checks
            boolean alreadyRegistered = userToSave.getRegisteredEvent().stream()
                    .anyMatch(e -> e.getEventID().equals(eventToRegister.getEventID()));

            if (!alreadyRegistered) {
                userToSave.getRegisteredEvent().add(eventToRegister);
            }
            // If already registered, you might show a different message or do nothing.
            // For now, we'll proceed with saving the user anyway, updating other details.
        } else {
            // Handle case where event is not found (e.g., log error, redirect to error page)
            System.err.println("Error: Event with ID " + eventId + " not found for registration.");
            // You might want to return an error view or redirect to a different page here
            return "registrationError"; // Create a registrationError.html page
        }

        // 4. Save the updated or new user document
        userRepo.save(userToSave);

        return "registrationSuccess";
    }

    // New endpoint to serve images
    @GetMapping("/eventImg/{id}")
    public ResponseEntity<byte[]> getEventImage(@PathVariable String id) {
        return eventRepo.findById(id) // Retrieve event by ID
                .map(event -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(event.getContentType())) // Set content type
                        .body(event.getEventIMG())) // Return image bytes
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }
}