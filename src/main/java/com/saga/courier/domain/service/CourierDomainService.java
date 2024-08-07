package com.saga.courier.domain.service;

import com.saga.courier.domain.in.CourierDomainServiceApi;
import com.saga.courier.domain.model.Package;
import com.saga.courier.domain.model.enums.Courier;
import com.saga.courier.domain.model.enums.ShipmentDomainStatus;
import com.saga.courier.domain.out.CourierRepositoryApi;
import com.saga.courier.domain.out.ShipmentProducerApi;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CourierDomainService implements CourierDomainServiceApi {

    private final CourierRepositoryApi courierRepositoryApi;
    private final ShipmentProducerApi shipmentProducerApi;

    @Override
    public void upsert(Package shipment) {
        Optional<Package> maybePackage = courierRepositoryApi.findByShipmentId(shipment.shipmentId());
        if (maybePackage.isEmpty()) {
            assignCourierToShipment(shipment);
            return;
        }
        Package aPackage = maybePackage.get();
        if (isCourierAssignedMoreThan(aPackage, ChronoUnit.MINUTES, 2)) {
            // reassign courier
            assignCourierToShipment(aPackage);
        }
        aPackage = aPackage.updateStatus(shipment.status());
        courierRepositoryApi.upsertPackage(aPackage);
    }

    @Override
    public List<Package> getPackagesForCourier(Integer courierId) {
        return courierRepositoryApi.findPackagesOfCourier(courierId);
    }

    @Override
    public boolean updateStatus(String packageId, ShipmentDomainStatus status) {
        Optional<Package> maybePackage = courierRepositoryApi.findPackageById(packageId);
        if (maybePackage.isEmpty()) {
            return false;
        }
        Package aPackage = maybePackage.get();
        aPackage = aPackage.updateStatus(status);
        courierRepositoryApi.upsertPackage(aPackage);
        shipmentProducerApi.updateShipment(aPackage);
        return true;
    }

    private void assignCourierToShipment(Package shipment) {
        Package aPackage = courierRepositoryApi.upsertPackage(shipment);
        Courier courier;
        if (aPackage.product().bulky()) {
            courier = assignOneManDeliveryCourier();
        } else {
            courier = assignTwoManDeliveryCourier();
        }
        courierRepositoryApi.assignCourier(aPackage, courier);
    }

    private Courier assignOneManDeliveryCourier() {
        return Courier.ONE_MAN_DELIVERY;
    }

    private Courier assignTwoManDeliveryCourier() {
        return Courier.TWO_MEN_DELIVERY;
    }

    private boolean isCourierAssignedMoreThan(Package pack, ChronoUnit unit, Integer amount) {
        return pack.courier() != null &&
                pack.status().equals(ShipmentDomainStatus.IN_DELIVERY) &&
                unit.between(pack.courierAssignedAt(), LocalDateTime.now()) >= amount;
    }
}
