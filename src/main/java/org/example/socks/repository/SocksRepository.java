package org.example.socks.repository;

import org.example.socks.model.Socks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface SocksRepository extends JpaRepository<Socks, Integer> {
Socks findById(Long id);

}