package com.system.internship.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @ManyToOne
  @JoinColumn(name = "sent_by_id", nullable = false)
  private Account sentBy;

  @ManyToOne
  @JoinColumn(name = "sent_to_id", nullable = true)
  private Account sentTo;

  @Column(name = "content", length = 9000)
  private String content;

  @Column(name = "status", nullable = true, length = 1000)
  private String status;

}
