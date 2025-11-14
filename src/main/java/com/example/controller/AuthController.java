package com.example.demo.controller;

import com.example.demo.service.FirebaseAuthService;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // permite llamadas desde tu frontend
public class AuthController {

    @Autowired
    private FirebaseAuthService authService;

    // Registro
    @PostMapping("/register")
public String register(@RequestParam String email, @RequestParam String password) {
    try {
        String uid = authService.registrarUsuario(email, password);
        return "Usuario registrado con UID: " + uid;
    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }
}
 // --- Endpoints existentes para autenticación aquí ---

    // Recibir datos del Arduino
    // @PostMapping("/updateWaterData")
    // public String receiveWaterData(@RequestBody WaterMeasurement measurement) {
    //     authService.saveWaterMeasurement(measurement);
    //     System.out.println("Datos recibidos: Flow=" + measurement.getFlowRate() +
    //                        " L/min | Total Litros=" + measurement.getTotalLiters());
    //     return "ok";
    // }

    // Obtener todas las mediciones
    // @GetMapping("/waterMeasurements")
    // public List<WaterMeasurement> getWaterMeasurements() {
    //     return authService.getAllWaterMeasurements();
    // }

    // Login
   // @PostMapping("/login")
    //public String login(@RequestParam String email) {
       // try {
         //   UserRecord user = authService.loginUsuario(email);
       //     return "Usuario autenticado: " + user.getEmail();
       // } catch (Exception e) {
        //    return "Error: " + e.getMessage();
       // }
    //}
   

}
