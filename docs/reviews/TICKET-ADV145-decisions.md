# TICKET-ADV145 — Kafka Consumer Config Review Decisions

| # | Area          | Finding                                                  | Recommendation              | Decision | Rationale                                          |
|---|---------------|----------------------------------------------------------|-----------------------------|----------|----------------------------------------------------|
| 1 | Backpressure  | max.poll.records default 500 risks max.poll.interval.ms  | Set to 100                  | Accept   | Slow downstream — keeps poll loop responsive       |
| 2 | Error handling| ExponentialBackOff has no jitter                         | Add jitter (custom BackOff) | Defer    | Logged as backlog item; out of scope today         |
| 3 | Idempotence   | Producer enable.idempotence not asserted                 | enable.idempotence: true    | Accept   | Cheap insurance against duplicate sends            |
| 4 | Observability | Metrics tags missing spring.application.name             | Add tags.application        | Accept   | Prevents collisions across services in Prometheus  |
| 5 | Security      | bootstrap-servers is PLAINTEXT                           | Use SASL_SSL in prod        | Reject   | Known dev gap; tracked separately for Day 10       |
