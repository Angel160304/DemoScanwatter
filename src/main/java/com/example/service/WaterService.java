package com.example.demo.service;

import com.example.demo.model.WaterMeasurement;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class WaterService {

    private static final String COLLECTION_NAME = "mediciones";

    // Guardar una medición
    public String guardarMedicion(WaterMeasurement medicion) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document();
        medicion.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(medicion);
        return "Guardado con éxito: " + result.get().getUpdateTime();
    }

    // Obtener todas las mediciones
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
}
