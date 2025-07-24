package com.alianza.clientes.repository;

import com.alianza.clientes.model.Cliente;
import com.alianza.clientes.model.dto.ClienteFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ClienteSpecification {

    public static Specification<Cliente> buildSpecification(ClienteFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getSharedKey())) {
                predicates.add(criteriaBuilder.equal(root.get("sharedKey"), filter.getSharedKey()));
            }

            if (StringUtils.hasText(filter.getNombre())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        "%" + filter.getNombre().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(filter.getTelefono())) {
                predicates.add(criteriaBuilder.like(
                        root.get("telefono"),
                        "%" + filter.getTelefono() + "%"));
            }

            if (StringUtils.hasText(filter.getEmail())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getFechaInicio() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("fechaInicio"), filter.getFechaInicio()));
            }

            if (filter.getFechaFin() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("fechaFin"), filter.getFechaFin()));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}