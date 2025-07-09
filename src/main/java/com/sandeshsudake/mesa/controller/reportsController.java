package com.sandeshsudake.mesa.controller;
import com.sandeshsudake.mesa.entity.event;
import com.sandeshsudake.mesa.entity.user;
import com.sandeshsudake.mesa.repository.eventRepo;
import com.sandeshsudake.mesa.repository.userRepo;

import com.sandeshsudake.mesa.entity.event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class reportsController {


    @Autowired
    private eventRepo eventRepo;

    @Autowired
    private userRepo userRepo;

    // ... (existing adminDashboard, addEvent, getEventImage methods) ...

    // NEW: Endpoint to display registered users for a specific event on reports.html
    @GetMapping("/admin/reports/{eventId}") // This new path will load reports.html
    public String showEventRegistrationsReport(@PathVariable String eventId, Model model) {
        // Fetch the specific event details to display its name as a title on the report page
        Optional<event> eventOptional = eventRepo.findById(eventId);
        if (eventOptional.isEmpty()) {
            // Handle case where event is not found (e.g., redirect to admin dashboard with an error)
            return "redirect:/admin?error=EventNotFound";
        }
        event targetEvent = eventOptional.get();
        model.addAttribute("eventName", targetEvent.getEventName()); // Pass event name to template

        // Fetch all users who have registered for this specific event
        List<user> registeredUsers = userRepo.findByRegisteredEvent_EventID(eventId);

        // Filter out only the specific event details for each user's registeredEvent list
        // This is important because a user might have multiple events, but we only want to show
        // the details for the event whose report we are viewing.
        List<Object[]> reportData = new ArrayList<>();
        for (user user : registeredUsers) {
            // Find the specific event within the user's registered events
            Optional<event> matchingEvent = user.getRegisteredEvent().stream()
                    .filter(e -> e.getEventID().equals(eventId))
                    .findFirst();

            if (matchingEvent.isPresent()) {
                event specificEvent = matchingEvent.get();
                reportData.add(new Object[]{
                        user.getUserID(),
                        user.getUserName(),
                        user.getUserMail(),
                        user.getUserCollege(),
                        user.getUTR(),
                        specificEvent.getDOE(), // Using LocalDate directly
                        specificEvent.getTOE(),
                        specificEvent.getEventLoc()
                });
            }
        }

        model.addAttribute("registeredUsers", reportData); // Pass the prepared data to the template
        return "reports"; // Thymeleaf template: reports.html
    }
}
