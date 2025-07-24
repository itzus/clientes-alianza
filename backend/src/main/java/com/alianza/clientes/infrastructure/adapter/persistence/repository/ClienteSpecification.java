package com.alianza.clientes.infrastructure.adapter.persistence.repository;

import com.alianza.clientes.domain.model.ClienteFilter;
import com.alianza.clientes.infrastructure.adapter.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para crear especificaciones JPA dinámicas para consultas de clientes.
 * Permite construir consultas flexibles basadas en filtros.
 */
public class ClienteSpecification {

    /**
     * Construye una especificación JPA basada en los filtros proporcionados
     * 
     * @param filter Filtros a aplicar en la consulta
     * @return Specification para la consulta JPA
     */
    public static Specification<ClienteEntity> buildSpecification(ClienteFilter filter) {
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