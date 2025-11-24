package com.example.demo.service;

import com.example.demo.model.WaterMeasurement;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class WaterService {

    private static final String COLLECTION_NAME = "mediciones";

    // ✅ Guardar un nuevo valor
    public String guardarMedicion(WaterMeasurement medicion) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document();
        medicion.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(medicion);
        return "Guardado con éxito: " + result.get().getUpdateTime();
    }

    // ✅ Obtener todas las mediciones
    public List<WaterMeasurement> obtenerMediciones() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get();
        List<QueryDocumentSnapshot> documentos = query.get().getDocuments();

        List<WaterMeasurement> lista = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documentos) {
            lista.add(doc.toObject(WaterMeasurement.class));
        }
        return lista;
    }
    
    // 🔒 NUEVO MÉTODO: Obtener mediciones filtradas por un día específico (para ADMIN)
    public List<WaterMeasurement> obtenerMedicionesPorFecha(String dateString) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        
        // Convertir la fecha string (ej: "2023-10-26") a rangos de timestamp (inicio y fin del día)
        LocalDate date = LocalDate.parse(dateString);
        
        // Inicio del día (00:00:00)
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
        long startTimestamp = startOfDay.toInstant().toEpochMilli();
        
        // Fin del día (23:59:59.999)
        ZonedDateTime endOfDay = date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusNanos(1);
        long endTimestamp = endOfDay.toInstant().toEpochMilli();

        // Construir la consulta de Firestore
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
                .whereLessThan("timestamp", endTimestamp) // Usar < para evitar que se escape al siguiente día
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get();
        
        List<QueryDocumentSnapshot> documentos = query.get().getDocuments();

        List<WaterMeasurement> lista = new ArrayList<>();
        for (QueryDocumentSnapshot doc : documentos) {
            lista.add(doc.toObject(WaterMeasurement.class));
        }
        return lista;
    }
}