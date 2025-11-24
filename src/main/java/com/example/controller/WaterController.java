package com.example.demo.controller;

import com.example.demo.model.WaterMeasurement;
import com.example.demo.service.WaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/flujo")
@CrossOrigin(origins = "*")
public class WaterController {

    @Autowired
    private WaterService waterService;

    // ✅ Endpoint para recibir datos del Arduino
    @PostMapping
    public String recibirFlujo(@RequestBody Map<String, Object> data) {
        try {
            double valor = Double.parseDouble(data.get("valor").toString());
            WaterMeasurement medicion = new WaterMeasurement(valor, System.currentTimeMillis());
            return waterService.guardarMedicion(medicion);
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error al guardar la medición";
        }
    }

    // ✅ Endpoint para que tu frontend consulte los datos
    @GetMapping("/datos")
    public List<WaterMeasurement> obtenerDatos() throws ExecutionException, InterruptedException {
        return waterService.obtenerMediciones();
    }
}
