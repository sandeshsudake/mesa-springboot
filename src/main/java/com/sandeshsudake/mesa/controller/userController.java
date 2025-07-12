package com.sandeshsudake.mesa.controller;

import com.sandeshsudake.mesa.entity.event;
import com.sandeshsudake.mesa.entity.user;
import com.sandeshsudake.mesa.repository.eventRepo;
import com.sandeshsudake.mesa.repository.userRepo;
import com.sandeshsudake.mesa.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter; // Import for date formatting

@Controller
public class userController {

    @Autowired
    private eventRepo eventRepo;

    @Autowired
    private userRepo userRepo;

    @Autowired
    private EmailService emailService;

    @GetMapping("")
    public String homePage(Model model){
        List<event> eventList = eventRepo.findAll();
        model.addAttribute("eventList", eventList);
        return "index";
    }

    @PostMapping("/registerEvents/")
    public String registerEvents(user newUserDetails, @RequestParam("eventID") String eventId) {
        Optional<user> existingUserOptional = userRepo.findByUserMail(newUserDetails.getUserMail());

        user userToSave;
        boolean isNewRegistration = true;
        String registeredEventName = "Unknown Event";
        String eventDate = "N/A"; // New variable for event date
        String eventTime = "N/A"; // New variable for event time
        String eventLocation = "N/A"; // New variable for event location


        if (existingUserOptional.isPresent()) {
            userToSave = existingUserOptional.get();
            userToSave.setUserName(newUserDetails.getUserName());
            userToSave.setUserCollege(newUserDetails.getUserCollege());
            userToSave.setUTR(newUserDetails.getUTR());

        } else {
            userToSave = newUserDetails;
            if (userToSave.getRegisteredEvent() == null) {
                userToSave.setRegisteredEvent(new ArrayList<>());
            }
        }

        Optional<event> eventToRegisterOptional = eventRepo.findById(eventId);

        if (eventToRegisterOptional.isPresent()) {
            event eventToRegister = eventToRegisterOptional.get();
            registeredEventName = eventToRegister.getEventName();

            // --- START: Extract Event Details for Email ---
            if (eventToRegister.getDOE() != null) {
                eventDate = eventToRegister.getDOE().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (eventToRegister.getTOE() != null) {
                eventTime = eventToRegister.getTOE(); // Assuming TOE is already a formatted String or can be directly used
            }
            if (eventToRegister.getEventLoc() != null) {
                eventLocation = eventToRegister.getEventLoc();
            }
            // --- END: Extract Event Details for Email ---

            boolean alreadyRegistered = userToSave.getRegisteredEvent().stream()
                    .anyMatch(e -> e.getEventID().equals(eventToRegister.getEventID()));

            if (!alreadyRegistered) {
                userToSave.getRegisteredEvent().add(eventToRegister);
            } else {
                isNewRegistration = false;
            }
        } else {
            System.err.println("Error: Event with ID " + eventId + " not found for registration.");
            return "registrationError";
        }

        userRepo.save(userToSave);

        if (isNewRegistration) {
            String toEmail = newUserDetails.getUserMail();
            String subject = "MESA Event Registration Confirmation: " + registeredEventName;
            String body = "Dear " + newUserDetails.getUserName() + ",\n\n"
                    + "Thank you for registering for the MESA event: " + registeredEventName + ".\n\n"
                    + "Event Details:\n"
                    + "  Date: " + eventDate + "\n"
                    + "  Time: " + eventTime + "\n"
                    + "  Location: " + eventLocation + "\n\n"
                    + "Your UTR/Reference ID for this registration is: " + newUserDetails.getUTR() + ".\n\n"
                    + "We look forward to seeing you there!\n\n"
                    + "Best regards,\n"
                    + "The MESA Team";
            try {
                emailService.sendEmail(toEmail, subject, body);
                System.out.println("Confirmation email sent to: " + toEmail + " for event: " + registeredEventName);
            } catch (Exception e) {
                System.err.println("Failed to send confirmation email to " + toEmail + ": " + e.getMessage());
            }
        } else {
            System.out.println("User " + newUserDetails.getUserMail() + " already registered for " + registeredEventName + ". No new email sent.");
        }

        return "registrationSuccess";
    }

    @GetMapping("/eventImg/{id}")
    public ResponseEntity<byte[]> getEventImage(@PathVariable String id) {
        return eventRepo.findById(id)
                .map(event -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(event.getContentType()))
                        .body(event.getEventIMG()))
                .orElse(ResponseEntity.notFound().build());
    }
}
