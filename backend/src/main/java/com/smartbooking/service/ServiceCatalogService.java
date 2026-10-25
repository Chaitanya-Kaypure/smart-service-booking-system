package com.smartbooking.service;

import com.smartbooking.dto.service.ServiceCreateRequest;
import com.smartbooking.dto.service.ServiceResponse;
import com.smartbooking.dto.service.ServiceStatusRequest;
import com.smartbooking.dto.service.ServiceUpdateRequest;
import com.smartbooking.entity.ServiceEntity;
import com.smartbooking.exception.ResourceNotFoundException;
import com.smartbooking.mapper.ServiceMapper;
import com.smartbooking.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional(readOnly = true)
    public Page<ServiceResponse> listPublicServices(String search, String category, Pageable pageable) {
        return serviceRepository.searchActive(search, category, pageable).map(serviceMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ServiceResponse getService(Long id) {
        return serviceMapper.toResponse(getServiceEntity(id));
    }

    @Transactional(readOnly = true)
    public ServiceResponse getActiveService(Long id) {
        ServiceEntity service = getServiceEntity(id);
        if (!service.isActive()) {
            throw new ResourceNotFoundException("Service not found");
        }
        return serviceMapper.toResponse(service);
    }

    @Transactional(readOnly = true)
    public List<String> listCategories() {
        return serviceRepository.findDistinctActiveCategories();
    }

    @Transactional(readOnly = true)
    public Page<ServiceResponse> listAllServices(Pageable pageable) {
        return serviceRepository.findAll(pageable).map(serviceMapper::toResponse);
    }

    @Transactional
    public ServiceResponse createService(ServiceCreateRequest request) {
        ServiceEntity service = ServiceEntity.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .category(request.getCategory().trim())
                .price(request.getPrice())
                .durationMinutes(request.getDurationMinutes())
                .active(true)
                .build();
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponse updateService(Long id, ServiceUpdateRequest request) {
        ServiceEntity service = getServiceEntity(id);
        service.setName(request.getName().trim());
        service.setDescription(request.getDescription());
        service.setCategory(request.getCategory().trim());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponse updateStatus(Long id, ServiceStatusRequest request) {
        ServiceEntity service = getServiceEntity(id);
        service.setActive(Boolean.TRUE.equals(request.getActive()));
        return serviceMapper.toResponse(serviceRepository.save(service));
    }

    ServiceEntity getServiceEntity(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }
}
