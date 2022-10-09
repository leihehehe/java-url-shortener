package com.leih.url.link.dao;

import com.leih.url.link.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link,Long> {
}
