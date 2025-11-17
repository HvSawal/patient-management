package com.pcrypto.patientservice.kafka;


import com.pcrypto.patientservice.model.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String PATIENT_TOPIC = "patient";
    private static final String PATIENT_CREATED_EVENT_TYPE = "PATIENT_CREATED";
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendEvent(Patient patient) {

        //TODO
        // Add classes for other events for sendEvent

        PatientEvent event = patientCreatedEvent(patient);
        byte[] eventBytes = event.toByteArray();
        String key = patient.getId().toString(); // Use patient ID as key

        log.info("🔄 Sending PatientCreated event: [PatientId={}, Key={}, Size={} bytes]",
                event.getPatientId(), key, eventBytes.length);

        try {
            kafkaTemplate.send(PATIENT_TOPIC, event.toByteArray());
            log.info("✅ Message sent successfully: [PatientId={}]",
                    event.getPatientId());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event : {}", event);
        }
    }

    private PatientEvent patientCreatedEvent(Patient patient) {

        return PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getFirstName() + " " + patient.getLastName())
                .setEmail(patient.getEmail())
                .setEventType(PATIENT_CREATED_EVENT_TYPE)
                .build();
    }

}
