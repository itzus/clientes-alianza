package com.alianza.clientes.infrastructure.adapter.persistence.mapper;

import org.springframework.data.domain.Sort;

public final class CommonMapper {

    private CommonMapper() {
    }

    /**
     * Crea un objeto Sort basado en el campo y dirección proporcionados
     * 
     * @param sortBy  Campo por el cual ordenar
     * @param sortDir Dirección de ordenamiento (asc o desc)
     * @return Objeto Sort configurado
     */
    public static Sort createSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

}
