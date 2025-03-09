package com.healthcare.controller;

import com.healthcare.model.Appointment;
import com.healthcare.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Create an appointment with reason and appointmentTime as query parameters
    @PostMapping("/{doctorId}/{patientId}")
    public Appointment createAppointment(@PathVariable Long doctorId,
                                         @PathVariable Long patientId,
                                         @RequestParam String reason,
                                         @RequestParam String appointmentTime) {
        return appointmentService.createAppointment(doctorId, patientId, reason, appointmentTime);
    }
}
