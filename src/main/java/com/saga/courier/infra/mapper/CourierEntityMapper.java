package package com.saga.courier.infra.mapper;

import com.saga.courier.domain.model.Package;
import com.saga.courier.domain.model.Product;
import com.saga.courier.domain.model.enums.ShipmentDomainStatus;
import com.saga.courier.infra.model.MerchantProductEntity;
import com.saga.courier.infra.model.PackageEntity;
import com.saga.courier.infra.model.enums.ShipmentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = CourierEntityMapper.class)
public interface PackageEntityMapper {

    ShipmentStatus toEntity(ShipmentDomainStatus status);

    @Mapping(target = "courier", ignore = true)
    @Mapping(target = "shipmentStatus", source = "status")
    @Mapping(target = "courierAssignedAt", ignore = true)
    PackageEntity toEntity(Package shipment);

    @Mapping(target = "status", source = "shipmentStatus")
    Package toDomain(PackageEntity packageEntity);

    Product toDomain(MerchantProductEntity productEntity);

    @Mapping(target = "description", ignore = true)
    @Mapping(target = "pickupAddress", ignore = true)
    @Mapping(target = "returnAddress", ignore = true)
    @Mapping(target = "stockLevel", ignore = true)
    @Mapping(target = "reservedLevel", ignore = true)
    MerchantProductEntity toEntity(Product product);
}
com.saga.courier.infra.mapper;

import com.saga.courier.domain.model.enums.Courier;
import com.saga.courier.infra.model.CourierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CourierEntityMapper {

    @Mapping(target = "id", expression = "java(courier.getId())")
    @Mapping(target = "name", expression = "java(courier.getName())")
    CourierEntity toEntity(Courier courier);

    default Courier toDomain(CourierEntity courier) {
        if (courier == null) {
            return null;
        }
        if (courier.getId() == 1) {
            return Courier.ONE_MAN_DELIVERY;
        } else {
            return Courier.TWO_MEN_DELIVERY;
        }
    }
}
