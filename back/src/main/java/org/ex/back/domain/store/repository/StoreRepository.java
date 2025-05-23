package org.ex.back.domain.store.repository;

import org.ex.back.domain.store.model.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<StoreEntity, Integer> {

    //orderEntity에서 store_pk 찾기
    Optional<StoreEntity> findById(Integer id);
}