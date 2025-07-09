package com.sandeshsudake.mesa.controller;

import com.sandeshsudake.mesa.entity.admin;
import com.sandeshsudake.mesa.entity.event;
import com.sandeshsudake.mesa.entity.frf;
import com.sandeshsudake.mesa.repository.eventRepo;
import com.sandeshsudake.mesa.repository.userRepo;
import com.sandeshsudake.mesa.repository.adminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sandeshsudake.mesa.repository.frfRepo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class adminController {

    @Autowired
    private adminRepo adminRepo;

    @Autowired
    private eventRepo eventRepo;

    @Autowired
    private frfRepo frfRepo;

    @Autowired
    private userRepo userRepo;

    @GetMapping("/admin/")
    public String homePage(Model model) {
        List<event> eventList = eventRepo.findAll();
        model.addAttribute("eventList", eventList);

        List<frf> allFRF = frfRepo.findAll();
        model.addAttribute("allFRF",allFRF);

        return "admin"; // Thymeleaf template: admin.html
    }

    @PostMapping("/admin/addEvent/")
    public String addEvent(event event,
                           @RequestParam("file") MultipartFile file,
                           Model model) throws IOException {

        // IMPORTANT: Ensure eventID is null to trigger MongoDB's auto-generation
        // This is crucial for new events.
        // If your form has a hidden input for eventID with value="", this line
        // will correctly set it to null, allowing MongoDB to generate a proper _id.
        if (event.getEventID() != null && event.getEventID().isEmpty()) {
            event.setEventID(null);
        }
        // Alternatively, if this endpoint is *only* for adding new events
        // and never for updating existing ones, you can simply force it to null:
        // event.setEventID(null);


        event.setEventIMG(file.getBytes());
        event.setContentType(file.getContentType());
        eventRepo.save(event); // Now this should trigger ID generation if eventID was null
        return "redirect:/admin/"; // back to dashboard
    }

    // New endpoint to serve images
    @GetMapping("/eventImage/{id}")
    public ResponseEntity<byte[]> getEventImage(@PathVariable String id) {
        return eventRepo.findById(id) // Retrieve event by ID
                .map(event -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(event.getContentType())) // Set content type
                        .body(event.getEventIMG())) // Return image bytes
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    @GetMapping("/event/delete/{id}/")
    public String deleteEvent(@PathVariable String id){
        // Fix: Correctly use the 'id' directly from the path variable
        eventRepo.deleteById(id);
        return "redirect:/admin/"; // back to dashboard
    }





    @PostMapping("/addAdmin")
    public String addAdmin(admin admin) {

        // Optional: If adminId is empty string, set to null (for Mongo to auto-generate)
        if (admin.getAdminId() != null && admin.getAdminId().isEmpty()) {
            admin.setAdminId(null);
        }

        // 1. Create encoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 2. Get plain text password from form
        String plainPWD = admin.getAdminPassword();

        // 3. Encode password
        String encodedPWD = passwordEncoder.encode(plainPWD);

        // ❗️4. Set encoded password back to the object
        admin.setAdminPassword(encodedPWD);

        // 5. Save to DB
        adminRepo.save(admin);

        return "redirect:/admin/";
    }

}