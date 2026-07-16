# Appointment Booking

Design a small appointment-booking application for one physical shop. One developer owns delivery and operations. Staff create availability and customers book, reschedule, or cancel appointments. A time slot must not be booked twice, including when two requests race. Staff may manage schedules; customers may manage only their own bookings. Authentication is required and authorization must hold outside the UI.

The shop has low traffic and one operating location. Appointment records must survive process restarts. A confirmation notification is useful but a mail-provider outage must not create a false booking or lose the committed appointment. The product has no stated multi-tenant, regulatory, geographic, or high-throughput requirement. Keep the design proportional to this context and identify unknowns that could change it.
