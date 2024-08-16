package com.example.jarvis.entity;

import com.example.jarvis.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"user\"")
@Schema(description = "users entity description!")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String kid;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;

    transient private List<Account> account;


   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "address_id")
    private Address address;

   @OneToMany(cascade = CascadeType.ALL)
   @JoinColumn(name = "user_id")
    private Set<Department> department;

   @ManyToMany(cascade = CascadeType.ALL)
   @JoinTable(
           name = "user_language",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "language_id")
   )
   private Set<Language> language;
}
