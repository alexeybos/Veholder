package org.skillsmart.veholder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.skillsmart.veholder.entity.Enterprise;
import org.skillsmart.veholder.entity.dto.DashboardData;
import org.skillsmart.veholder.entity.dto.EnterpriseDto;
import org.skillsmart.veholder.repository.DriverRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
import org.skillsmart.veholder.repository.VehicleTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DashboardService {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private final ObjectMapper objectMapper;

    private final Scheduler dbScheduler = Schedulers.from(Executors.newFixedThreadPool(10));

    public DashboardService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Single<DashboardData> getEnterpriseDashboard(Long enterpriseId) {
        return Observable.zip(
                        getEnterpriseInfo(enterpriseId),
                        getVehicleStats(enterpriseId),
                        getDriverStats(enterpriseId),
                        this::combineDashboardData
                )
                .subscribeOn(dbScheduler)
                .timeout(30, TimeUnit.SECONDS)
                .doOnError(error -> log.error("Error getting dashboard for enterprise {}", enterpriseId, error))
                .onErrorResumeNext(error -> {
                    log.info("Return empty dashboard for enterprise {}", enterpriseId);
                    return Observable.just(createEmptyDashboard());
                })
                .firstOrError();
    }

    private DashboardData createEmptyDashboard() {
        return new DashboardData(
                "no data", 0, 0
        );
    }

    private Observable<String> getEnterpriseInfo(Long enterpriseId) {
        return Observable.fromCallable(() -> {
                    JsonNode enterprise;
                        String result = enterpriseRepository.getFullEnterpriseInfoById(enterpriseId);
                        enterprise = objectMapper.readTree(result);
                    return enterprise.get("name").asText();
        })
                .subscribeOn(dbScheduler)
                .onErrorReturn(error -> "no data");
    }

    private Observable<Integer> getVehicleStats(Long enterpriseId) {
        return Observable.fromCallable(() -> {
                    JsonNode enterprise;
                    String result = enterpriseRepository.getFullEnterpriseInfoById(enterpriseId);
                    enterprise = objectMapper.readTree(result);
                    return enterprise.get("drivers").size();
                })
                .subscribeOn(dbScheduler)
                .onErrorReturn(error -> 0);
    }

    private Observable<Integer> getDriverStats(Long enterpriseId) {
        return Observable.fromCallable(() -> {
                    JsonNode enterprise;
                    String result = enterpriseRepository.getFullEnterpriseInfoById(enterpriseId);
                    enterprise = objectMapper.readTree(result);
                    return enterprise.get("vehicles").size();
                })
                .subscribeOn(dbScheduler)
                .onErrorReturn(error -> 0);
    }

    private DashboardData combineDashboardData(String enterprise, Integer vehicleCnt, Integer driversCnt) {
        return new DashboardData(enterprise, vehicleCnt, driversCnt);
    }
}
