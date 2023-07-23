package io.github.calvary.erp;


/**
 * This service responds to processing events on the requested entity
 */
public interface PostingProcessorService<T> {

    /**
     * Processing transaction item T
     *
     * @param dto
     * @return
     */
    T post(T dto);
}
