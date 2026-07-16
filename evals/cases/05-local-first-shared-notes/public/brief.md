# Local-First Shared Notes

Design a notes application used across several devices. Users must read and edit locally while offline. Edits synchronize when connectivity returns. Two devices may edit the same note during a partition, and users need predictable conflict semantics rather than silent last-writer data loss. Identity, authorization, local ownership, synchronization, and eventual reconciliation matter.

The product does not prescribe a conflict algorithm, cloud provider, or UI framework. State what is authoritative, how operations or versions are represented, how deletes and retries behave, and which guarantees are deliberately weaker during a partition. Keep the design proportional but do not pretend this is an ordinary single-server CRUD application.
