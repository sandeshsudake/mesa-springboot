package com.sandeshsudake.mesa.controller;

import com.sandeshsudake.mesa.entity.frf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.sandeshsudake.mesa.repository.frfRepo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class frfController {

    @Autowired
    private frfRepo frfRepo;


    private frf frf;
//
//    @GetMapping("/allfrf")
//    public String showFrf(Model model, frf frf){
//
//        List<frf> allFRF = frfRepo.findAll();
//        model.addAttribute("allFRF",allFRF);
//        return "admin";
//
//    }

    @PostMapping("/addFRF")
    public String addFrf(frf frf){

        // *** ADD THIS LOGIC TO ENSURE ID IS NULL FOR NEW ENTRIES ***
        if (frf.getFrfID() != null && frf.getFrfID().isEmpty()) {
            frf.setFrfID(null); // Set to null so MongoDB auto-generates the _id
        }
        frfRepo.save(frf);
        return "redirect:/home/";
    }
}
