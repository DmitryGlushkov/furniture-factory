package com.factory.repository;

import com.factory.models.FurnitureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypesRepository extends JpaRepository<FurnitureType, Long> {
}
