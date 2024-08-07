package com.saga.courier.infra.repository;

import com.saga.courier.domain.model.Package;
import com.saga.courier.domain.model.enums.Courier;
import com.saga.courier.domain.out.CourierRepositoryApi;
import com.saga.courier.infra.mapper.CourierEntityMapper;
import com.saga.courier.infra.mapper.PackageEntityMapper;
import com.saga.courier.infra.model.CourierEntity;
import com.saga.courier.infra.model.PackageEntity;
import com.saga.courier.infra.repository.jpa.PackageEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourierRepository implements CourierRepositoryApi {

    private final PackageEntityRepository packageEntityRepository;
    private final PackageEntityMapper packageEntityMapper;
    private final CourierEntityMapper courierEntityMapper;

    @Override
    public Package upsertPackage(Package shipment) {
        PackageEntity packageEntity = packageEntityMapper.toEntity(shipment);
        packageEntity = packageEntityRepository.save(packageEntity);
        return packageEntityMapper.toDomain(packageEntity);
    }

    @Override
    public void assignCourier(Package shipment, Courier courier) {
        PackageEntity packageEntity = packageEntityMapper.toEntity(shipment);
        CourierEntity courierEntity = courierEntityMapper.toEntity(courier);
        packageEntity.setCourier(courierEntity);
        packageEntity.setCourierAssignedAt(LocalDateTime.now());
        packageEntityRepository.save(packageEntity);
    }

    @Override
    public List<Package> findPackagesOfCourier(Integer courierId) {
        return packageEntityMapper.toDomain(packageEntityRepository.findByCourierId(courierId));
    }

    @Override
    public Optional<Package> findPackageById(String packageId) {
        return packageEntityRepository.findById(packageId).map(packageEntityMapper::toDomain);
    }

    @Override
    public Optional<Package> findByShipmentId(Integer shipmentId) {
        return packageEntityRepository.findByShipmentId(shipmentId).map(packageEntityMapper::toDomain);
    }
}
