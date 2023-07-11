package ru.practicum.ewm.main.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.service.RequestStatus;
import ru.practicum.ewm.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
//@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public ParticipationRequest(User requester, Event event, LocalDateTime created, RequestStatus status) {
        this.requester = requester;
        this.event = event;
        this.created = created;
        this.status = status;
    }
}
