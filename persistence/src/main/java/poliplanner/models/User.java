package poliplanner.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // NOTE: solo se usa para el formulario de registro
    @Transient
    private String confirmedPassword;

    // NOTE: requerido para que spring-security no se queje
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    // Para recuperación de contraseñas
    @Column(name = "email", nullable = true, unique = true)
    private String email;

    // Token hasheado para mayor seguridad
    @Column(name = "recovery_token_hash")
    private String recoveryTokenHash;

    // Fecha/hora de expiración del token
    @Column(name = "recovery_token_expiration")
    private LocalDateTime recoveryTokenExpiration;

    // Para marcar si ya fue utilizado
    @Column(name = "recovery_token_used")
    private boolean recoveryTokenUsed = false;

    // Constructor
    public User(String name, String password) {
        this.username = name;
        this.password = password;
    }

    // toString para debug
    @Override
    public String toString() {
        return "User{"
                + "id="
                + id
                + ", username='"
                + username
                + '\''
                + ", password='"
                + password
                + '\''
                + '}';
    }
}
