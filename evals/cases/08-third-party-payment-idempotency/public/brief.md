# Third-Party Payment Idempotency

An agent pays suppliers through a third-party payments API.

Requirements:

- The same invoice must never be paid twice.
- The provider accepts an idempotency key.
- PostgreSQL is available as the payment ledger.

Design the payment architecture, submission flow, and recovery behavior.
