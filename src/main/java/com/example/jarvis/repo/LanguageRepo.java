package com.example.jarvis.repo;

import com.example.jarvis.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepo extends JpaRepository<Language,Long> {
}
