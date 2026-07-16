# High-Throughput Event Ingestion

Design an ingestion system with verified sustained volume of 250,000 events per second and bursts to 1,000,000 events per second for fifteen minutes. Accepted events must be durable before acknowledgement, replayable for downstream consumers, and recoverable after consumer failure. Producers need backpressure or an explicit overload response. Events have a stable source key; per-key order is required, global order is not. Duplicate delivery is possible.

The product has an operational team able to run a durable log or queue and independently scale consumers. Poison events must not stop unrelated work. Explain partitioning, batching, idempotency, retention, monitoring, recovery, and why each operational component is justified. Do not add unrelated services or data stores merely for diagram completeness.
