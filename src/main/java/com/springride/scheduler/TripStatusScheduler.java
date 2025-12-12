package com.springride.scheduler;

import com.springride.model.Trip;
import com.springride.model.enums.TripStatus;
import com.springride.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripStatusScheduler {

    private final TripRepository tripRepository;

    // S'exécute toutes les minutes pour vérifier les trajets expirés
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateExpiredTrips() {
        log.info("Checking for expired trips...");
        List<Trip> expiredTrips = tripRepository.findExpiredTrips(LocalDateTime.now());

        for (Trip trip : expiredTrips) {
            log.info("Trip {} marked as EXPIRED", trip.getId());
            trip.setStatus(TripStatus.EXPIRE);
        }

        if (!expiredTrips.isEmpty()) {
            tripRepository.saveAll(expiredTrips);
        }
    }
}
