package poliplanner.models.metadata;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects_metadata", indexes = @Index(columnList = "name, career_id"))
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SubjectsMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CareerMetadata career;

    @Column(name = "name")
    private String name;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "credits_percentage_required")
    private Double creditsPercentageRequired;

    @Column(name = "semester", nullable = false)
    private Integer semester = 0;

    @OneToMany(mappedBy = "class1", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SubjectsPrerrequisites> prerequisites = new ArrayList<>();

    @OneToMany(mappedBy = "class2", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SubjectsPrerrequisites> postrequisites = new ArrayList<>();
}
