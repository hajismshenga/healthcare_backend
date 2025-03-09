package com.healthcare.controller;

import com.healthcare.model.Laboratory;
import com.healthcare.service.LaboratoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/laboratories")
public class LaboratoryController {

    @Autowired
    private LaboratoryService laboratoryService;

    @GetMapping
    public List<Laboratory> getAllLaboratories() {
        return laboratoryService.getAllLaboratories();
    }

    @GetMapping("/{id}")
    public Optional<Laboratory> getLaboratoryById(@PathVariable Long id) {
        return laboratoryService.getLaboratoryById(id);
    }

    @PostMapping
    public Laboratory addLaboratory(@RequestBody Laboratory laboratory) {
        return laboratoryService.addLaboratory(laboratory);
    }

    @DeleteMapping("/{id}")
    public void deleteLaboratory(@PathVariable Long id) {
        laboratoryService.deleteLaboratory(id);
    }
}
